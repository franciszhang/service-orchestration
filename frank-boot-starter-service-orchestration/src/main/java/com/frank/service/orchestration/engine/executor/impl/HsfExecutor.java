package com.frank.service.orchestration.engine.executor.impl;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

/**
 * @author francis
 * @version 2022-03-22
 */
@Slf4j
@Service
public class HsfExecutor extends DubboExecutor {

    @Override
    public String getType() {
        return HSF;
    }

}
