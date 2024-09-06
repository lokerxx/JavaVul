package com.myapp.util;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.regex.*;

public class FileFilter {

    private static final Pattern PATH_TRAVERSAL_PATTERN = Pattern.compile(".*[.]{2,}.*|.*[%]{2,}.*|.*[/]{2,}.*", Pattern.CASE_INSENSITIVE);

    /**
     * 过滤路径中的目录遍历关键字符
     *
     * @param path 要验证的路径
     * @return 如果路径包含目录遍历字符返回 false，否则返回 true
     */
    public static boolean doFilter(String path) {
        if (path == null || path.isEmpty()) {
            return false;
        }
        return !PATH_TRAVERSAL_PATTERN.matcher(path).matches();
    }

    /**
     * 验证路径是否在指定的父目录中
     *
     * @param path      要验证的路径
     * @param parentDir 父目录
     * @return 如果路径在父目录中返回 true，否则返回 false
     * @throws IOException 如果路径无法解析为绝对路径
     */
    public static boolean isValidDirectoryPath(String path, String parentDir) throws IOException {
        Path basePath = Paths.get(parentDir).toRealPath().normalize();
        Path targetPath = basePath.resolve(path).normalize();

        return targetPath.startsWith(basePath);
    }

}
