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
            recursiveGetExpression(jsonObject, expressionList);
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

    public static void setExpressionVal(List<Expression> expressions, String alias, String resultJson) {
        if (expressions == null) {
            return;
        }
        for (Expression expression : expressions) {
            if (!alias.equals(expression.getAlias()) || expression.getExpressionValue() != null) {
                continue;
            }
            String executorExp = expression.getRealExpression();
            Object read = JSONPath.read(resultJson, executorExp);
            if (read == null) {
                continue;
            }
            expression.setExpressionValue(read);
        }
    }

    public static void setDslInputVal(Task task, Collection<Expression> expressions) {
        for (Expression expression : expressions) {
            recursiveSetExpressionVal(task.getInputs(), expression);
        }
    }

    public static void setDslOutputVal(DslData dsl, Collection<Expression> expressions) {
        for (Expression expression : expressions) {
            recursiveSetExpressionVal(dsl.getOutputs(), expression);
        }
    }

    private static void recursiveSetExpressionVal(Map<String, Object> map, Expression exp) {
        if (exp.getExpressionValue() == null) {
            return;
        }
        for (Map.Entry<String, Object> entry : map.entrySet()) {
            Object value = entry.getValue();
            if (value instanceof String) {
                String valStr = value.toString();
                if (valStr.equals(exp.getOriginExpression())) {
                    entry.setValue(exp.getExpressionValue());
                } else if (valStr.contains(exp.getOriginExpression())) {
                    entry.setValue(valStr.replace(exp.getOriginExpression(), exp.getExpressionValue().toString()));
                }
            } else if (value instanceof Map) {
                recursiveSetExpressionVal((Map) value, exp);
            }
        }
    }

    private static void recursiveGetExpression(JSONObject jsonObject, List<String> expressionList) {
        for (String s : jsonObject.keySet()) {
            Object o = jsonObject.get(s);
            if (o == null) {
                continue;
            }
            if (o instanceof String && ((String) o).startsWith($_STR)) {
                if (expressionList.contains(o)) {
                    continue;
                }
                expressionList.add(o.toString());
            } else if (o instanceof JSONObject) {
                recursiveGetExpression((JSONObject) o, expressionList);
            }
        }

    }
}
