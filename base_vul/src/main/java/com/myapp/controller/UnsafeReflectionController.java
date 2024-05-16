package com.myapp.controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class UnsafeReflectionController {
    //http://localhost:8080/unsafeReflection?className=java.lang.Runtime
    //http://localhost:8080/unsafeReflection?className=java.util.Date
    //http://localhost:8080/unsafeReflection?className=com.example.malicious.MaliciousClass
    @GetMapping("/unsafeReflection")
    public String unsafeReflection(@RequestParam String className) {
        try {
            // 不安全的反射，因为它根据用户输入直接加载类
            Class<?> clazz = Class.forName(className);

            // 假设这个类有一个无参构造函数，我们创建它的实例
            Object instance = clazz.newInstance();  // Deprecated method, for illustration only

            // 返回实例的信息，可能包含敏感数据
            return instance.toString();

        } catch (ClassNotFoundException | InstantiationException | IllegalAccessException e) {
            // 异常处理，应该避免返回给用户详细的错误信息
            return "Error occurred: " + e.getMessage();
        }
    }
}