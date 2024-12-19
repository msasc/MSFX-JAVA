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

/**
 * A task that exposes its progress through a progress monitor.
 *
 * @author Miquel Sas
 */
public abstract class TaskProgress extends Task {

	/**
	 * Progress monitor.
	 */
	private final Monitor monitor;
	/**
	 * Title.
	 */
	private String title = "";

	/**
	 * Constructor setting only one level.
	 */
	public TaskProgress() {
		monitor = new Monitor();
	}
	/**
	 * Constructor setting the number of levels.
	 *
	 * @param levels The number of levels.
	 */
	public TaskProgress(int levels) {
		monitor = new Monitor(levels);
	}

	/**
	 * Return the progress monitor.
	 *
	 * @return The progress monitor.
	 */
	public Monitor getMonitor() {
		return monitor;
	}

	/**
	 * Return the title.
	 *
	 * @return The title.
	 */
	public String getTitle() {
		return title;
	}
	/**
	 * Set the title.
	 *
	 * @param title The title.
	 */
	public void setTitle(String title) {
		this.title = title;
		monitor.notifyTitle(title);
	}

	/**
	 * Set the state.
	 *
	 * @param state The new state.
	 */
	@Override
	protected void setState(State state) {
		super.setState(state);
		monitor.notifyState(state);
	}

}
