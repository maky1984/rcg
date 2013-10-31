package com.rcg.server.impl;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.rcg.server.api.Task;
import com.rcg.server.api.TaskExecutor;

public class TaskExecutorImpl implements TaskExecutor {

	private static final Logger logger = LoggerFactory.getLogger(TaskExecutorImpl.class);

	private BlockingQueue<Task> tasks = new LinkedBlockingQueue<Task>();

	private volatile boolean isStopped;

	public void start() {
		logger.info("Starting executor");
		Thread thread = new Thread(new Runnable() {

			@Override
			public void run() {
				logger.info("Executor started");
				while (!isStopped) {
					try {
						Task task = tasks.take();
						logger.info("Strating task:" + task);
						task.run();
						logger.info("Ended task:" + task);
					} catch (InterruptedException e) {
						logger.error("InterruptedException in executor", e);
					} catch (Throwable t) {
						logger.error("Task throws exception:", t);
					}
				}
			}
		}, TaskExecutorImpl.class.getName());
		thread.start();
	}

	public void stop() {
		logger.info("Executor stopped");
		isStopped = true;
	}

	public void addTask(Task task) {
		if (!isStopped) {
			try {
				tasks.put(task);
			} catch (InterruptedException e) {
				logger.error("InterruptedException durring add to executor", e);
			}
		} else {
			logger.error("Executor is stopped. Cant add task:" + task);
		}
	}

}
