package com.frank.service.orchestration.engine.executor;

import com.frank.service.orchestration.engine.pojo.Task;

/**
 * @author francis
 * @version 2022-03-22
 */
public interface Executor {

    String invoke(Task task);

    String getType();

    String HSF = "hsf";
    String HTTP = "http";
    String DUBBO = "dubbo";
    String BEAN = "bean";
}
