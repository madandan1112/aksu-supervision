package cn.aksu.supervision.auth.captcha;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.Base64;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

/**
 * 图形验证码服务
 * 使用内存缓存（ConcurrentHashMap）存储验证码，5分钟过期
 * 如后续启用Redis，可替换为Redis实现
 */
@Slf4j
@Service
public class CaptchaService {

    private static final int WIDTH = 120;
    private static final int HEIGHT = 40;
    private static final int CODE_LENGTH = 4;
    private static final long EXPIRE_MINUTES = 5;
    private static final String CHARS = "ABCDEFGHJKLMNPQRSTUVWXYZ23456789";

    /**
     * 是否在日志中打印验证码明文。
     * 开发/联调环境开启（供自动化连通性测试从日志取码）；
     * 生产环境必须配置 captcha.log-code-enabled: false，只记key不记码。
     */
    @org.springframework.beans.factory.annotation.Value("${captcha.log-code-enabled:true}")
    private boolean logCodeEnabled;

    private final ConcurrentHashMap<String, CaptchaItem> captchaCache = new ConcurrentHashMap<>();
    private final ScheduledExecutorService scheduler = Executors.newSingleThreadScheduledExecutor();

    public CaptchaService() {
        // 每分钟清理过期验证码
        scheduler.scheduleAtFixedRate(this::cleanExpired, 1, 1, TimeUnit.MINUTES);
    }

    /**
     * 生成验证码
     * @return CaptchaResult 包含captchaKey和base64图片
     */
    public CaptchaResult generateCaptcha() {
        String key = UUID.randomUUID().toString().replace("-", "");
        String code = generateRandomCode();
        String base64Image = generateImage(code);

        captchaCache.put(key, new CaptchaItem(code, System.currentTimeMillis() + EXPIRE_MINUTES * 60 * 1000));
        if (logCodeEnabled) {
            log.info("生成验证码: key={}, code={}", key, code);
        } else {
            log.info("生成验证码: key={}", key);
        }

        return new CaptchaResult(key, "data:image/png;base64," + base64Image);
    }

    /**
     * 校验验证码
     * @param captchaKey  验证码key
     * @param captchaCode 用户输入的验证码
     * @return 是否正确
     */
    public boolean validateCaptcha(String captchaKey, String captchaCode) {
        if (captchaKey == null || captchaCode == null) {
            return false;
        }

        CaptchaItem item = captchaCache.remove(captchaKey);
        if (item == null) {
            log.debug("验证码不存在或已使用: key={}", captchaKey);
            return false;
        }

        if (System.currentTimeMillis() > item.expireTime) {
            log.debug("验证码已过期: key={}", captchaKey);
            return false;
        }

        boolean valid = item.code.equalsIgnoreCase(captchaCode);
        log.debug("验证码校验: key={}, input={}, expected={}, valid={}", captchaKey, captchaCode, item.code, valid);
        return valid;
    }

    private String generateRandomCode() {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < CODE_LENGTH; i++) {
            int index = (int) (Math.random() * CHARS.length());
            sb.append(CHARS.charAt(index));
        }
        return sb.toString();
    }

    private String generateImage(String code) {
        BufferedImage image = new BufferedImage(WIDTH, HEIGHT, BufferedImage.TYPE_INT_RGB);
        Graphics2D g = image.createGraphics();

        // 背景
        g.setColor(Color.WHITE);
        g.fillRect(0, 0, WIDTH, HEIGHT);

        // 边框
        g.setColor(new Color(200, 200, 200));
        g.drawRect(0, 0, WIDTH - 1, HEIGHT - 1);

        // 干扰线
        for (int i = 0; i < 6; i++) {
            g.setColor(randomColor(150, 200));
            g.drawLine(
                    (int) (Math.random() * WIDTH), (int) (Math.random() * HEIGHT),
                    (int) (Math.random() * WIDTH), (int) (Math.random() * HEIGHT)
            );
        }

        // 干扰点
        for (int i = 0; i < 30; i++) {
            g.setColor(randomColor(150, 200));
            g.fillOval(
                    (int) (Math.random() * WIDTH), (int) (Math.random() * HEIGHT),
                    2, 2
            );
        }

        // 文字
        g.setFont(new Font("Arial", Font.BOLD | Font.ITALIC, 28));
        int x = 10;
        for (int i = 0; i < code.length(); i++) {
            g.setColor(randomColor(50, 120));
            // 随机旋转
            double theta = (Math.random() - 0.5) * 0.4;
            g.rotate(theta, x + 12, 28);
            g.drawString(String.valueOf(code.charAt(i)), x, 30);
            g.rotate(-theta, x + 12, 28);
            x += 26;
        }

        g.dispose();

        try {
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            ImageIO.write(image, "png", baos);
            return Base64.getEncoder().encodeToString(baos.toByteArray());
        } catch (IOException e) {
            throw new RuntimeException("生成验证码图片失败", e);
        }
    }

    private Color randomColor(int min, int max) {
        int r = min + (int) (Math.random() * (max - min));
        int g = min + (int) (Math.random() * (max - min));
        int b = min + (int) (Math.random() * (max - min));
        return new Color(r, g, b);
    }

    private void cleanExpired() {
        long now = System.currentTimeMillis();
        captchaCache.entrySet().removeIf(entry -> now > entry.getValue().expireTime);
    }

    private static class CaptchaItem {
        final String code;
        final long expireTime;

        CaptchaItem(String code, long expireTime) {
            this.code = code;
            this.expireTime = expireTime;
        }
    }

    public record CaptchaResult(String captchaKey, String captchaImage) {}
}
