package cn.aksu.supervision.message.controller;

import cn.aksu.supervision.common.PageResult;
import cn.aksu.supervision.common.Result;
import cn.aksu.supervision.message.entity.Message;
import cn.aksu.supervision.message.service.MessageService;
import cn.aksu.supervision.security.JwtTokenProvider;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@Tag(name = "消息管理")
@RestController
@RequestMapping("/api/message")
@RequiredArgsConstructor
@SecurityRequirement(name = "bearer")
public class MessageController {

    private final MessageService messageService;
    private final JwtTokenProvider jwtTokenProvider;

    @Operation(summary = "消息列表")
    @GetMapping("/list")
    public Result<PageResult<Message>> listMessages(
            @RequestHeader("Authorization") String auth,
            @RequestParam(required = false) String type,
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "10") int size) {
        Long userId = jwtTokenProvider.getUserIdFromToken(auth.replace("Bearer ", ""));
        return Result.success(messageService.listMessages(userId, type, page, size));
    }

    @Operation(summary = "标记已读")
    @PutMapping("/{id}/read")
    public Result<Message> markAsRead(@RequestHeader("Authorization") String auth,
                                       @PathVariable Long id) {
        Long userId = jwtTokenProvider.getUserIdFromToken(auth.replace("Bearer ", ""));
        return Result.success(messageService.markAsRead(id, userId));
    }

    @Operation(summary = "删除消息")
    @DeleteMapping("/{id}")
    public Result<Void> deleteMessage(@RequestHeader("Authorization") String auth,
                                       @PathVariable Long id) {
        Long userId = jwtTokenProvider.getUserIdFromToken(auth.replace("Bearer ", ""));
        messageService.deleteMessage(id, userId);
        return Result.success();
    }

    @Operation(summary = "消息设置")
    @PutMapping("/settings")
    public Result<Void> updateSettings(@RequestHeader("Authorization") String auth,
                                        @RequestBody String settings) {
        Long userId = jwtTokenProvider.getUserIdFromToken(auth.replace("Bearer ", ""));
        messageService.updateSettings(userId, settings);
        return Result.success();
    }

    @Operation(summary = "获取消息设置")
    @GetMapping("/settings")
    public Result<String> getSettings(@RequestHeader("Authorization") String auth) {
        Long userId = jwtTokenProvider.getUserIdFromToken(auth.replace("Bearer ", ""));
        return Result.success(messageService.getSettings(userId));
    }
}
