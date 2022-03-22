package com.yunzhi.xiaoyuanhao.service.arrange.facade;

import java.util.Map;

/**
 * @author francis
 * @version 2022-03-22
 */
public interface ServiceArrangeFacade {

    Map<String, Object> process(String dslJsonStr, String paramJsonStr);

}
