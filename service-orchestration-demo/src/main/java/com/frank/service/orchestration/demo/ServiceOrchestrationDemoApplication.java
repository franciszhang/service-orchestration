package com.frank.service.orchestration.demo;

import com.alibaba.fastjson.JSON;
import com.frank.service.orchestration.facade.ServiceOrchestrationFacade;
import org.apache.commons.io.FileUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import java.io.File;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

@RestController
@SpringBootApplication
public class ServiceOrchestrationDemoApplication {

    public static void main(String[] args) {
        SpringApplication.run(ServiceOrchestrationDemoApplication.class, args);
    }

    @Autowired
    private ServiceOrchestrationFacade serviceOrchestrationFacade;

    @RequestMapping("/test")
    public Object test(HttpServletRequest request) throws Exception {
        String path = Objects.requireNonNull(ServiceOrchestrationDemoApplication.class.getClassLoader().getResource("b.json")).getPath();
        File file = new File(path);
        String jsonStr = FileUtils.readFileToString(file);
        HashMap<String, Object> map = new HashMap<>();
        for (String s : request.getParameterMap().keySet()) {
            map.put(s, request.getParameter(s));
        }

        Map<String, Object> process = serviceOrchestrationFacade.process(jsonStr, JSON.toJSONString(map));
        System.out.println(JSON.toJSONString(process));
        return process;
    }

    @RequestMapping("/test1")
    public Object test1() throws Exception {
        String path = Objects.requireNonNull(ServiceOrchestrationDemoApplication.class.getClassLoader().getResource("a.json")).getPath();
        File file = new File(path);
        String jsonStr = FileUtils.readFileToString(file);
//        String param = "{\"org_id\":\"330108-S000189\",\"dept_id\":\"f2c2521762254d7dbdbc4d908430163e\",\"clientId\":\"c4afe29340cb435a921977bb9fc918be\",\"clientSecret\":\"230106df961c448cae53b25021e34e8b\"}";
        String param = "{\"imei\":\"845128451284511\"}";

        Map<String, Object> process = serviceOrchestrationFacade.process(jsonStr, param);
        System.out.println(JSON.toJSONString(process));
        return process;
    }

    @RequestMapping("/test2")
    public Object test2() throws Exception {
        String path = Objects.requireNonNull(ServiceOrchestrationDemoApplication.class.getClassLoader().getResource("c.json")).getPath();
        File file = new File(path);
        String jsonStr = FileUtils.readFileToString(file);
        String param = "{\"orgIds\":[\"440101-S0000111\"]}";

        Map<String, Object> process = serviceOrchestrationFacade.process(jsonStr, param);
        System.out.println(JSON.toJSONString(process));
        return process;
    }

    @RequestMapping("/test3")
    public Object test3() throws Exception {
        String path = Objects.requireNonNull(ServiceOrchestrationDemoApplication.class.getClassLoader().getResource("d.json")).getPath();
        File file = new File(path);
        String jsonStr = FileUtils.readFileToString(file);
        String param = "{\"media_ids\":[\"440101-S000011\"],\"org_id\":\"440101-S000011\"}";

        Map<String, Object> process = serviceOrchestrationFacade.process(jsonStr, param);
        System.out.println(JSON.toJSONString(process));
        return process;
    }

    @RequestMapping("/test4")
    public Object test4() throws Exception {
        String path = Objects.requireNonNull(ServiceOrchestrationDemoApplication.class.getClassLoader().getResource("e.json")).getPath();
        File file = new File(path);
        String jsonStr = FileUtils.readFileToString(file);
        String param = "{\"user_id\":\"769fe83321c34145bccc280be4aa59cd\",\"org_id\":\"440101-S000011\"}";

        Map<String, Object> process = serviceOrchestrationFacade.process(jsonStr, param);
        System.out.println(JSON.toJSONString(process));
        return process;
    }

    @RequestMapping("/test5")
    public Object test5() throws Exception {
        String path = Objects.requireNonNull(ServiceOrchestrationDemoApplication.class.getClassLoader().getResource("f.json")).getPath();
        File file = new File(path);
        String jsonStr = FileUtils.readFileToString(file);
//        String param = "{\"deptId\":[\"228891204B02461B875CEE137E0EFF8B\"],\"workContext\":{\"orgId\":\"440101-S000011\"}}";
        String param = "{\"dept_id\":\"228891204B02461B875CEE137E0EFF8B\",\"org_id\":\"440101-S000011\"}";

        Map<String, Object> process = serviceOrchestrationFacade.process(jsonStr, param);
        System.out.println(JSON.toJSONString(process));
        return process;
    }

    @RequestMapping("/test0")
    public Object test0(HttpServletRequest request) throws Exception {
        HashMap<String, Object> map = new HashMap<>();
        for (String s : request.getParameterMap().keySet()) {
            map.put(s, request.getParameter(s));
        }

        String path = Objects.requireNonNull(ServiceOrchestrationDemoApplication.class.getClassLoader().getResource(map.get("file").toString())).getPath();
        File file = new File(path);
        String jsonStr = FileUtils.readFileToString(file);
        Map<String, Object> process = serviceOrchestrationFacade.process(jsonStr, JSON.toJSONString(map));
        System.out.println(JSON.toJSONString(process));
        return process;
    }
}
