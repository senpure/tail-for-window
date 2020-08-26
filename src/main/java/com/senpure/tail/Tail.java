package com.senpure.tail;


import org.springframework.util.StringUtils;

import java.io.File;
import java.io.RandomAccessFile;
import java.nio.charset.StandardCharsets;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;
import java.util.function.BiConsumer;


public class Tail {

    private final BiConsumer<String, Boolean> consumer;

    private File file;

    private final ScheduledExecutorService service;
    private ScheduledFuture<?> tailScheduledFuture;

    private long nullExitTime = 0;
    private String nullExitMessage;

    public Tail() {
        this(Tail::accept);
    }

    public Tail(BiConsumer<String, Boolean> consumer) {
        this.consumer = consumer;
        service = Executors.newSingleThreadScheduledExecutor(r -> {
            Thread thread = new Thread(r);
            thread.setDaemon(true);
            thread.setName("tail-executor");
            return thread;
        });
    }

    private static void accept(String line, Boolean newLine) {
        System.out.print(line);
        if (newLine) {
            System.out.println();
        }
    }

    public void tail(File file) {

        tail(file, 5, "utf-8");
    }

    public void tail(File file, int lastLine) {

        tail(file, lastLine, "utf-8");
    }

    public void tail(File file, int lastLine, String charsetName) {
        if (this.file != null) {
            return;
        }
        this.file = file;
        // createWatcher();
        try {
            RandomAccessFile dataFile = new RandomAccessFile(file, "r");
            dataFile.seek(dataFile.length() - 1);
            int findLine = 0;
            int needLine = lastLine;
            long v = dataFile.getFilePointer();
            int endChar = dataFile.read();
            if (endChar != '\n') {
                needLine--;
            }
            dataFile.seek(v);
            for (long i = v - 1; i >= 0; i--) {
                int c = dataFile.read();
                if (c == '\n') {
                    findLine++;
                    if (findLine > needLine) {
                        break;
                    }
                }
                dataFile.seek(i);
            }

            tailScheduledFuture = service.scheduleAtFixedRate(() -> tail(dataFile, charsetName), 0, 300, TimeUnit.MILLISECONDS);

            //    tail(dataFile);

        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    private long firstNullTime = 0;
    private boolean lastLineEnd = true;

    private void tail(RandomAccessFile dataFile, String charsetName) {
        int lineEndChar = 10;
        boolean checkEnd = false;
        String show = null;
        boolean flag = true;
        boolean exit = false;
        while (flag) {
            try {
                String line = dataFile.readLine();
                if (line == null) {
                    if (nullExitTime > 0) {
                        long now = System.currentTimeMillis();
                        if (firstNullTime == 0) {
                            firstNullTime = now;
                        } else {
                            if (now - firstNullTime >= nullExitTime) {
                                exit = true;
                            }
                        }
                    }

                    checkEnd = true;
                    dataFile.seek(dataFile.getFilePointer() - 1);
                    lineEndChar = dataFile.read();

                } else {
                    firstNullTime = 0;
                }
                if (show != null) {
                    show = new String(show.getBytes(StandardCharsets.ISO_8859_1), charsetName);
                    if (checkEnd) {
                        checkEnd = false;
                        if (show.isEmpty()) {
                            // show = "[" + show + "empty]";
                            lastLineEnd = true;
                            consumer.accept(show, true);
                        } else {
                            //show = "[" + show + "end" + (lineEndChar == '\n') + lineEndChar + "]";
                            lastLineEnd = lineEndChar == '\n';
                            consumer.accept(show, lastLineEnd);
                        }

                    } else {
                        //   show = "[" + show + "normal]";
                        lastLineEnd = true;
                        consumer.accept(show, true);
                    }
                }
                show = line;
                if (show == null) {
                    flag = false;
                }
            } catch (Exception e) {
                e.printStackTrace();
            }

        }
        if (exit) {

            if (StringUtils.hasText(nullExitMessage)) {
                if (!lastLineEnd) {
                    consumer.accept("", true);
                }
                consumer.accept(nullExitMessage, true);
            }
            stop();
            service.schedule(() -> System.exit(0), 1500, TimeUnit.MILLISECONDS);


        }
    }


    public void stop() {
        if (file != null) {
            file = null;
        }
        if (tailScheduledFuture != null) {
            tailScheduledFuture.cancel(false);
        }
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

    public static void main(String[] args) {

        File file = new File("E:\\Work\\JAVA\\root\\pokerDragonTiger\\logs\\log.log");
        Tail tail = new Tail();
        tail.tail(file, 8);
    }
}
