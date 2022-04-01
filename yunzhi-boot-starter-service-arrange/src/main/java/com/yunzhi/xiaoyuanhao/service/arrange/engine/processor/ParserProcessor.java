package com.yunzhi.xiaoyuanhao.service.arrange.engine.processor;

import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.parser.Feature;
import com.yunzhi.xiaoyuanhao.service.arrange.engine.pojo.DslData;
import com.yunzhi.xiaoyuanhao.service.arrange.engine.pojo.Task;
import org.springframework.expression.EvaluationContext;
import org.springframework.expression.Expression;
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
        String prefix = System.getProperty("service.arrange.expression.prefix");
        if (StringUtils.hasText(prefix)) {
            expressionPrefix = prefix;
        }
        String suffix = System.getProperty("service.arrange.expression.suffix");
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
            for (String s : inputs.keySet()) {
                Object sVal = inputs.get(s);
                if (sVal.toString().contains(expressionPrefix)) {
                    Object realVal = parser.parseExpression(sVal.toString(), context).getValue(evaluationContext, Object.class);
                    inputs.put(s, realVal);
                }
            }
        }
        return dsl;
    }


}
