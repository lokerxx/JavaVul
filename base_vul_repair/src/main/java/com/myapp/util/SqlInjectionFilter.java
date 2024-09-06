package com.myapp.util;

public class SqlInjectionFilter {

    private static final String[] SQL_INJECTION_KEYWORDS = {
            "SELECT", "INSERT", "UPDATE", "DELETE", "DROP", "ALTER", "--", ";", "/*", "*/", "@@", "CHAR", "NCHAR", "VARCHAR", "NVARCHAR", "ALTER", "BEGIN", "CAST", "CREATE", "CURSOR", "DECLARE", "END", "EXEC", "FETCH", "KILL", "OPEN", "SYS", "SYSOBJECTS", "SYSUSERS", "TABLE", "INFORMATION_SCHEMA", "UNION"
    };

    public static void validate(String input) throws IllegalArgumentException {
        if (input != null) {
            String upperInput = input.toUpperCase();
            for (String keyword : SQL_INJECTION_KEYWORDS) {
                if (upperInput.contains(keyword)) {
                    throw new IllegalArgumentException("Invalid input detected");
                }
            }
        }
    }
}
