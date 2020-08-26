package com.senpure.tail;

import com.beust.jcommander.JCommander;
import com.beust.jcommander.Parameter;
import com.beust.jcommander.UsageFormatter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.util.Arrays;

/**
 * Command
 *
 * @author senpure
 * @time 2020-08-19 10:51:31
 */
public class Command {

    private Logger logger = LoggerFactory.getLogger(getClass());

    @Parameter(order = 0, description = "文件路径", required = true)
    private String path;
    @Parameter(names = {"-n", "num"}, order = 1, description = "数量增加/减少操作")
    private Integer num = 5;
    @Parameter(names = {"-c", "charset"}, order = 6, description = "文件编码")
    private String charset = "utf-8";
    @Parameter(names = {"--null-exit-time"}, order = 7, description = "空行退出时间")
    private long nullExitTime;
    @Parameter(names = {"--null-exit-message"}, order = 8, description = "空行退出提示")
    private String nullExitMessage;
    @Parameter(names = {"-h", "help"}, description = "使用帮助", order = 1000, help = true)
    private boolean help;


    public void execute(String[] args) {
        execute(new Tail(), args);
    }

    public void execute(Tail tail, String[] args) {
        JCommander commander = new JCommander();
        commander.setProgramName("tail");
        commander.addObject(this);
        try {
            commander.parse(args);
        } catch (Exception e) {
            logger.error("命令错误" + Arrays.toString(args), e);
            System.exit(0);
            return;
        }
        if (isHelp()) {
            StringBuilder out = new StringBuilder();
            UsageFormatter usageFormatter = new UsageFormatter(commander);
            usageFormatter.usage(out);
            System.out.println(out.toString());
            System.exit(0);
        } else {

            File file = FileUtil.file(path);
            if (!file.exists() || file.isDirectory()) {
                logger.error("文件路径错误：["+file.getAbsolutePath()+"]");
                System.exit(0);
                return;
            }
            if (num < 1 || num > 100) {
                logger.error("num 参数错误[1,100] " + num);
                System.exit(0);
                return;
            }
            tail.setNullExitMessage(nullExitMessage);
            tail.setNullExitTime(nullExitTime);
            tail.tail(file, num, charset);

        }


    }

    public boolean isHelp() {
        return help;
    }

    public void setHelp(boolean help) {
        this.help = help;
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public Integer getNum() {
        return num;
    }

    public void setNum(Integer num) {
        this.num = num;
    }

    public String getCharset() {
        return charset;
    }

    public void setCharset(String charset) {
        this.charset = charset;
    }

    public long getNullExitTime() {
        return nullExitTime;
    }

    public void setNullExitTime(long nullExitTime) {
        this.nullExitTime = nullExitTime;
    }

    public String getNullExitMessage() {
        return nullExitMessage;
    }

    public void setNullExitMessage(String nullExitMessage) {
        this.nullExitMessage = nullExitMessage;
    }

    @Override
    public String toString() {
        return "Command{" +
                "path='" + path + '\'' +
                ", num=" + num +
                ", help=" + help +
                '}';
    }

    public static void main(String[] args) {
        args = new String[]{"-n", "5", "ppp", "-h"};
        Command command = new Command();
        command.execute(args);
    }
}
