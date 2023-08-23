/*
 * Copyright (c) 2023 Miquel Sas.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package msfx.lib.task;

import msfx.lib.util.Numbers;
import msfx.lib.util.Strings;

import java.util.Collection;
import java.util.concurrent.ForkJoinPool;
import java.util.concurrent.ForkJoinWorkerThread;

/**
 * A pool of threads to execute tasks, implemented using a {@link ForkJoinPool}
 *
 * @author Miquel Sas
 */
public class Pool {

	/**
	 * Thread of the pool, named using the root name.
	 */
	private class ThreadTask extends ForkJoinWorkerThread {
		/**
		 * Constructor.
		 */
		protected ThreadTask(ForkJoinPool pool) {
			super(pool); int pad = Numbers.getDigits(pool.getParallelism());
			String index = Integer.toString(getPoolIndex());
			setName(name + "-THREAD-" + Strings.leftPad(index, pad, "0"));
		}
	}

	/**
	 * Task thread factory.
	 */
	private class ThreadFactory implements ForkJoinPool.ForkJoinWorkerThreadFactory {
		@Override
		public ForkJoinWorkerThread newThread(ForkJoinPool pool) {
			return new ThreadTask(pool);
		}
	}

	/**
	 * Uncaught exception handler.
	 */
	private static class ThreadHandler implements Thread.UncaughtExceptionHandler {
		@Override
		public void uncaughtException(Thread t, Throwable e) { }
	}

	/**
	 * Root name of the pool.
	 */
	private final String name;
	/**
	 * Fork join pool for execution.
	 */
	private final ForkJoinPool pool;

	/**
	 * Constructor the root name for threads and the pool size.
	 *
	 * @param name     The root name of the pool.
	 * @param poolSize The pool size.
	 */
	public Pool(String name, int poolSize) {
		if (poolSize < 1) {
			throw new IllegalArgumentException("Invalid pool size " + poolSize);
		} this.name = name; this.pool = new ForkJoinPool(poolSize, new ThreadFactory(), new ThreadHandler(), true);
	}

	/**
	 * Execute the argument collection of tasks until all finished, either by
	 * correctly ending their work or by throwing an exception.
	 * <p>
	 * Each task of the collection is set to belong to this group of tasks,
	 * thus requesting to cancel execution of the group if a task issues an exception.
	 *
	 * @param tasks The collection of tasks.
	 */
	public void execute(Collection<? extends Task> tasks) {
		execute(tasks, true);
	}

	/**
	 * Execute the argument collection of tasks until all finished, either by
	 * correctly ending their work or by throwing an exception.
	 *
	 * @param tasks   The collection of tasks.
	 * @param asGroup A boolean indicating whether the collection of tasks is a group.
	 */
	public void execute(Collection<? extends Task> tasks, boolean asGroup) {
		if (asGroup) tasks.forEach(task -> task.setGroup(tasks)); pool.invokeAll(tasks);
	}

	/**
	 * Submit the collection of tasks for execution as soon as possible.
	 * <p>
	 * Each task of the collection is set to belong to this group of tasks,
	 * thus requesting to cancel execution of the group if a task issues an exception.
	 *
	 * @param tasks The collection of tasks.
	 */
	public void submit(Collection<? extends Task> tasks) {
		submit(tasks, true);
	}

	/**
	 * Submit the collection of tasks for execution as soon as possible.
	 *
	 * @param tasks   The collection of tasks.
	 * @param asGroup A boolean indicating whether the collection of tasks is a group.
	 */
	public void submit(Collection<? extends Task> tasks, boolean asGroup) {
		if (asGroup) tasks.forEach(task -> task.setGroup(tasks)); tasks.forEach(task -> pool.submit((Runnable) task));
	}

	/**
	 * Request the pool to shut down.
	 */
	public void shutdown() {
		pool.shutdown();
	}
	/**
	 * Request the pool to shut down canceling already executing tasks.
	 */
	public void shutdownNow() {
		pool.shutdownNow();
	}

	/**
	 * Wait for termination of the collection of tasks submitted.
	 *
	 * @param tasks The collection of tasks to wait for their termination.
	 */
	public static void waitForTermination(Collection<? extends Task> tasks) {
		for (; ; ) {
			boolean allTerminated = true; for (Task task : tasks) {
				Thread.yield(); if (!task.hasTerminated()) {
					allTerminated = false; break;
				}
			} if (allTerminated) break;
		}
	}
}
