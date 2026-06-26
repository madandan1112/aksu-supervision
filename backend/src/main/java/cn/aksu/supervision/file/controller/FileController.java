package cn.aksu.supervision.file.controller;

import cn.aksu.supervision.common.Result;
import cn.aksu.supervision.file.service.MinioService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;

@Tag(name = "文件管理")
@RestController
@RequestMapping("/api/file")
@RequiredArgsConstructor
@SecurityRequirement(name = "bearer")
public class FileController {

    private final MinioService minioService;

    @Operation(summary = "上传文件")
    @PostMapping(value = "/upload", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public Result<Map<String, String>> uploadFile(
            @RequestParam("file") MultipartFile file,
            @RequestParam(value = "folder", defaultValue = "default") String folder) {
        String objectName = minioService.uploadFile(file, folder);
        Map<String, String> result = new HashMap<>();
        result.put("objectName", objectName);
        result.put("originalName", file.getOriginalFilename());
        result.put("size", String.valueOf(file.getSize()));
        return Result.success(result);
    }

    @Operation(summary = "下载/预览文件")
    @GetMapping("/{bucket}/{objectName}")
    public Result<String> getFileUrl(@PathVariable String bucket,
                                      @PathVariable String objectName) {
        String url = minioService.getPresignedUrl(bucket, objectName, 60);
        return Result.success(url);
    }
}
