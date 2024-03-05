package com.frank.service.choreography.engine.processor;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.JSONPath;
import com.frank.service.choreography.engine.pojo.DslData;
import com.frank.service.choreography.engine.pojo.Expression;
import com.frank.service.choreography.engine.pojo.Task;

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
    private static final String COLON_STR = ":";

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
        for (String e : expressionList) {
            list.add(fillExpression(e));
        }
        return list;
    }

    private static Expression fillExpression(String e) {
        String[] colonSplit = e.split(COLON_STR);
        String expressionStr = colonSplit[0];
        String unexpectExpressionStr = null;
        if (colonSplit.length > 1) {
            unexpectExpressionStr = colonSplit[1];
        }
        String[] split = expressionStr.split(DOT_REGEX_STR);
        String alias = split[0].replace($_STR, EMPTY_STR);
        String realExpressionStr = expressionStr.replace($_STR + alias, $_STR);
        Expression expression = new Expression(expressionStr, realExpressionStr, alias);
        expression.setUnexpectExpression(unexpectExpressionStr);
        return expression;
    }

    public static void setExpressionVal(List<Expression> expressions, String alias, String resultJson) {
        if (expressions == null) {
            return;
        }
        for (Expression expression : expressions) {
            if (!alias.equals(expression.getAlias()) || expression.getExpressionValue() != null) {
                continue;
            }
            Object read = JSONPath.read(resultJson, expression.getRealExpression());
            expression.setExpressionValue(read);
            if (read == null && expression.getUnexpectExpression() != null) {
                read = JSONPath.read(resultJson, expression.getUnexpectExpression());
                if (read != null) {
                    expression.setUnexpectExpressionValue(read);
                    throw new RuntimeException(read.toString());
                }
            }
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
