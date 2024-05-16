package my.agent;

import java.lang.instrument.Instrumentation;

public class SimpleAgent {
    public static void premain(String agentArgs, Instrumentation inst) {
        System.out.println("SimpleAgent has been loaded");
        printIastLogo();
    }

    private static void printIastLogo() {
        String[] lines = new String[] {
                "  _____   _   _    _____   _____ ",
                " |_   _| | | | |  |_   _| |  ___|",
                "   | |   | |_| |    | |   | |__  ",
                "   | |   |  _  |    | |   |  __| ",
                "  _| |_  | | | |   _| |_  | |___ ",
                " |_____| |_| |_|  |_____| |_____|"
        };

        for (String line : lines) {
            System.out.println(line);
        }
    }
}
