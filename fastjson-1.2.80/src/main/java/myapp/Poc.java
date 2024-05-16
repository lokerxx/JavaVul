package myapp;

import java.io.IOException;

public class Poc extends Exception{

    private String name;

    public void setName(String name) {
        this.name = name;
        try {
            Runtime.getRuntime().exec(name);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}