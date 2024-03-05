package com.frank.service.choreography.engine.util;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.frank.service.choreography.engine.pojo.Task;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.*;

/**
 * @author francis
 * @version 2022-05-06
 */
public class GenericUtil {
    public static final String CLASS_STR = "class";
    public static final String COLON_STR = ":";


    public static RpcParam getRpcParam(Task task) {
        List<Object> inputs = new ArrayList<>(task.getInputs().values());
        Object[] params = new Object[inputs.size()];
        Map<String, String> inputsExtra = task.getInputsExtra();
        String[] paramTypes = inputsExtra.values().toArray(new String[0]);
        for (int i = 0; i < paramTypes.length; i++) {
            String paramType = paramTypes[i];
            Object o = inputs.get(i);
            if (o.getClass().getName().equals(paramType)) {
                params[i] = inputs.get(i);
            } else {
                Object object = checkBaseType(paramType, o);
                if (object != null) {
                    params[i] = object;
                } else {
                    if (o.getClass().getName().equals(String.class.getName())) {
                        o = JSON.parseObject(o.toString());
                    }
                    Map map = (Map) PojoUtils.generalize(o);
                    map.put(CLASS_STR, paramType);
                    params[i] = map;
                }
            }
        }
        return new RpcParam(params, paramTypes);
    }

    public static Object checkBaseType(String type, Object value) {
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
            if (value.getClass().getName().equals(String.class.getName())) {
                return JSONObject.parseArray(value.toString()).toJavaObject(ArrayList.class);
            }
            return ((JSONArray) value).toJavaObject(ArrayList.class);
        }
        return null;
    }

    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    public static class RpcParam {
        private Object[] params;
        private String[] paramTypes;
    }

}
