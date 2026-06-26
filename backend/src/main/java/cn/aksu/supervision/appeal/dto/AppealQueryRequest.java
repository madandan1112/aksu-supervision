package cn.aksu.supervision.appeal.dto;

import lombok.Data;

@Data
public class AppealQueryRequest {

    private String status;
    private String type;
    private String keyword;
    private Integer page = 1;
    private Integer size = 10;
}
