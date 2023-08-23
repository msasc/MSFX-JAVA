/*
 * Copyright (c) 2021. Miquel Sas
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <https://www.gnu.org/licenses/>.
 */
package msfx.lib.task.progress;

import msfx.lib.task.State;

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
