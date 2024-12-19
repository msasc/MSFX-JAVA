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

package msfx.ztrash.task.timer;

import java.util.TimerTask;

/**
 * A timer task constructed passing the runnable.
 *
 * @author Miquel Sas
 */
public class TimerTaskRun extends TimerTask {
	/**
	 * The runnable object.
	 */
	private Runnable action;
	/**
	 * Constructor.
	 *
	 * @param action The runnable object.
	 */
	public TimerTaskRun(Runnable action) {
		this.action = action;
	}
	/**
	 * Do execute the task.
	 */
	@Override
	public void run() {
		action.run();
	}
}
