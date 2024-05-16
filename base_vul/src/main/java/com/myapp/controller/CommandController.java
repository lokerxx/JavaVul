package com.myapp.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
@RestController
public class CommandController {
    @GetMapping("/runtime_command_execute")
    public String executeRuntimeCommand(@RequestParam String  command) throws IOException {
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