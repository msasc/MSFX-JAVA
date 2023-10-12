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

package msfx.lib.util.funtion;

/**
 * Functional interfaces for functions with N-parameters.
 *
 * @author Miquel Sas
 */
public interface Function {
	/**
	 * @param <R>  Return type.
	 */
	public interface P0<R> {
		R call();
	}
	/**
	 * @param <R>  Return type.
	 * @param <P1> Type of parameter 1
	 */
	public interface P1<R, P1> {
		R call(P1 p1);
	}
	/**
	 * @param <R>  Return type.
	 * @param <P1> Type of parameter 1
	 * @param <P2> Type of parameter 2
	 */
	public interface P2<R, P1, P2> {
		R call(P1 p1, P2 p2);
	}
	/**
	 * @param <R>  Return type.
	 * @param <P1> Type of parameter 1
	 * @param <P2> Type of parameter 2
	 * @param <P3> Type of parameter 3
	 */
	public interface P3<R, P1, P2, P3> {
		R call(P1 p1, P2 p2, P3 p3);
	}
	/**
	 * @param <R>  Return type.
	 * @param <P1> Type of parameter 1
	 * @param <P2> Type of parameter 2
	 * @param <P3> Type of parameter 3
	 * @param <P4> Type of parameter 4
	 */
	public interface P4<R, P1, P2, P3, P4> {
		R call(P1 p1, P2 p2, P3 p3, P4 p4);
	}
}
