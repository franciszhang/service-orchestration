package com.yunzhi.xiaoyuanhao.service.choreography.engine.processor;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.JSONPath;
import com.yunzhi.xiaoyuanhao.service.choreography.engine.pojo.DslData;
import com.yunzhi.xiaoyuanhao.service.choreography.engine.pojo.Expression;
import com.yunzhi.xiaoyuanhao.service.choreography.engine.pojo.Task;

import java.util.*;

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

    public static List<Expression> getExpressions(DslData dsl) {
        String dslStr = dsl.toString();
        List<String> expressionList = new ArrayList<>();
        JSONArray jsonArray = (JSONArray) JSONPath.read(dslStr, INPUT_PATH);
        jsonArray.add(JSONPath.read(dslStr, OUTPUT_PATH));
        for (int i = 0; i < jsonArray.size(); i++) {
            JSONObject jsonObject = jsonArray.getJSONObject(i);
            recursiveGetExp(jsonObject, expressionList);
        }

        List<Expression> list = new ArrayList<>();
        for (String s : expressionList) {
            String[] split = s.split(DOT_REGEX_STR);
            String alias = split[0].replace($_STR, EMPTY_STR);
            String realExpression = s.replace($_STR + alias, $_STR);
            list.add(new Expression(s, realExpression, alias));
        }
        return list;
    }

    public static void setExpressionVal(List<Expression> expressions, String resultJson) {
        if (expressions == null) {
            return;
        }
        for (Expression expression : expressions) {
            String executorExp = expression.getRealExpression();
            Object read = JSONPath.read(resultJson, executorExp);
            if (read == null) {
                continue;
            }
            expression.setExpressionValue(read);
        }
    }

    public static void setDslInputVal(DslData dsl, Collection<Expression> expressions) {
        for (Task task : dsl.getTasks()) {
            for (Expression expression : expressions) {
                recursiveSetExpVal(task.getInputs(), expression);
            }
        }

    }

    public static void setDslOutputVal(DslData dsl, Collection<Expression> expressions) {
        for (Expression expression : expressions) {
            recursiveSetExpVal(dsl.getOutputs(), expression);
        }
    }

    private static void recursiveSetExpVal(Map<String, Object> map, Expression exp) {
        if (exp.getExpressionValue() == null) {
            return;
        }
        for (Map.Entry<String, Object> entry : map.entrySet()) {
            Object value = entry.getValue();
            if (value.equals(exp.getOriginExpression())) {
                entry.setValue(exp.getExpressionValue());
            }
            if (value instanceof Map) {
                recursiveSetExpVal((Map) value, exp);
            }
        }
    }

    private static void recursiveGetExp(JSONObject jsonObject, List<String> expressionList) {
        for (String s : jsonObject.keySet()) {
            Object o = jsonObject.get(s);
            if (o == null) {
                continue;
            }
            String objStr = o.toString();
            if (o instanceof String && objStr.startsWith($_STR)) {
                if (expressionList.contains(objStr)) {
                    continue;
                }
                expressionList.add(objStr);
            }
            if (o instanceof JSONObject) {
                recursiveGetExp((JSONObject) o, expressionList);
            }
        }

    }
}
