package com.frank.service.choreography.engine.executor.impl;

import com.frank.service.choreography.engine.executor.Executor;
import com.frank.service.choreography.engine.pojo.Task;

/**
 * @author francis
 * @version 2022-04-27
 */
public class BeanExecutor implements Executor {
    @Override
    public String invoke(Task task) {
        return null;
    }

    @Override
    public String getType() {
        return BEAN;
    }
}
