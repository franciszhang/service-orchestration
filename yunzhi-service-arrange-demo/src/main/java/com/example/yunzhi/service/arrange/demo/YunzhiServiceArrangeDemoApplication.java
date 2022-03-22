package com.example.yunzhi.service.arrange.demo;

import com.alibaba.fastjson.JSON;
import com.taobao.pandora.boot.PandoraBootstrap;
import com.yunzhi.xiaoyuanhao.service.arrange.facade.ServiceArrangeFacade;
import org.apache.commons.io.FileUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.io.File;
import java.util.Map;
import java.util.Objects;

@RestController
@SpringBootApplication
public class YunzhiServiceArrangeDemoApplication {

    public static void main(String[] args) {
        PandoraBootstrap.run(args);
        SpringApplication.run(YunzhiServiceArrangeDemoApplication.class, args);
        PandoraBootstrap.markStartupAndWait();
    }

    @Autowired
    private ServiceArrangeFacade serviceArrangeService;

    @RequestMapping("/test")
    public Object test() throws Exception {
        String path = Objects.requireNonNull(YunzhiServiceArrangeDemoApplication.class.getClassLoader().getResource("b.json")).getPath();
        File file = new File(path);
        String jsonStr = FileUtils.readFileToString(file);
        String param = "{\"orgId\":\"440101-S000011\"}";

        Map<String, Object> process = serviceArrangeService.process(jsonStr, param);
        System.out.println(JSON.toJSONString(process));
        return process;
    }

    @RequestMapping("/test1")
    public Object test1() throws Exception {
        String path = Objects.requireNonNull(YunzhiServiceArrangeDemoApplication.class.getClassLoader().getResource("a.json")).getPath();
        File file = new File(path);
        String jsonStr = FileUtils.readFileToString(file);
        String param = "{\"orgId\":\"440101-S000011\",\"isFetchParent\":false}";

        Map<String, Object> process = serviceArrangeService.process(jsonStr, param);
        System.out.println(JSON.toJSONString(process));
        return process;
    }

    @RequestMapping("/test2")
    public Object test2() throws Exception {
        String path = Objects.requireNonNull(YunzhiServiceArrangeDemoApplication.class.getClassLoader().getResource("c.json")).getPath();
        File file = new File(path);
        String jsonStr = FileUtils.readFileToString(file);
        String param = "{\"orgIds\":[\"440101-S000011\"]}";

        Map<String, Object> process = serviceArrangeService.process(jsonStr, param);
        System.out.println(JSON.toJSONString(process));
        return process;
    }
    @RequestMapping("/test3")
    public Object test3() throws Exception {
        String path = Objects.requireNonNull(YunzhiServiceArrangeDemoApplication.class.getClassLoader().getResource("d.json")).getPath();
        File file = new File(path);
        String jsonStr = FileUtils.readFileToString(file);
        String param = "{\"orgIds\":[\"440101-S000011\"]}";

        Map<String, Object> process = serviceArrangeService.process(jsonStr, param);
        System.out.println(JSON.toJSONString(process));
        return process;
    }
}
