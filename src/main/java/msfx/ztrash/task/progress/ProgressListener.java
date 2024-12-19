/*
 * Copyright (c) 2021-2024 Miquel Sas.
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
package msfx.ztrash.task.progress;

import msfx.ztrash.task.State;

/**
 * Listener of the progress of tasks and processes. Note that progress bar zero is the main progress
 * bar that indicates whether the whole listener is determinate or not.
 *
 * @author Miquel Sas
 */
public interface ProgressListener {
	/**
	 * Notify that the calling task has started.
	 */
	void notifyStart();
	/**
	 * Notify that the calling task has finished.
	 */
	void notifyEnd();
	/**
	 * Notify a progress message, normally when the task is indeterminate.
	 *
	 * @param index   The message index.
	 * @param message The message.
	 */
	void notifyMessage(int index, String message);
	/**
	 * Notify an increase in the work done. Zero or negative work increases or total work are not
	 * considered and skipped. Additionally, in an indeterminate state, work and total are also
	 * skipped.
	 *
	 * @param index        Progress bar index.
	 * @param workIncrease Work increase.
	 * @param totalWork    Total work.
	 */
	void notifyProgress(int index, double workIncrease, double totalWork);
	/**
	 * Notify the state.
	 *
	 * @param state The state.
	 */
	void notifyState(State state);
	/**
	 * Set that the progress bar is indeterminate.
	 *
	 * @param index         The index of the progress bar.
	 * @param indeterminate A boolean.
	 */
	void setIndeterminate(int index, boolean indeterminate);
	/**
	 * Reset the progress bar to zero work done.
	 *
	 * @param index The index of the progress bar.
	 */
	void resetProgress(int index);
	/**
	 * Initialize and set up the listener to manage the argument number of labels and progress bars.
	 *
	 * @param numLabels Number of labels.
	 * @param numBars   Number of progress bars.
	 */
	void setup(int numLabels, int numBars);
}
