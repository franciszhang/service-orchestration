package com.yunzhi.xiaoyuanhao.service.arrange.engine.executor.impl;

import com.yunzhi.xiaoyuanhao.service.arrange.engine.executor.Executor;
import com.yunzhi.xiaoyuanhao.service.arrange.engine.pojo.Task;

/**
 * @author francis
 * @version 2022-03-22
 */
public class HttpExecutor implements Executor {
    @Override
    public String invoke(Task task) {
        return null;

    }

    @Override
    public String getType() {
        return HTTP;
    }


}
