package com.myapp.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.regex.Pattern;

@RestController
public class ReDosController {
    @GetMapping("/testReDos1")
    public String testReDos1(@RequestParam String input) {
        // 示例正则表达式：(a+)+
        //1. 对于`(a+)+`，可以传入一个长字符串，由大量的`a`组成，如`"aaaaaaaaaaaaaaaaaaaa..."`（字符串很长）。
        String pattern = "(a+)+";
        boolean isMatch = Pattern.matches(pattern, input);
        if (isMatch) {
            return "匹配成功";
        } else {
            return "匹配失败";
        }
    }
    @GetMapping("/testReDos2")
    public String testReDos2(@RequestParam String input) {
        // 正则表达式：([a-zA-Z]+)*
        //1. 对于`([a-zA-Z]+)*`，同样可以传入一个长的、由多个字母组成的字符串，比如`"aaaaaaaaaa...zzzzzzzzzz..."`（字符串很长）。

        String pattern = "([a-zA-Z]+)*";
        boolean isMatch = Pattern.matches(pattern, input);
        if (isMatch) {
            return "匹配成功";
        } else {
            return "匹配失败";
        }
    }

    @GetMapping("/testReDos3")
    public String testReDos3(@RequestParam String input) {
        // 正则表达式：(a|aa)+
        //1. 对于`(a|aa)+`，可以传入类似于第一个表达式的字符串，也就是大量的`a`，如`"aaaaaaaaaaaaaaaaaaaa..."`。
        String pattern = "(a|aa)+";
        boolean isMatch = Pattern.matches(pattern, input);
        if (isMatch) {
            return "匹配成功";
        } else {
            return "匹配失败";
        }
    }

    @GetMapping("/testReDos4")
    public String testReDos4(@RequestParam String input) {
        // 正则表达式：(a|a?)+
        //1. 对于`(a|a?)+`，这个表达式会对包含大量`a`的字符串特别敏感，因为它试图匹配`a`或者可选的`a`，所以同样传入长字符串`"aaaaaaaaaaaaaaaaaaaa..."`。
        String pattern = "(a|a?)+";
        boolean isMatch = Pattern.matches(pattern, input);
        if (isMatch) {
            return "匹配成功";
        } else {
            return "匹配失败";
        }
    }

    @GetMapping("/testReDos5")
    public String testReDos5(@RequestParam String input) {
        // 正则表达式：(.*a){x} | 注意：x > 10，在代码中直接使用具体数值，如20
        //1. 对于`(.*a){x}`，其中`x > 10`，可以传入一个长字符串，确保字符串的长度远远超过`x`，并且在末尾包含一个`a`，比如`"....................a"`（前面的点代表任意字符，总数超过`x`）。
        String pattern = "(.*a){20}";
        boolean isMatch = Pattern.matches(pattern, input);
        if (isMatch) {
            return "匹配成功";
        } else {
            return "匹配失败";
        }
    }



}
