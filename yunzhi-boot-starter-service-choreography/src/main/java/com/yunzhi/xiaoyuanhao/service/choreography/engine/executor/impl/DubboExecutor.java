package com.yunzhi.xiaoyuanhao.service.choreography.engine.executor.impl;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.serializer.PropertyFilter;
import com.yunzhi.xiaoyuanhao.service.choreography.engine.executor.Executor;
import com.yunzhi.xiaoyuanhao.service.choreography.engine.pojo.Task;
import com.yunzhi.xiaoyuanhao.service.choreography.engine.util.GenericUtil;
import lombok.extern.slf4j.Slf4j;
import org.apache.dubbo.config.ReferenceConfig;
import org.apache.dubbo.rpc.service.GenericService;
import org.springframework.stereotype.Component;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import static com.yunzhi.xiaoyuanhao.service.choreography.engine.util.GenericUtil.*;

/**
 * @author francis
 * @version 2022-03-22
 */
@Slf4j
@Component
public class DubboExecutor implements Executor {
    private static final Map<String, GenericService> dubboUrl2genericMap = new ConcurrentHashMap<>();


    @Override
    public String invoke(Task task) {
        GenericUtil.RpcParam rpcParam = getRpcParam(task);
        GenericService gs = getGenericService(task.getUrl(), task.getTimeout());
        Object o;
        try {
            o = gs.$invoke(task.getMethod(), rpcParam.getParamTypes(), rpcParam.getParams());
        } catch (Exception e) {
            log.error("dubboExecutor执行异常!", e);
            throw new RuntimeException(e);
        }
        //过滤掉class属性
        PropertyFilter filter = (source, name, value) -> !CLASS_STR.equals(name);
        return JSON.toJSONString(o, filter);
    }

    private GenericService getGenericService(String url, int timeout) {
        GenericService gs = dubboUrl2genericMap.get(url);
        if (gs == null) {
            String[] split = url.split(COLON_STR);
            gs = build(split[0], split[1], split[2], timeout);
            dubboUrl2genericMap.put(url, gs);
        }
        return gs;
    }


    private GenericService build(String interfaceClass, String group, String version, int timeOut) {
        ReferenceConfig<GenericService> reference = new ReferenceConfig<>();
        reference.setInterface(interfaceClass);
        reference.setVersion(version);
        reference.setGroup(group);
        reference.setTimeout(timeOut);
        reference.setGeneric("true");
        return reference.get();
    }

    @Override
    public String getType() {
        return DUBBO;
    }

}
