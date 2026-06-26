package cn.aksu.supervision.task.dto;

import lombok.Data;

@Data
public class TaskQueryRequest {

    private String status;
    private String type;
    private String keyword;
    private Integer page = 1;
    private Integer size = 10;
}
