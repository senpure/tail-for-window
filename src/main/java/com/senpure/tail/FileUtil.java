package com.senpure.tail;

import com.senpure.base.AppEvn;
import com.senpure.base.util.StringUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;


public class FileUtil {

    private static Logger logger = LoggerFactory.getLogger(FileUtil.class);

    public static String checkPath(String path) {
        if (!Charset.forName("gbk").newEncoder().canEncode(path)) {
            path = new String(path.getBytes(StandardCharsets.ISO_8859_1), StandardCharsets.UTF_8);
        }
        return path;
    }

    /**
     * 绝对路径获相对于classRoot目录
     * C:path or ../path
     *
     * @param path
     * @return
     */
    public static File file(String path) {

        return file(path, AppEvn.getClassRootPath());
    }

    /**
     * @param path   路径  C:path or ../path
     * @param parent 相对路径的目录
     * @return
     */
    public static File file(String path, String parent) {
        path = checkPath(path);
        File file;
        path = path.replace("\\", "/");
        String upperPath = path.toUpperCase();
        if (path.startsWith("/") || upperPath.startsWith("C:") || upperPath.startsWith("D:") || upperPath.startsWith("E:")
                || upperPath.startsWith("F:") || upperPath.startsWith("G:") || upperPath.startsWith("H:")
                || upperPath.startsWith("I:") || upperPath.startsWith("J:") || upperPath.startsWith("K:")
                || upperPath.startsWith("L:") || upperPath.startsWith("M:") || upperPath.startsWith("N:")
        ) {
            file = new File(path);
        } else if (path.startsWith("../")) {
            int count = StringUtil.count(path, "../");
            File parentFile = new File(parent);
            for (int i = 0; i < count; i++) {
                parentFile = parentFile.getParentFile();
            }
            file = new File(parentFile, path.replace("../", ""));
        } else {
            file = new File(parent, path);
        }
        return file;
    }

    /**
     * 确保路径是文件符合 '/' 结尾
     *
     * @param path
     * @return
     */
    public static String fullFileEnd(String path) {
        if (!(path.endsWith("/") || path.endsWith(File.separator))) {
            path = path + File.separator;
        }
        return path;
    }

}
