package com.frank.service.orchestration.facade;

import java.util.Map;

/**
 * @author francis
 * @version 2022-03-22
 */
public interface ServiceOrchestrationFacade {

    Map<String, Object> process(String dslJsonStr, String paramJsonStr);

}
