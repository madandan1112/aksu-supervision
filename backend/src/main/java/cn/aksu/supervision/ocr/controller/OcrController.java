package cn.aksu.supervision.ocr.controller;

import cn.aksu.supervision.common.Result;
import cn.aksu.supervision.ocr.service.OcrService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@Tag(name = "OCR识别")
@RestController
@RequestMapping("/api/ocr")
@RequiredArgsConstructor
@SecurityRequirement(name = "bearer")
public class OcrController {

    private final OcrService ocrService;

    @Operation(summary = "OCR识别")
    @PostMapping("/recognize")
    public Result<Map<String, Object>> recognize(@RequestParam String imageBase64,
                                                   @RequestParam(defaultValue = "business_license") String type) {
        return Result.success(ocrService.recognize(imageBase64, type));
    }

    @Operation(summary = "批量OCR识别（整改反馈专用）")
    @PostMapping("/recognize-batch")
    public Result<Map<String, Object>> recognizeBatch(@RequestBody Map<String, List<String>> body) {
        List<String> images = body.get("images");
        String fullText = ocrService.recognizeImages(images);
        Map<String, Object> result = Map.of(
                "fullText", fullText,
                "imageCount", images != null ? images.size() : 0
        );
        return Result.success(result);
    }
}
