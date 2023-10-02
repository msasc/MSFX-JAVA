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

import javafx.collections.ObservableList;
import javafx.geometry.Bounds;
import javafx.scene.Node;
import javafx.scene.layout.Background;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
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
	 * @param font   The optional font.
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
	/**
	 * Returns the text component.
	 *
	 * @param str The string.
	 * @return The text component.
	 */
	public static Text getText(String str) {
		return getText(null, str, null);
	}
	/**
	 * Returns the text component.
	 *
	 * @param str  The string.
	 * @param font Optional font.
	 * @return The text component.
	 */
	public static Text getText(String str, Font font) {
		return getText(null, str, font);
	}
	/**
	 * Returns the text component.
	 *
	 * @param id   Optional id.
	 * @param str  The string.
	 * @param font Optional font.
	 * @return The text component.
	 */
	public static Text getText(String id, String str, Font font) {
		Text text = new Text(str);
		if (font != null) text.setFont(font);
		if (id != null) text.setId(id);
		return text;
	}
	/**
	 * Remove the chid with the given ID from the list.
	 *
	 * @param id       The id of the child.
	 * @param children The list of children.
	 */
	public static Node remove(String id, ObservableList<Node> children) {
		for (int i = children.size() - 1; i >= 0; i--) {
			Node child = children.get(i);
			if (child.getId() != null && child.getId().equals(id)) {
				children.remove(i);
				return child;
			}
		}
		return null;
	}
}
