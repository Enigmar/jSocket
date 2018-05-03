/*
 * Copyright (C) 2018. Niklas Linz - All Rights Reserved
 * You may use, distribute and modify this code under the
 * terms of the LGPLv3 license, which unfortunately won't be
 * written for another century.
 *
 * You should have received a copy of the LGPLv3 license with
 *  this file. If not, please write to: niklas.linz@enigmar.de
 */

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
