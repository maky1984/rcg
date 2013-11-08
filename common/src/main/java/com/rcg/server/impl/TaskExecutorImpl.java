package com.rcg.server.impl;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.rcg.server.api.Task;
import com.rcg.server.api.TaskExecutor;

public class TaskExecutorImpl implements TaskExecutor {

	private static final Logger logger = LoggerFactory.getLogger(TaskExecutorImpl.class);

	private BlockingQueue<Task> tasks = new LinkedBlockingQueue<Task>();

	private List<Task> periodicTasks = Collections.synchronizedList(new ArrayList<Task>());
	
	private volatile boolean isStopped;
	
	private Timer timer = new Timer();

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
		timer.cancel();
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
	
	@Override
	public void addTask(final Task task, long period) {
		periodicTasks.add(task);
		addTask(task);
		timer.schedule(new TimerTask() {
			@Override
			public void run() {
				if (periodicTasks.contains(task)) {
					addTask(task);
				} else {
					this.cancel();
				}
			}
		}, period, period);
	}

	@Override
	public void removeTask(Task task) {
		periodicTasks.remove(task);
	}
}
