package com.yunzhi.xiaoyuanhao.service.arrange.engine.executor.impl;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.serializer.PropertyFilter;
import com.taobao.hsf.app.spring.util.HSFSpringConsumerBean;
import com.taobao.hsf.remoting.service.GenericService;
import com.taobao.hsf.util.PojoUtils;
import com.yunzhi.xiaoyuanhao.service.arrange.engine.executor.Executor;
import com.yunzhi.xiaoyuanhao.service.arrange.engine.pojo.Task;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author francis
 * @version 2022-03-22
 */
@Slf4j
@Service
public class HsfExecutor implements Executor {
    public static final String CLASS_STR = "class";
    public static final String COLON_STR = ":";

    private static final Map<String, GenericService> genericServiceMap = new ConcurrentHashMap<>();

    @Override
    public String invoke(Task task) {
        List<Object> inputs = new ArrayList<>(task.getInputs().values());
        Object[] params = new Object[inputs.size()];
        Map<String, String> inputsExtra = task.getInputsExtra();
        String[] paramTypes = inputsExtra.values().toArray(new String[0]);
        for (int i = 0; i < paramTypes.length; i++) {
            String paramType = paramTypes[i];
            if (inputs.get(i).getClass().getName().equals(paramType)) {
                params[i] = inputs.get(i);
            } else {
                Object object = checkBaseType(paramType, inputs.get(i));
                if (object != null) {
                    params[i] = object;
                } else {
                    Map map = (Map) PojoUtils.generalize(inputs.get(i));
                    map.put(CLASS_STR, paramType);
                    params[i] = map;
                }
            }
        }

        GenericService gs = getGenericService(task.getUrl(), task.getTimeout());
        Object o;
        try {
            o = gs.$invoke(task.getMethod(), paramTypes, params);
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
        GenericService gs = genericServiceMap.get(url);
        if (gs == null) {
            String[] split = url.split(COLON_STR);
            gs = build(split[0], split[1], split[2], timeout);
            genericServiceMap.put(url, gs);
        }
        return gs;
    }

    private static Object checkBaseType(String type, Object value) {
        if (byte.class.getName().equals(type) || Byte.class.getName().equals(type)) {
            return Byte.valueOf(value.toString());
        } else if (int.class.getName().equals(type) || Integer.class.getName().equals(type)) {
            return Integer.valueOf(value.toString());
        } else if (long.class.getName().equals(type) || Long.class.getName().equals(type)) {
            return Long.valueOf(value.toString());
        } else if (double.class.getName().equals(type) || Double.class.getName().equals(type)) {
            return Double.valueOf(value.toString());
        } else if (float.class.getName().equals(type) || Float.class.getName().equals(type)) {
            return Float.valueOf(value.toString());
        } else if (boolean.class.getName().equals(type) || Boolean.class.getName().equals(type)) {
            return Boolean.valueOf(value.toString());
        } else if (short.class.getName().equals(type) || Short.class.getName().equals(type)) {
            return Short.valueOf(value.toString());
        } else if (type.equals(List.class.getName()) || type.equals(Collection.class.getName())) {
            return ((JSONArray) value).toJavaObject(ArrayList.class);
        }
        return null;
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
