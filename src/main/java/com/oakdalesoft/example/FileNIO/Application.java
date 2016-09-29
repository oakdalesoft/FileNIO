package com.oakdalesoft.example.FileNIO;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


import javax.annotation.PostConstruct;
import java.io.File;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;
import java.nio.channels.FileLock;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.concurrent.*;

/**
 * Created by Alex on 28/09/2016.
 */
public class Application {

    Logger log = LoggerFactory.getLogger(Application.class);

    public ExecutorService pool = Executors.newFixedThreadPool(5);

    public Path tempFile;

    public CompletableFuture<String> work;

    public CompletableFuture<String> play;

    @PostConstruct
    void startUp() {
        try{this.tempFile = File.createTempFile("temp", ".buffer", Paths.get(".").toFile()).toPath();}
        catch (IOException e) {}

        this.work = CompletableFuture.supplyAsync(() -> {
            log.info("Writing data to {}", this.tempFile.getFileName());
            FileChannel fileChannel = null;
            try {
                fileChannel = FileChannel.open(this.tempFile, StandardOpenOption.WRITE,
                        StandardOpenOption.APPEND);
            } catch (IOException e) {
                e.printStackTrace();
            }
            StringBuffer input = new StringBuffer("**** Start of file ****\n");
            for(int i = 1; i <= 1000; i++) {
                input.append("---- Added line number ").append(i).append(" ----\n");
                if(i%100 == 0) {
                    //log.info("Writing to file with {}", input.toString());
                    ByteBuffer buffer = ByteBuffer.wrap(input.toString().getBytes());
                    try{//fileChannel.position(fileChannel.size() - 1); // positions at the end of file
                        FileLock lock = fileChannel.lock(); // gets an exclusive lock
                        int written = fileChannel.write(buffer);
                        log.info("Wrote {} bytes to file", written);
                        lock.release();
                        } catch (IOException e) { e.printStackTrace(); }
                    input.delete(0, input.length());
                }
            }
            try {
                fileChannel.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
            return "Done";
        });
        reader();
    }

    public void reader() {
        FileChannel fileChannel = null;
        try {
            fileChannel = FileChannel.open(this.tempFile, StandardOpenOption.READ);
        } catch (IOException e) {
            e.printStackTrace();
        }
        ByteBuffer buffer = ByteBuffer.allocate(20);
        int noOfBytesRead = 0;
        try {
            noOfBytesRead = fileChannel.read(buffer);
        } catch (IOException e) {
            e.printStackTrace();
        }
        System.out.println("Buffer contents: ");

        while (noOfBytesRead != -1) {

            buffer.flip();
            while (buffer.hasRemaining()) {
            log.info(String.valueOf((char)buffer.get()));
            }
            buffer.clear();
            try {
                Thread.sleep(10);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            try {
                noOfBytesRead = fileChannel.read(buffer);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        try {
            fileChannel.close(); // also releases the lock
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


}
