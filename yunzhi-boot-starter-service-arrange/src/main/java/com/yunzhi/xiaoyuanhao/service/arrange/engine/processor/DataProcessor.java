package com.yunzhi.xiaoyuanhao.service.arrange.engine.processor;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.JSONPath;
import com.yunzhi.xiaoyuanhao.service.arrange.engine.pojo.DslData;
import com.yunzhi.xiaoyuanhao.service.arrange.engine.pojo.Task;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author francis
 * @version 2022-03-22
 */
public class DataProcessor {
    private static final String INPUT_PATH = "$.tasks[*].inputs";
    private static final String OUTPUT_PATH = "$.outputs";
    private static final String $_STR = "$";
    private static final String EMPTY_STR = "";
    private static final String DOT_REGEX_STR = "\\.";

    public static Map<String, List<String>> getDest2expression(String dsl) {
        List<String> expressionList = new ArrayList<>();
        JSONArray jsonArray = (JSONArray) JSONPath.read(dsl, INPUT_PATH);
        jsonArray.add(JSONPath.read(dsl, OUTPUT_PATH));
        for (int i = 0; i < jsonArray.size(); i++) {
            JSONObject jsonObject = jsonArray.getJSONObject(i);
            recursiveGetExp(jsonObject, expressionList);
        }

        Map<String, List<String>> alias2expMap = new HashMap<>();
        for (String s : expressionList) {
            String[] split = s.split(DOT_REGEX_STR);
            String alias = split[0].replace($_STR, EMPTY_STR);
            if (alias2expMap.containsKey(alias)) {
                alias2expMap.get(alias).add(s);
            } else {
                List<String> list = new ArrayList<>();
                list.add(s);
                alias2expMap.put(alias, list);
            }
        }
        return alias2expMap;
    }

    public static void setExpressionVal(List<String> expressions, String resultJson, DslData dsl) {
        for (String expression : expressions) {
            String[] split = expression.split(DOT_REGEX_STR);
            String executorExp = expression.replace(split[0], $_STR);
            Object read = JSONPath.read(resultJson, executorExp);
            if (read == null) {
                continue;
            }
            recursiveSetExpVal(dsl.getOutputs(), expression, read);

            for (Task task : dsl.getTasks()) {
                recursiveSetExpVal(task.getInputs(), expression, read);
            }
        }
    }

    private static void recursiveSetExpVal(Map<String, Object> map, String exp, Object readVal) {
        for (Map.Entry<String, Object> entry : map.entrySet()) {
            Object value = entry.getValue();
            if (value.equals(exp)) {
                entry.setValue(readVal);
            }
            if (value instanceof Map) {
                recursiveSetExpVal((Map) value, exp, readVal);
            }
        }
    }

    private static void recursiveGetExp(JSONObject jsonObject, List<String> expressionList) {
        for (String s : jsonObject.keySet()) {
            Object o = jsonObject.get(s);
            if (o instanceof String && o.toString().startsWith($_STR)) {
                expressionList.add(o.toString());
            }
            if (o instanceof JSONObject) {
                recursiveGetExp((JSONObject) o, expressionList);
            }
        }

    }
}
