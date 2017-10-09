package de.linzn.jSocket.core;

import java.util.concurrent.Executors;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

public class TaskRunnable {

    public void runThreadPoolExecutor(Runnable run) {
        ThreadPoolExecutor threadRunner = new ThreadPoolExecutor(1, 1, 250L, TimeUnit.MILLISECONDS, new LinkedBlockingQueue<Runnable>());
        threadRunner.submit(run);

    }

    public void runThreadExecutor(Thread thread) {
        thread.start();
    }

    public void runSingleThreadExecutor(Runnable run) {
        Executors.newSingleThreadExecutor().execute(run);
    }
}
