/*
 * Copyright (c) 2023-2024 Miquel Sas.
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

package msfx.ztrash.task;

import java.util.Collection;
import java.util.concurrent.Callable;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicReference;

/**
 * @author Miquel Sas
 */
public abstract class Task implements Runnable, Callable<Void> {

	/**
	 * The state, READY on creation.
	 */
	private final AtomicReference<State> state = new AtomicReference<>(State.READY);
	/**
	 * A boolean that indicates whether the execution should be cancelled.
	 */
	private final AtomicBoolean cancelRequested = new AtomicBoolean(false);
	/**
	 * Exception thrown when the execution failed.
	 */
	private Throwable exception = null;

	/**
	 * Optional group of tasks to which this task belongs. If the group has been set and this tasks
	 * fails, a request of cancel is send to all the tasks of the group.
	 */
	private Collection<? extends Task> group;

	/**
	 * Constructor.
	 */
	public Task() {
	}

	/**
	 * Execute the task.
	 *
	 * @throws Throwable If an error occurs.
	 */
	public abstract void execute() throws Throwable;
	/**
	 * Optional, called before any other action when execution starts.
	 */
	protected void beforeExecute() {
	}
	/**
	 * Optional, called after execution ends.
	 */
	protected void afterExecute() {
	}

	/**
	 * Launch the execution-tracing state.
	 */
	protected void executeTask() {

		/* Initially there is no exception. */
		exception = null;

		/* Before execution. */
		beforeExecute();

		/* Register start time and initialize execution. */
		setState(State.RUNNING);

		/* Execute and register an eventual exception. */
		try {
			execute();
		} catch (Throwable exc) {
			exception = exc;
		}

		/* Register final state. */
		if (exception != null) {
			setState(State.FAILED);
			if (group != null) {
				group.forEach(Task::requestCancel);
			}
		} else if (state.get() != State.CANCELLED) {
			setState(State.SUCCEEDED);
		}

		/* After execution. */
		afterExecute();

		/* Ensure cancel requested does not remain set, the state is enough. */
		cancelRequested.set(false);
	}

	/**
	 * Callable implementation.
	 */
	@Override
	public final Void call() {
		executeTask();
		return null;
	}
	/**
	 * Runnable implementation.
	 */
	@Override
	public final void run() {
		executeTask();
	}

	/**
	 * Return the exception.
	 *
	 * @return The eventual exception.
	 */
	public Throwable getException() {
		return exception;
	}

	/**
	 * Returns the state.
	 *
	 * @return The state.
	 */
	public State getState() {
		return state.get();
	}
	/**
	 * Sets the state.
	 *
	 * @param state The new state.
	 */
	protected void setState(State state) {
		this.state.set(state);
	}

	/**
	 * Indicate that the task has been already cancelled. Extenders should
	 * call this method when acquainted of a request of cancel, and seamlessly
	 * exit the main loop.
	 */
	protected void setCancelled() {
		setState(State.CANCELLED);
	}

	/**
	 * Request the task to try to seamlessly cancel execution.
	 */
	public void requestCancel() {
		cancelRequested.set(true);
	}
	/**
	 * Returns a boolean indicating whether cancel execution has been requested, and
	 * thus the task should seamlessly stop processing.
	 *
	 * @return A boolean indicating whether cancel has been requested.
	 */
	public boolean shouldCancel() {
		if (Thread.currentThread().isInterrupted()) {
			return true;
		}
		return cancelRequested.get();
	}

	/**
	 * Check whether the task should be cancelled and, if so, cancel it and return true,
	 * otherwise return false. A normal use could be <code>if (cancel()) break;</code>
	 *
	 * @return A boolean indicating that the task has been cancelled if so requested.
	 */
	public boolean cancel() {
		if (shouldCancel()) {
			setCancelled();
			return true;
		}
		return false;
	}

	/**
	 * Returns a boolean that indicates whether the task is ready and waiting to be executed.
	 *
	 * @return A boolean.
	 */
	public boolean isReady() {
		return state.get() == State.READY;
	}
	/**
	 * Returns a boolean that indicates whether the task is running.
	 *
	 * @return A boolean.
	 */
	public boolean isRunning() {
		return state.get() == State.RUNNING;
	}
	/**
	 * Returns a boolean that indicates whether the task has terminated successfully.
	 *
	 * @return A boolean
	 */
	public boolean hasSucceeded() {
		return state.get() == State.SUCCEEDED;
	}
	/**
	 * Returns a boolean that indicates whether the task was cancelled.
	 *
	 * @return A boolean.
	 */
	public boolean wasCancelled() {
		return state.get() == State.CANCELLED;
	}
	/**
	 * Returns a boolean that indicates whether the task has failed and thrown an exception.
	 *
	 * @return A boolean.
	 */
	public boolean hasFailed() {
		return state.get() == State.FAILED;
	}
	/**
	 * Returns a boolean that indicates whether the task has terminated.
	 *
	 * @return A boolean.
	 */
	public boolean hasTerminated() {
		State s = state.get();
		return (s == State.SUCCEEDED || s == State.CANCELLED || s == State.FAILED);
	}

	/**
	 * Reinitialize the task setting its state to ready.This method is not strictly
	 * necessary to run the task again.
	 */
	public void reinitialize() {
		exception = null;
		cancelRequested.set(false);
		setState(State.READY);
	}

	/**
	 * Set the group of tasks to which this task belongs.
	 *
	 * @param group The group or collection of tasks.
	 */
	public void setGroup(Collection<? extends Task> group) {
		this.group = group;
	}
}
