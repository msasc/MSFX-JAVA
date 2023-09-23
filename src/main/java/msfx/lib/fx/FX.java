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

package msfx.lib.fx;

import javafx.geometry.Bounds;
import javafx.scene.text.Font;
import javafx.scene.text.Text;
import javafx.scene.text.TextBoundsType;

/**
 * JavaFX utility functions.
 *
 * @author Miquel Sas
 */
public class FX {
	/**
	 * Return the logical string bounds for text sizes calculations.
	 *
	 * @param string The string.
	 * @param font The optional font.
	 * @return The bounds.
	 */
	public static Bounds getStringBounds(String string, Font font) {
		Text text = new Text(string);
		if (font != null) {
			text.setFont(font);
		}
		text.setBoundsType(TextBoundsType.LOGICAL_VERTICAL_CENTER);
		return text.getLayoutBounds();
	}
}
