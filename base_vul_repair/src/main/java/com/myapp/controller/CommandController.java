package com.myapp.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Arrays;
import java.util.List;

@RestController
public class CommandController {
    private final List<String> allowedCommands = Arrays.asList("ls", "echo", "date"); // 允许的命令列表

    @GetMapping("/runtime_command_execute")
    public String executeRuntimeCommand(@RequestParam String command) throws IOException {
        if (!allowedCommands.contains(command)) {
            return "Command not allowed,only allow [ls,echo,date]";
        }

        String output = "";
        Process process = Runtime.getRuntime().exec(command);
        BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()));
        String line;
        while ((line = reader.readLine()) != null) {
            output += line + "\n";
        }
        return output;
    }

    @GetMapping("/process_builder_command_execute")
    public String executeProcessBuilderCommand(@RequestParam String command) throws IOException {
        if (!allowedCommands.contains(command.split(" ")[0])) {
            return "Command not allowed,only allow [ls,echo,date]";
        }

        String output = "";
        ProcessBuilder builder = new ProcessBuilder(command.split(" "));
        Process process = builder.start();
        BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()));
        String line;
        while ((line = reader.readLine()) != null) {
            output += line + "\n";
        }
        return output;
    }





}