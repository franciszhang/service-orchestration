package com.yunzhi.xiaoyuanhao.service.choreography.facade;

import java.util.Map;

/**
 * @author francis
 * @version 2022-03-22
 */
public interface ServiceChoreographyFacade {

    Map<String, Object> process(String dslJsonStr, String paramJsonStr);

}
