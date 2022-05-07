package com.yunzhi.xiaoyuanhao.service.choreography.engine.processor;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.parser.Feature;
import com.yunzhi.xiaoyuanhao.service.choreography.engine.pojo.DslData;
import com.yunzhi.xiaoyuanhao.service.choreography.engine.pojo.Task;
import org.springframework.expression.EvaluationContext;
import org.springframework.expression.ExpressionParser;
import org.springframework.expression.ParserContext;
import org.springframework.expression.common.TemplateParserContext;
import org.springframework.expression.spel.standard.SpelExpressionParser;
import org.springframework.expression.spel.support.StandardEvaluationContext;
import org.springframework.util.StringUtils;

import java.util.List;
import java.util.Map;

/**
 * @author francis
 * @version 2022-03-22
 */
public class ParserProcessor {
    public static String expressionPrefix = "${";
    public static String expressionSuffix = "}";

    static {
        String prefix = System.getProperty("service.choreography.expression.prefix");
        if (StringUtils.hasText(prefix)) {
            expressionPrefix = prefix;
        }
        String suffix = System.getProperty("service.choreography.expression.suffix");
        if (StringUtils.hasText(suffix)) {
            expressionSuffix = suffix;
        }
    }


    public static DslData parser(String dslJsonStr, String params) {
        DslData dsl = JSONObject.parseObject(dslJsonStr, DslData.class, Feature.OrderedField);
        EvaluationContext evaluationContext = new StandardEvaluationContext();
        ParserContext context = new TemplateParserContext(expressionPrefix, expressionSuffix);
        JSONObject jsonObject = JSONObject.parseObject(params);
        for (String o : jsonObject.keySet()) {
            evaluationContext.setVariable(o, jsonObject.get(o));
        }
        ExpressionParser parser = new SpelExpressionParser();
        List<Task> tasks = dsl.getTasks();
        for (Task task : tasks) {
            Map<String, Object> inputs = task.getInputs();
            for (Map.Entry<String, Object> entry : inputs.entrySet()) {
                Object sVal = entry.getValue();
                if (sVal instanceof String && sVal.toString().contains(expressionPrefix)) {
                    entry.setValue(parser.parseExpression(sVal.toString(), context).getValue(evaluationContext, Object.class));
                } else if (sVal instanceof JSONObject) {
                    for (Map.Entry<String, Object> stringObjectEntry : ((JSONObject) sVal).entrySet()) {
                        Object value = stringObjectEntry.getValue();
                        if (value instanceof String && value.toString().contains(expressionPrefix)) {
                            stringObjectEntry.setValue(parser.parseExpression(value.toString(), context).getValue(evaluationContext, Object.class));
                        } else if (value instanceof JSONArray && value.toString().contains(expressionPrefix)) {
                            JSONArray arrayVal = (JSONArray) value;
                            for (int i = 0; i < arrayVal.size(); i++) {
                                Object o = arrayVal.get(i);
                                if (o.toString().contains(expressionPrefix)) {
                                    o = parser.parseExpression(o.toString(), context).getValue(evaluationContext, Object.class);
                                }
                                arrayVal.set(i, o);
                            }
                        }
                    }
                }
            }
        }
        Map<String, Object> outputs = dsl.getOutputs();
        for (Map.Entry<String, Object> entry : outputs.entrySet()) {
            Object value = entry.getValue();
            if (value.toString().contains(expressionPrefix)) {
                entry.setValue(parser.parseExpression(value.toString(), context).getValue(evaluationContext, Object.class));
            }
        }
        return dsl;
    }


}
