package com.driver;

import java.util.Scanner;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;

public class EventMonitoringServer {
	private static final int THREAD_POOL_SIZE = 5;
    private static final ExecutorService executorService = Executors.newFixedThreadPool(THREAD_POOL_SIZE);
    private static final CountDownLatch shutdownLatch = new CountDownLatch(1);
    private static final AtomicBoolean highMagnitudeEventDetected = new AtomicBoolean(false);

    public static void main(String[] args) {
        try {
            startServer();
        } catch (InterruptedException e) {
            e.printStackTrace();
        } finally {
            stopServer();
        }
    }
    

    private static void startServer() throws InterruptedException {
    	// your code goes here
        System.out.println("Event monitoring server started. Enter 'shutdown' to stop the server manually.");

        new Thread(() -> {
            while(true) {
                String input = getUserInput();
                if ("shutdown".equalsIgnoreCase(input)) {
                    System.out.println("Shutting down the server gracefully...");
                    shutdownLatch.countDown();
                    break;
                }
            }
        }).start();

        for(int i=0; i<=10; i++){
            int eventid = i;
            executorService.submit(()-> processEvent(eventid));
            Thread.sleep(500);
        }
        waitForShutdownSignal();
    }

    private static void processEvent(int eventId) {
    	// your code goes here
        if(eventId == 6 && highMagnitudeEventDetected.compareAndSet(false, true)){
            System.out.println("High magnitude event detected!");
            shutdownLatch.countDown();
            return;
        }

        System.out.println("Event " + eventId + " processed.");
    }

    private static void waitForShutdownSignal() throws InterruptedException {
    	// your code goes here
        shutdownLatch.await();
    }

    private static String getUserInput() {
    	Scanner scanner = new Scanner(System.in);
        return scanner.nextLine();
    }

    private static void stopServer() {
    	// your code goes here
        executorService.shutdown();
        try{
            if(!executorService.awaitTermination(5, TimeUnit.SECONDS)){
                executorService.shutdownNow();
            }
        }catch(InteruptedException e){
            executorService.shutdownNow();
        }
    }
}
