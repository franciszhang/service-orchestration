package com.yunzhi.xiaoyuanhao.service.arrange.engine.executor;

import com.yunzhi.xiaoyuanhao.service.arrange.engine.pojo.Task;

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
}
