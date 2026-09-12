package cn.aksu.supervision.ocr.service;

import cn.aksu.supervision.common.BusinessException;
import cn.hutool.http.HttpUtil;
import cn.hutool.json.JSONArray;
import cn.hutool.json.JSONObject;
import cn.hutool.json.JSONUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
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
    private static final String ACCURATE_BASIC_URL = "https://aip.baidubce.com/rest/2.0/ocr/v1/accurate_basic";
    private static final String GENERAL_BASIC_URL = "https://aip.baidubce.com/rest/2.0/ocr/v1/general_basic";

    public Map<String, Object> recognize(String imageBase64, String type) {
        String accessToken = getAccessToken();

        Map<String, Object> result = new HashMap<>();

        switch (type) {
            case "business_license":
                result = recognizeBusinessLicense(imageBase64, accessToken);
                break;
            case "accurate_basic":
                result = recognizeAccurateBasic(imageBase64, accessToken);
                break;
            case "general_basic":
                result = recognizeGeneralBasic(imageBase64, accessToken);
                break;
            default:
                throw new BusinessException("不支持的识别类型: " + type + "，支持: business_license/accurate_basic/general_basic");
        }

        return result;
    }

    /**
     * 通用文字识别（高精度版）- 用于整改反馈附件OCR
     */
    public String recognizeText(String imageBase64) {
        Map<String, Object> result = recognizeAccurateBasic(imageBase64, getAccessToken());
        return (String) result.getOrDefault("fullText", "");
    }

    /**
     * 批量识别多张图片，返回合并文本
     */
    public String recognizeImages(List<String> imageBase64List) {
        StringBuilder fullText = new StringBuilder();
        for (int i = 0; i < imageBase64List.size(); i++) {
            try {
                String text = recognizeText(imageBase64List.get(i));
                if (!text.isEmpty()) {
                    fullText.append("--- 第").append(i + 1).append("张图片 ---\n");
                    fullText.append(text).append("\n");
                }
            } catch (Exception e) {
                log.warn("OCR识别第{}张图片失败: {}", i + 1, e.getMessage());
                fullText.append("--- 第").append(i + 1).append("张图片识别失败 ---\n");
            }
        }
        return fullText.toString();
    }

    private Map<String, Object> recognizeAccurateBasic(String imageBase64, String accessToken) {
        try {
            String url = ACCURATE_BASIC_URL + "?access_token=" + accessToken;

            Map<String, Object> params = new HashMap<>();
            params.put("image", imageBase64);
            params.put("detect_direction", "true");
            params.put("paragraph", "true");

            String response = HttpUtil.post(url, params);
            JSONObject json = JSONUtil.parseObj(response);

            if (json.containsKey("error_code")) {
                throw new BusinessException("OCR识别失败: " + json.getStr("error_msg"));
            }

            // 提取文字内容
            StringBuilder fullText = new StringBuilder();
            JSONArray wordsResult = json.getJSONArray("words_result");
            List<String> lines = new ArrayList<>();
            if (wordsResult != null) {
                for (int i = 0; i < wordsResult.size(); i++) {
                    String words = wordsResult.getJSONObject(i).getStr("words");
                    lines.add(words);
                    fullText.append(words).append("\n");
                }
            }

            Map<String, Object> result = new HashMap<>();
            result.put("words_result", wordsResult);
            result.put("words_result_num", json.get("words_result_num"));
            result.put("lines", lines);
            result.put("fullText", fullText.toString().trim());
            result.put("log_id", json.get("log_id"));
            return result;
        } catch (BusinessException e) {
            throw e;
        } catch (Exception e) {
            log.error("OCR高精度识别异常", e);
            throw new BusinessException("OCR识别异常: " + e.getMessage());
        }
    }

    private Map<String, Object> recognizeGeneralBasic(String imageBase64, String accessToken) {
        try {
            String url = GENERAL_BASIC_URL + "?access_token=" + accessToken;

            Map<String, Object> params = new HashMap<>();
            params.put("image", imageBase64);
            params.put("detect_direction", "true");

            String response = HttpUtil.post(url, params);
            JSONObject json = JSONUtil.parseObj(response);

            if (json.containsKey("error_code")) {
                throw new BusinessException("OCR识别失败: " + json.getStr("error_msg"));
            }

            StringBuilder fullText = new StringBuilder();
            JSONArray wordsResult = json.getJSONArray("words_result");
            List<String> lines = new ArrayList<>();
            if (wordsResult != null) {
                for (int i = 0; i < wordsResult.size(); i++) {
                    String words = wordsResult.getJSONObject(i).getStr("words");
                    lines.add(words);
                    fullText.append(words).append("\n");
                }
            }

            Map<String, Object> result = new HashMap<>();
            result.put("words_result", wordsResult);
            result.put("words_result_num", json.get("words_result_num"));
            result.put("lines", lines);
            result.put("fullText", fullText.toString().trim());
            result.put("log_id", json.get("log_id"));
            return result;
        } catch (BusinessException e) {
            throw e;
        } catch (Exception e) {
            log.error("OCR通用识别异常", e);
            throw new BusinessException("OCR识别异常: " + e.getMessage());
        }
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
