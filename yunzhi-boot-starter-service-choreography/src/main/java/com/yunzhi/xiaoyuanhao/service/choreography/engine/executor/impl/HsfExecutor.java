package com.yunzhi.xiaoyuanhao.service.choreography.engine.executor.impl;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.serializer.PropertyFilter;
import com.taobao.hsf.app.spring.util.HSFSpringConsumerBean;
import com.taobao.hsf.remoting.service.GenericService;
import com.yunzhi.xiaoyuanhao.service.choreography.engine.executor.Executor;
import com.yunzhi.xiaoyuanhao.service.choreography.engine.pojo.Task;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import static com.yunzhi.xiaoyuanhao.service.choreography.engine.util.GenericUtil.*;

/**
 * @author francis
 * @version 2022-03-22
 */
@Slf4j
@Service
public class HsfExecutor implements Executor {

    private static final Map<String, GenericService> hsfUrl2genericSMap = new ConcurrentHashMap<>();

    @Override
    public String invoke(Task task) {
        RpcParam rpcParam = getRpcParam(task);
        GenericService gs = getGenericService(task.getUrl(), task.getTimeout());
        Object o;
        try {
            o = gs.$invoke(task.getMethod(), rpcParam.getParamTypes(), rpcParam.getParams());
        } catch (Exception e) {
            log.error("hsfExecutor执行异常!", e);
            throw new RuntimeException(e);
        }
        //过滤掉class属性
        PropertyFilter filter = (source, name, value) -> !CLASS_STR.equals(name);
        return JSON.toJSONString(o, filter);

    }

    @Override
    public String getType() {
        return HSF;
    }

    private GenericService getGenericService(String url, int timeout) {
        GenericService gs = hsfUrl2genericSMap.get(url);
        if (gs == null) {
            String[] split = url.split(COLON_STR);
            gs = build(split[0], split[1], split[2], timeout);
            hsfUrl2genericSMap.put(url, gs);
        }
        return gs;
    }


    private GenericService build(String interfaceClass, String group, String version, int timeOut) {
        HSFSpringConsumerBean hsfSpringConsumerBean = new HSFSpringConsumerBean();
        hsfSpringConsumerBean.setVersion(version);
        hsfSpringConsumerBean.setGroup(group);
        hsfSpringConsumerBean.setInterfaceName(interfaceClass);
        hsfSpringConsumerBean.setGeneric("true");
        hsfSpringConsumerBean.setClientTimeout(timeOut);
        try {
            hsfSpringConsumerBean.init();
            return (GenericService) hsfSpringConsumerBean.getObject();
        } catch (Exception e) {
            throw new RuntimeException(e.getMessage());
        }
    }
}
