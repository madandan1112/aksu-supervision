package cn.aksu.supervision.task.controller;

import cn.aksu.supervision.common.PageResult;
import cn.aksu.supervision.common.Result;
import cn.aksu.supervision.security.JwtTokenProvider;
import cn.aksu.supervision.task.dto.TaskCreateRequest;
import cn.aksu.supervision.task.dto.TaskQueryRequest;
import cn.aksu.supervision.task.entity.InspectionTask;
import cn.aksu.supervision.task.entity.TaskExtension;
import cn.aksu.supervision.task.entity.TaskInspector;
import cn.aksu.supervision.task.service.TaskService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.Map;

@Tag(name = "检查任务管理")
@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
@SecurityRequirement(name = "bearer")
public class TaskController {

    private final TaskService taskService;
    private final JwtTokenProvider jwtTokenProvider;

    @Operation(summary = "创建任务")
    @PostMapping("/admin/task")
    public Result<InspectionTask> createTask(@Valid @RequestBody TaskCreateRequest request,
                                              @RequestHeader("Authorization") String auth) {
        String username = jwtTokenProvider.getUsernameFromToken(auth.replace("Bearer ", ""));
        return Result.success(taskService.createTask(request, username));
    }

    @Operation(summary = "任务列表")
    @GetMapping("/admin/task/list")
    public Result<PageResult<InspectionTask>> listTasks(TaskQueryRequest request) {
        return Result.success(taskService.listTasks(request.getStatus(), request.getType(),
                request.getKeyword(), request.getPage(), request.getSize()));
    }

    @Operation(summary = "编辑任务")
    @PutMapping("/admin/task/{id}")
    public Result<InspectionTask> updateTask(@PathVariable Long id,
                                              @Valid @RequestBody TaskCreateRequest request) {
        return Result.success(taskService.updateTask(id, request));
    }

    @Operation(summary = "任务详情")
    @GetMapping("/admin/task/{id}")
    public Result<InspectionTask> getTask(@PathVariable Long id) {
        return Result.success(taskService.getTask(id));
    }

    @Operation(summary = "终止任务")
    @PostMapping("/admin/task/{id}/terminate")
    public Result<InspectionTask> terminateTask(@PathVariable Long id,
                                                 @RequestParam String reason) {
        return Result.success(taskService.terminateTask(id, reason));
    }

    @Operation(summary = "检查员任务列表")
    @GetMapping("/inspector/task/list")
    public Result<PageResult<TaskInspector>> listInspectorTasks(
            @RequestHeader("Authorization") String auth,
            @RequestParam(required = false) String status,
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "10") int size) {
        Long userId = jwtTokenProvider.getUserIdFromToken(auth.replace("Bearer ", ""));
        return Result.success(taskService.listInspectorTasks(userId, status, page, size));
    }

    @Operation(summary = "认领任务")
    @PostMapping("/inspector/task/{id}/claim")
    public Result<TaskInspector> claimTask(@PathVariable Long id,
                                            @RequestHeader("Authorization") String auth) {
        Long userId = jwtTokenProvider.getUserIdFromToken(auth.replace("Bearer ", ""));
        return Result.success(taskService.claimTask(id, userId));
    }

    @Operation(summary = "申请延期")
    @PostMapping("/inspector/task/{id}/extend")
    public Result<TaskExtension> requestExtension(@PathVariable Long id,
                                                   @RequestHeader("Authorization") String auth,
                                                   @RequestParam @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss") LocalDateTime requestedEndTime,
                                                   @RequestParam String reason) {
        Long userId = jwtTokenProvider.getUserIdFromToken(auth.replace("Bearer ", ""));
        return Result.success(taskService.requestExtension(id, userId, requestedEndTime, reason));
    }

    @Operation(summary = "个人任务统计")
    @GetMapping("/inspector/task/statistics")
    public Result<Map<String, Object>> getInspectorStatistics(
            @RequestHeader("Authorization") String auth) {
        Long userId = jwtTokenProvider.getUserIdFromToken(auth.replace("Bearer ", ""));
        return Result.success(taskService.getInspectorStatistics(userId));
    }
}
