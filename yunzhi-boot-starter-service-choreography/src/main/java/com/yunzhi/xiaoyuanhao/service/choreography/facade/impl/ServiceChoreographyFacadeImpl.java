package com.yunzhi.xiaoyuanhao.service.choreography.facade.impl;

import com.yunzhi.xiaoyuanhao.service.choreography.engine.executor.ExecutorFactory;
import com.yunzhi.xiaoyuanhao.service.choreography.engine.pojo.Expression;
import com.yunzhi.xiaoyuanhao.service.choreography.engine.processor.ParserProcessor;
import com.yunzhi.xiaoyuanhao.service.choreography.engine.pojo.DslData;
import com.yunzhi.xiaoyuanhao.service.choreography.engine.pojo.Task;
import com.yunzhi.xiaoyuanhao.service.choreography.engine.processor.DataProcessor;
import com.yunzhi.xiaoyuanhao.service.choreography.facade.ServiceChoreographyFacade;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

/**
 * @author francis
 * @version 2022-03-22
 */
@Slf4j
@Service
public class ServiceChoreographyFacadeImpl implements ServiceChoreographyFacade {


    @Override
    public Map<String, Object> process(String dslJsonStr, String paramJsonStr) {
        DslData dsl = ParserProcessor.parser(dslJsonStr, paramJsonStr);
        List<Expression> expressions = DataProcessor.getExpressions(dsl);

        long l = System.currentTimeMillis();
        Map<String, Object> resultMap = syncProcess(dsl, expressions);
        long l1 = System.currentTimeMillis();

        log.info("serviceChoreography-cost[{}]ms", (l1 - l));
        return resultMap;
    }

    private Map<String, Object> syncProcess(DslData dsl, List<Expression> expressions) {
        for (Task task : dsl.getTasks()) {
            DataProcessor.setDslInputVal(task, expressions);

            String result = ExecutorFactory.getExecutor(task.getTaskType()).invoke(task);

            DataProcessor.setExpressionVal(expressions, task.getAlias(), result);
        }

        DataProcessor.setDslOutputVal(dsl, expressions);
        return dsl.getOutputs();
    }
}
