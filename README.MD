## 服务编排

- 可以通过定义dsl来实现服务编排
- 通过表达式来赋值
- 采用jsonPath方式取值 https://github.com/alibaba/fastjson/wiki/JSONPath

-------
在[service-orchestration-demo](service-orchestration-demo)工程里附加了4个编排，dsl分别是a.json,b.json,c.json,d.json。</br>
如何访问？ 在[ServiceOrchestrationDemoApplication.java](service-orchestration-demo%2Fsrc%2Fmain%2Fjava%2Fcom%2Ffrank%2Fservice%2Forchestration%2Fdemo%2FServiceOrchestrationDemoApplication.java)类中，直接启动程序，然后访问以下4个地址

- http://localhost:8080/test
- http://localhost:8080/test1
- http://localhost:8080/test2
- http://localhost:8080/test3

