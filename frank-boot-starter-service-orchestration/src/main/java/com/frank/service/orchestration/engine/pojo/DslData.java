package com.frank.service.orchestration.engine.pojo;

import com.alibaba.fastjson.JSON;
import lombok.Data;

import java.util.List;
import java.util.Map;

/**
 * @author francis
 * @version 2022-03-22
 */
@Data
public class DslData {
    private List<Task> tasks;
    private String name;
    private String description;
    private Map<String, Object> outputs;

    @Override
    public String toString() {
        return JSON.toJSONString(this);
    }

}




