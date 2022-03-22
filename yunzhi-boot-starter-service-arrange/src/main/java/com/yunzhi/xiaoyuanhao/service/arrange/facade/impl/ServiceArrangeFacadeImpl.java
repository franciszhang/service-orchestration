package com.yunzhi.xiaoyuanhao.service.arrange.facade.impl;

import com.yunzhi.xiaoyuanhao.service.arrange.engine.executor.ExecutorFactory;
import com.yunzhi.xiaoyuanhao.service.arrange.engine.processor.ParserProcessor;
import com.yunzhi.xiaoyuanhao.service.arrange.engine.pojo.DslData;
import com.yunzhi.xiaoyuanhao.service.arrange.engine.pojo.Task;
import com.yunzhi.xiaoyuanhao.service.arrange.engine.processor.DataProcessor;
import com.yunzhi.xiaoyuanhao.service.arrange.facade.ServiceArrangeFacade;
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
public class ServiceArrangeFacadeImpl implements ServiceArrangeFacade {


    @Override
    public Map<String, Object> process(String dslJsonStr, String paramJsonStr) {
        DslData dsl = ParserProcessor.parser(dslJsonStr, paramJsonStr);
        Map<String, List<String>> dest2expMap = DataProcessor.getDest2expression(dslJsonStr);

        long l = System.currentTimeMillis();
        Map<String, Object> resultMap = syncProcess(dsl, dest2expMap);
        long l1 = System.currentTimeMillis();

        log.info("serviceArrange-cost[{}]ms", (l1 - l));
        return resultMap;
    }

    private Map<String, Object> syncProcess(DslData dsl, Map<String, List<String>> dest2expMap) {
        for (Task task : dsl.getTasks()) {
            String result = ExecutorFactory.getExecutor(task.getTaskType()).invoke(task);
            List<String> exps = dest2expMap.get(task.getAlias());
            DataProcessor.setExpressionVal(exps, result, dsl);
        }
        return dsl.getOutputs();
    }
}
