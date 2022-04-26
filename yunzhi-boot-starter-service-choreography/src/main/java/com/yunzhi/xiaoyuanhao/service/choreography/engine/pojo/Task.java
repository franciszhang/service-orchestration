package com.yunzhi.xiaoyuanhao.service.choreography.engine.pojo;

import lombok.Data;

import java.util.Map;

/**
 * @author francis
 * @version 2022-03-22
 */
@Data
public class Task {
    private String url;
    private String alias;
    private String taskType;
    private String executeMode;
    private String method;
    private int timeout;
    private Map<String, Object> inputs;
    private Map<String, String> inputsExtra;
}
