## 服务编排

- 通过定义dsl来实现服务编排
- 通过表达式来赋值
- 可同步执行和异步执行
- 采用jsonPath方式取值 https://github.com/alibaba/fastjson/wiki/JSONPath

-------
在[service-orchestration-demo](service-orchestration-demo)工程里附加了几个编排，dsl是json结尾的文件。</br>

可以参考示例上述dsl文件进行修改， 然后在启动 [service-orchestration-demo](service-orchestration-demo) 工程，访问下面几个接口，查看效果
- http://localhost:8080/test
- http://localhost:8080/test1
- http://localhost:8080/test2
- http://localhost:8080/test3

