package com.yunzhi.xiaoyuanhao.service.arrange.engine.processor;

import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.parser.Feature;
import com.yunzhi.xiaoyuanhao.service.arrange.engine.pojo.DslData;
import com.yunzhi.xiaoyuanhao.service.arrange.engine.pojo.Task;
import org.springframework.expression.EvaluationContext;
import org.springframework.expression.ExpressionParser;
import org.springframework.expression.spel.standard.SpelExpressionParser;
import org.springframework.expression.spel.support.StandardEvaluationContext;

import java.util.List;
import java.util.Map;

/**
 * @author francis
 * @version 2022-03-22
 */
public class ParserProcessor {
    public static final String SYMBOL = "#";

    public static DslData parser(String dslJsonStr, String params) {
        DslData dsl = JSONObject.parseObject(dslJsonStr, DslData.class, Feature.OrderedField);
        EvaluationContext context = new StandardEvaluationContext();
        JSONObject jsonObject = JSONObject.parseObject(params);
        for (String o : jsonObject.keySet()) {
            context.setVariable(o, jsonObject.get(o));
        }
        ExpressionParser parser = new SpelExpressionParser();
        List<Task> tasks = dsl.getTasks();
        for (Task task : tasks) {
            Map<String, Object> inputs = task.getInputs();
            for (String s : inputs.keySet()) {
                Object sVal = inputs.get(s);
                if (sVal.toString().contains(SYMBOL)) {
                    Object realVal = parser.parseExpression(sVal.toString()).getValue(context, Object.class);
                    inputs.put(s, realVal);
                }
            }
        }
        return dsl;
    }


}
