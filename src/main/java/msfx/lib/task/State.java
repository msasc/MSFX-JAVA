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

/**
 * Task states.
 *
 * @author Miquel Sas
 */
public enum State {
	/**
	 * The task is ready to be executed.
	 */
	READY,
	/**
	 * The task is running.
	 */
	RUNNING,
	/**
	 * The task has completed successfully.
	 */
	SUCCEEDED,
	/**
	 * The task has been cancelled, usually as a result of an external request.
	 */
	CANCELLED,
	/**
	 * The task has failed, an exception can be retrieved from the task.
	 */
	FAILED
}
