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

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicLong;
import java.util.concurrent.atomic.AtomicReference;

/**
 * A helper class to keep track of or monitor the progress of a multiple level task.
 *
 * @author Miquel Sas
 */
public class Monitor {

	/**
	 * Progress information that can be requested to the monitor at any time.
	 */
	public static class Progress {
		/**
		 * Indeterminate flag.
		 */
		public boolean indeterminate;
		/**
		 * Execution start time.
		 */
		public LocalDateTime startTime;
		/**
		 * Time when the progress information was requested.
		 */
		public LocalDateTime currentTime;
		/**
		 * Last message set to the monitor.
		 */
		public String message;
		/**
		 * Work done if not indeterminate, otherwise 0.
		 */
		public long workDone;
		/**
		 * Total work if not indeterminate, otherwise 0.
		 */
		public long totalWork;
		/**
		 * Elapsed duration.
		 */
		public Duration elapsedDuration;
		/**
		 * Estimated duration.
		 */
		public Duration estimatedDuration;
		/**
		 * Pending duration if not indeterminate, otherwise null.
		 */
		public Duration pendingDuration;
		/**
		 * Expected end time if not indeterminate, otherwise null.
		 */
		public LocalDateTime endTime;
	}

	/**
	 * A level of progress.
	 */
	private static class Level {
		/**
		 * Start time.
		 */
		private final AtomicReference<LocalDateTime> startTime = new AtomicReference<>();
		/**
		 * End time.
		 */
		private final AtomicReference<LocalDateTime> endTime = new AtomicReference<>();
		/**
		 * Message.
		 */
		private final AtomicReference<String> message = new AtomicReference<>("");
		/**
		 * Work done.
		 */
		private final AtomicLong workDone = new AtomicLong();
		/**
		 * Total work.
		 */
		private final AtomicLong totalWork = new AtomicLong();
		/**
		 * Indeterminate flag.
		 */
		private final AtomicBoolean indeterminate = new AtomicBoolean();
	}

	/**
	 * Title.
	 */
	private final AtomicReference<String> title = new AtomicReference<>("");
	/**
	 * State.
	 */
	private final AtomicReference<State> state = new AtomicReference<>();
	/**
	 * List of levels.
	 */
	private final List<Level> levels = new ArrayList<>();

	/**
	 * Constructor setting only one level.
	 */
	public Monitor() {
		levels.add(new Level());
	}
	/**
	 * Constructor setting the number of levels.
	 *
	 * @param levels The number of levels.
	 */
	public Monitor(int levels) {
		if (levels <= 0) {
			throw new IllegalArgumentException("Invalid number of levels " + levels);
		}
		while (--levels >= 0) {
			this.levels.add(new Level());
		}
	}

	/**
	 * Return the size or number of levels.
	 *
	 * @return The number of levels.
	 */
	public int size() {
		return levels.size();
	}

	/**
	 * Notify that the default task level has started.
	 */
	public void notifyStart() {
		notifyStart(0);
	}
	/**
	 * Notify that the task level has started.
	 *
	 * @param index The level index.
	 */
	public void notifyStart(int index) {
		Level level = levels.get(index);
		level.startTime.set(LocalDateTime.now());
		level.endTime.set(null);
		level.message.set("");
		level.workDone.set(0);
		level.totalWork.set(0);
		level.indeterminate.set(false);
	}

	/**
	 * Notify that the default task level has ended.
	 */
	public void notifyEnd() {
		notifyEnd(0);
	}
	/**
	 * Notify that the task level has ended.
	 *
	 * @param index The level index.
	 */
	public void notifyEnd(int index) {
		levels.get(index).endTime.set(LocalDateTime.now());
	}

	/**
	 * Notify that the default task level is indeterminate.
	 *
	 * @param indeterminate A boolean that indicates whether the level is indeterminate.
	 */
	public void notifyIndeterminate(boolean indeterminate) {
		notifyIndeterminate(0, indeterminate);
	}
	/**
	 * Notify that the task level is indeterminate.
	 *
	 * @param index         The task level.
	 * @param indeterminate A boolean that indicates whether the level is indeterminate.
	 */
	public void notifyIndeterminate(int index, boolean indeterminate) {
		levels.get(index).indeterminate.set(indeterminate);
	}

	/**
	 * Notify the default task level message.
	 *
	 * @param message The message.
	 */
	public void notifyMessage(String message) {
		notifyMessage(0, message);
	}
	/**
	 * Notify the task level message.
	 *
	 * @param index   The index.
	 * @param message The message.
	 */
	public void notifyMessage(int index, String message) {
		levels.get(index).message.set(message);
	}

	/**
	 * Notify the work progress of the default level.
	 *
	 * @param workIncrease The work increase or delta.
	 * @param totalWork    The total work.
	 */
	public void notifyProgress(long workIncrease, long totalWork) {
		notifyProgress(0, workIncrease, totalWork);
	}
	/**
	 * Notify the work progress.
	 *
	 * @param index        The index of the level.
	 * @param workIncrease The work increase or delta.
	 * @param totalWork    The total work.
	 */
	public void notifyProgress(int index, long workIncrease, long totalWork) {
		levels.get(index).workDone.addAndGet(workIncrease);
		levels.get(index).totalWork.set(totalWork);
	}

	/**
	 * Notify the state. The state is global and not depends on the level.
	 *
	 * @param state The new state.
	 */
	public void notifyState(State state) {
		this.state.set(state);
	}
	/**
	 * Notify the title. The title is global and not depends on the level.
	 *
	 * @param title The new title.
	 */
	public void notifyTitle(String title) {
		this.title.set(title);
	}

	/**
	 * Return the current progress of the default level.
	 *
	 * @return The progress of the default level.
	 */
	public Progress getProgress() { return getProgress(0); }
	/**
	 * Return the current progress track of the given task level.
	 *
	 * @param index The task level index.
	 * @return The level progress.
	 */
	public Progress getProgress(int index) {
		Level level = levels.get(index);
		Progress progress = new Progress();
		progress.currentTime = LocalDateTime.now();
		progress.startTime = level.startTime.get();
		if (progress.startTime != null) {
			progress.message = level.message.get();
			progress.workDone = level.workDone.get();
			progress.totalWork = level.totalWork.get();
			progress.indeterminate = level.indeterminate.get();
			progress.elapsedDuration = Duration.between(progress.startTime, progress.currentTime);
			if (!progress.indeterminate) {
				double workCalc = progress.workDone > 0 ? Long.valueOf(progress.workDone).doubleValue() : 1.0;
				double elapsed = Long.valueOf(progress.elapsedDuration.toMillis()).doubleValue();
				long estimated = Double.valueOf(elapsed * progress.totalWork / workCalc).longValue();
				long pending = estimated - progress.elapsedDuration.toMillis();
				progress.estimatedDuration = Duration.ofMillis(estimated);
				progress.pendingDuration = Duration.ofMillis(pending);
				progress.endTime = progress.startTime.plus(progress.estimatedDuration);
			} else {
				progress.estimatedDuration = null;
				progress.pendingDuration = null;
				progress.endTime = null;
			}
		}
		return progress;
	}

	/**
	 * Return the title.
	 *
	 * @return The task title.
	 */
	public String getTitle() {
		return title.get();
	}
	/**
	 * Return the state.
	 *
	 * @return The task state.
	 */
	public State getState() {
		return state.get();
	}
}