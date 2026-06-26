package cn.aksu.supervision.ocr.service;

import cn.aksu.supervision.common.BusinessException;
import cn.hutool.http.HttpUtil;
import cn.hutool.json.JSONObject;
import cn.hutool.json.JSONUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;

@Slf4j
@Service
public class OcrService {

    @Value("${baidu.ocr.api-key:}")
    private String apiKey;

    @Value("${baidu.ocr.secret-key:}")
    private String secretKey;

    private static final String TOKEN_URL = "https://aip.baidubce.com/oauth/2.0/token";
    private static final String BUSINESS_LICENSE_URL = "https://aip.baidubce.com/rest/2.0/ocr/v1/business_license";

    public Map<String, Object> recognize(String imageBase64, String type) {
        String accessToken = getAccessToken();

        Map<String, Object> result = new HashMap<>();

        if ("business_license".equals(type)) {
            result = recognizeBusinessLicense(imageBase64, accessToken);
        } else {
            throw new BusinessException("不支持的识别类型: " + type);
        }

        return result;
    }

    private Map<String, Object> recognizeBusinessLicense(String imageBase64, String accessToken) {
        try {
            String url = BUSINESS_LICENSE_URL + "?access_token=" + accessToken;

            Map<String, Object> params = new HashMap<>();
            params.put("image", imageBase64);

            String response = HttpUtil.post(url, params);
            JSONObject json = JSONUtil.parseObj(response);

            if (json.containsKey("error_code")) {
                throw new BusinessException("OCR识别失败: " + json.getStr("error_msg"));
            }

            Map<String, Object> result = new HashMap<>();
            result.put("words_result", json.get("words_result"));
            result.put("words_result_num", json.get("words_result_num"));
            result.put("log_id", json.get("log_id"));
            return result;
        } catch (BusinessException e) {
            throw e;
        } catch (Exception e) {
            log.error("OCR识别异常", e);
            throw new BusinessException("OCR识别异常: " + e.getMessage());
        }
    }

    private String getAccessToken() {
        try {
            Map<String, Object> params = new HashMap<>();
            params.put("grant_type", "client_credentials");
            params.put("client_id", apiKey);
            params.put("client_secret", secretKey);

            String response = HttpUtil.post(TOKEN_URL, params);
            JSONObject json = JSONUtil.parseObj(response);
            return json.getStr("access_token");
        } catch (Exception e) {
            log.error("获取百度AccessToken失败", e);
            throw new BusinessException("获取OCR服务Token失败: " + e.getMessage());
        }
    }
}
