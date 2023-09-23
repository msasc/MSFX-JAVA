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

package msfx.mkt.chart;

import javafx.application.Platform;
import javafx.beans.binding.Bindings;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.geometry.Bounds;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.shape.Line;
import javafx.scene.text.Font;
import javafx.scene.text.Text;
import javafx.scene.text.TextBoundsType;
import javafx.scene.text.TextFlow;
import msfx.lib.fx.FX;
import msfx.lib.task.Pool;
import msfx.lib.task.Task;
import msfx.lib.util.Numbers;
import msfx.mkt.DataSource;

import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

/**
 * The chart component that manages the vertical layout of several plot areas and a horizontal axis
 * with the timeline at the bottom.
 * <p>
 * All plot areas panes have the same with, as well as all the vertical axes panes. Thus, the width
 * and the horizontal margins of the plot areas should be located at the chart frame level.
 * <p>
 * The vertical margins apply to each plot area and its associated vertical axis, thus they should
 * be located at the plot frame level.
 *
 * @author Miquel Sas
 */
public class ChartFrame {

	/**
	 * A border pane that contains a plot info pane in the top, a plot are in the center and a
	 * vertical axis in the right.
	 */
	private class PlotFrame {

		/**
		 * Effective border pane that contains the info pane, the plot area pane and the vertical
		 * axis.
		 */
		private final BorderPane pane;
		/**
		 * The plot area where the data charts are plotted.
		 */
		private final PlotArea plotArea;
		/**
		 * The associated vertical axis.
		 */
		private final VAxis vaxis;

		/**
		 * Top margin.
		 */
		private double marginTop;
		/**
		 * Bottom margin.
		 */
		private double marginBottom;
		/**
		 * Minimum value within a start and an end index.
		 */
		private double minimumValue;
		/**
		 * Maximum value within a start and an end index.
		 */
		private double maximumValue;

		private PlotScale scale;

		/**
		 * List of data plotters.
		 */
		private final List<DataPlotter> plotters;

		/**
		 * Constructor.
		 */
		private PlotFrame() {

			plotters = new ArrayList<>();
			scale = PlotScale.LOGARITHMIC;

			plotArea = new PlotArea();
			vaxis = new VAxis();
			pane = new BorderPane();
			pane.setCenter(plotArea.pane);
			pane.setRight(vaxis.pane);
		}

		/**
		 * Calculates the minimum and maximum values within the range indexes.
		 *
		 * @param startIndex Start index, included.
		 * @param endIndex   End index included.
		 */
		private void calculateMinMaxValues(int startIndex, int endIndex) {

			int dataSize = plotData.getDataSize();
			minimumValue = Numbers.MAX_DOUBLE;
			maximumValue = Numbers.MIN_DOUBLE;

			for (int index = startIndex; index <= endIndex; index++) {
				if (index < 0) continue;
				if (index >= dataSize) continue;
				for (DataPlotter plotter : plotters) {
					double[] values = plotter.getValues(index);
					for (double value : values) {
						if (value < minimumValue) {
							minimumValue = value;
						}
						if (value > maximumValue) {
							maximumValue = value;
						}
					}
				}
			}
		}
		/**
		 * Calculate the vertical margins.
		 */
		private void calculateVerticalMargins() {
			marginTop = Numbers.round(plotArea.pane.getHeight() * insets.getTop(), 0);
			marginBottom = Numbers.round(plotArea.pane.getHeight() * insets.getBottom(), 0);
		}
		/**
		 * Calculates the coordinate Y given the value and the scale of this area.
		 *
		 * @param value The value.
		 * @return The coordinate Y.
		 */
		private double getCoordinateY(double value) {

			double minValue = minimumValue;
			double maxValue = maximumValue;
			if (scale == PlotScale.LOGARITHMIC) {
				minValue = Math.log1p(minValue);
				maxValue = Math.log1p(maxValue);
				value = Math.log1p(value);
			}
			if (scale == PlotScale.PERCENTAGE) {
				// TODO pending to implement PlotScale.PERCENTAGE.
			}

			double valueFactor = (value - minValue) / (maxValue - minValue);
			double relativeY = 0;
			double chartHeight = pane.getHeight() - marginTop - marginBottom;
			if (Double.isFinite(value) && Double.isFinite(valueFactor)) {
				relativeY = valueFactor * chartHeight;
			}

			double coordinateY = marginTop + chartHeight - relativeY;
			return coordinateY;
		}
		/**
		 * Returns a suitable plot context.
		 *
		 * @return The plot context to draw.
		 */
		private PlotContext getContext() {
			return new Context(this);
		}
		/**
		 * Plot the frame.
		 */
		private void plot() {
			PlotContext context = getContext();
			for (DataPlotter plotter : plotters) {
				plotter.plot(context);
			}
		}
	}

	/**
	 * The chart component where data and drawings are plotted.
	 */
	private class PlotArea {
		/**
		 * The pane that contains the bound canvas.
		 */
		private final Pane pane;
		/**
		 * The canvas to paint on.
		 */
		private final Canvas canvas;
		/**
		 * Constructor.
		 */
		private PlotArea() {
			pane = new Pane();
			canvas = new Canvas();
			pane.getChildren().add(canvas);
			canvas.widthProperty().bind(Bindings.selectDouble(canvas.parentProperty(), "width"));
			canvas.heightProperty().bind(Bindings.selectDouble(canvas.parentProperty(), "height"));
		}
	}

	/**
	 * The plot info component contains a text flow control that expands to the right and, at the
	 * right, a close button.
	 */
	private class PlotInfo {
		/**
		 * Grid that contains the cursor information and the buttons.
		 */
		private final GridPane pane = new GridPane();
	}

	/**
	 * Vertical axis, the component where scaled values of the plot are shown.
	 */
	private class VAxis {
		/**
		 * The pane that contains the bound canvas.
		 */
		private final Pane pane;
		/**
		 * The canvas to paint on.
		 */
		private final Canvas canvas;
		/**
		 * Constructor.
		 */
		private VAxis() {
			pane = new Pane();
			canvas = new Canvas();
			pane.getChildren().add(canvas);
			canvas.widthProperty().bind(Bindings.selectDouble(canvas.parentProperty(), "width"));
			canvas.heightProperty().bind(Bindings.selectDouble(canvas.parentProperty(), "height"));
		}
	}

	/**
	 * Horizontal axis, the component where the timeline is shown.
	 */
	private class XAxis {
		/**
		 * The pane that contains the bound canvas.
		 */
		private final Pane pane;
		/**
		 * The canvas to paint on.
		 */
		private final Canvas canvas;
		/**
		 * Constructor.
		 */
		private XAxis() {
			pane = new Pane();
			canvas = new Canvas();
			pane.getChildren().add(canvas);
			canvas.widthProperty().bind(Bindings.selectDouble(canvas.parentProperty(), "width"));
			canvas.heightProperty().bind(Bindings.selectDouble(canvas.parentProperty(), "height"));
		}
	}

	/**
	 * Width and height change listener to respond to size events.
	 */
	private class SizeListener implements ChangeListener<Number> {
		@Override
		public void changed(
				ObservableValue<? extends Number> observable,
				Number oldValue,
				Number newValue) {
			Platform.runLater(() -> { plot(); });
		}
	}

	/**
	 * The plot context to pass to data plotters.
	 */
	public class Context implements PlotContext {
		/**
		 * The associated plot frame.
		 */
		private PlotFrame plotFrame;
		/**
		 * Constructor.
		 *
		 * @param plotFrame The plot frame.
		 */
		private Context(PlotFrame plotFrame) {
			this.plotFrame = plotFrame;
		}

		/**
		 * Returns a suitable pool to prepare plot calculations concurrently.
		 *
		 * @return The pool.
		 */
		public Pool getPlotPool() {
			return plotPool;
		}

		/**
		 * Returns the plot data.
		 *
		 * @return The plot data.
		 */
		public PlotData getPlotData() {
			return plotData;
		}

		/**
		 * Returns the graphics context.
		 *
		 * @return The graphics context.
		 */
		public GraphicsContext getGraphicsContext() {
			return plotFrame.plotArea.canvas.getGraphicsContext2D();
		}

		/**
		 * Returns the width of the plot area.
		 *
		 * @return The width.
		 */
		public double getWidth() {
			return plotFrame.plotArea.canvas.getWidth();
		}
		/**
		 * Returns the height of the plot area.
		 *
		 * @return The height.
		 */
		public double getHeight() {
			return plotFrame.plotArea.canvas.getHeight();
		}

		/**
		 * Returns the top margin.
		 *
		 * @return The top margin.
		 */
		public double getMarginTop() {
			return plotFrame.marginTop;
		}
		/**
		 * Returns the right margin.
		 *
		 * @return The right margin.
		 */
		public double getMarginRight() {
			return marginRight;
		}
		/**
		 * Returns the bottom margin.
		 *
		 * @return The bottom margin.
		 */
		public double getMarginBottom() {
			return plotFrame.marginBottom;
		}
		/**
		 * Returns the left margin.
		 *
		 * @return The left margin.
		 */
		public double getMarginLeft() {
			return marginLeft;
		}

		/**
		 * Returns the coordinate X.
		 *
		 * @param index The index.
		 * @return The coordinate X.
		 */
		public double getCoordinateX(int index) {
			return ChartFrame.this.getCoordinateX(index);
		}
		/**
		 * Returns the coordinate Y.
		 *
		 * @param value The value.
		 * @return The coordinate Y.
		 */
		public double getCoordinateY(double value) {
			return plotFrame.getCoordinateY(value);
		}

		/**
		 * Return the minimum value within the range of indexes.
		 *
		 * @return The minimum value.
		 */
		public double getMinimumValue() {
			return plotFrame.minimumValue;
		}
		/**
		 * Return the maximum value within the range of indexes.
		 *
		 * @return The maximum value.
		 */
		public double getMaximumValue() {
			return plotFrame.maximumValue;
		}
	}

	/**
	 * Timer task to refresh and plot when the buttons move left or right and zoom in or out are
	 * hold pressed.
	 */
	private class TaskRefresh extends TimerTask {
		/**
		 * The runnable that performs the task.
		 */
		private Runnable task;
		/**
		 * Constructor.
		 *
		 * @param task The runnable task to perform.
		 */
		private TaskRefresh(Runnable task) {
			this.task = task;
		}
		/**
		 * Do run.
		 */
		@Override
		public void run() {
			task.run();
			Platform.runLater(() -> plot());
		}
	}

	/**
	 * Timer utility class that packs the timer and the task, it is stored in the user object of the
	 * button, and cleaned when the button is released.
	 */
	private class TimerPack {
		/**
		 * The timer.
		 */
		private Timer timer;
		/**
		 * The timer task.
		 */
		private TimerTask task;
		/**
		 * Constructor.
		 *
		 * @param task The timer task.
		 */
		private TimerPack(TimerTask task) {
			this.task = task;
			this.timer = new Timer(true);
			this.timer.schedule(this.task, refreshPeriod * 4, refreshPeriod);
		}
		/**
		 * Terminate the task and the timer, called on button released.
		 */
		private void terminate() {
			task.cancel();
			timer.purge();
			timer.cancel();
			timer = null;
			task = null;
		}
	}

	/**
	 * The insets, as a unitary factor, that define the margins of the panes. The plot area has a
	 * margin all around. The vertical axis has a top and bottom margin, that are the as the ones
	 * of its correspondent plot area. The horizontal axis and the plot info area have no margins
	 * defined by these insets.
	 */
	private Insets insets = new Insets(0.05, 0.05, 0.05, 0.05);
	/**
	 * Right margin.
	 */
	private double marginRight;
	/**
	 * Left margin.
	 */
	private double marginLeft;

	/**
	 * Plot data.
	 */
	private final PlotData plotData;
	/**
	 * List of plot frames.
	 */
	private final List<PlotFrame> plotFrames;
	/**
	 * Horizontal axis.
	 */
	private final XAxis xAxis;

	/**
	 * The border pane that contains all the components of the chart frame.
	 */
	private final BorderPane pane;

	/**
	 * Width of the top toolbar pane buttons.
	 */
	private final double buttonWidth = 32;
	/**
	 * height of the top toolbar pane buttons.
	 */
	private final double buttonHeight = 24;
	/**
	 * Side of the internal frame of the button where the icon is drawn.
	 */
	private final double buttonFrame = 8;
	/**
	 * Width of the viewport that shows the relative position of the visible data. Note that the
	 * total width of the bar that shows the relative position of the visible data is three times
	 * the viewport width, and to compute the total width of the pane we have to add the normal
	 * button frame margins.
	 */
	private final double viewportWidth = 128;

	/**
	 * Refresh period in millis.
	 */
	private int refreshPeriod = 75;
	/**
	 * Pool parallelism for concurrent plot calculations.
	 */
	private int parallelism = 20;

	/**
	 * Pool the pass to data plotters.
	 */
	private Pool plotPool;

	/**
	 * Global font for all texts int this chart frame.
	 */
	private Font textFont;

	private double lastX;

	/**
	 * Constructor.
	 *
	 * @param plotter The starting data plotter.
	 */
	public ChartFrame(DataPlotter plotter) {

		plotData = new PlotData();
		plotFrames = new ArrayList<>();
		xAxis = new XAxis();
		pane = new BorderPane();

		textFont = new Font(10);

		addPlotFrame(plotter);
		setIndexesRangeFromEnd(500);

		/*
		 * Size listener lo launch the plot when the size changes.
		 */

		SizeListener sizeListener = new SizeListener();
		pane.widthProperty().addListener(sizeListener);
		pane.heightProperty().addListener(sizeListener);

		/*
		 * Flow pane on top to handle buttons to move start, end, left and right,
		 * and to zoom in and out.
		 */

		FlowPane flowPane = new FlowPane();
		flowPane.setId("FRAME-FLOW");
		BorderStroke borderStroke = new BorderStroke(
				Color.BLACK,
				BorderStrokeStyle.SOLID,
				CornerRadii.EMPTY,
				new BorderWidths(0.0, 0.0, 0.5, 0.0));
		flowPane.setBorder(new Border(borderStroke));

		/*
		 * Add the buttons to zoom in and out, and to move start, end, left and right.
		 */

		Pane buttonZoomIn = getButtonZoomIn();
		Pane buttonZoomOut = getButtonZoomOut();
		Pane buttonMoveLeft = getButtonMoveLeft();
		Pane buttonMoveRight = getButtonMoveRight();
		Pane buttonMoveStart = getButtonMoveStart();
		Pane buttonMoveEnd = getButtonMoveEnd();

		flowPane.getChildren().add(buttonZoomIn);
		flowPane.getChildren().add(buttonZoomOut);
		flowPane.getChildren().add(buttonMoveLeft);
		flowPane.getChildren().add(buttonMoveRight);
		flowPane.getChildren().add(buttonMoveStart);
		flowPane.getChildren().add(buttonMoveEnd);
		flowPane.getChildren().add(getViewPortCanvas());

		/*
		 * Text flow pane as a text info pane.
		 */

		TextFlow frameInfo = new TextFlow();
		frameInfo.setId("FRAME-INFO");
		frameInfo.setMaxHeight(buttonHeight);
		frameInfo.setMinHeight(buttonHeight);

		frameInfo.getChildren().add(getText("FRAME-TEXT-SIZE", "Size: "));
		frameInfo.getChildren().add(getText("FRAME-TEXT-SEP1", "   "));
		frameInfo.getChildren().add(getText("FRAME-TEXT-PERIODS", "Periods: "));
		frameInfo.getChildren().add(getText("FRAME-TEXT-SEP2", "   "));
		frameInfo.getChildren().add(getText("FRAME-TEXT-START", "Start: "));
		frameInfo.getChildren().add(getText("FRAME-TEXT-SEP3", "   "));
		frameInfo.getChildren().add(getText("FRAME-TEXT-END", "End: "));

		Bounds bounds = FX.getStringBounds("Some data", textFont);
		double top = (buttonHeight - bounds.getHeight()) / 2;
		frameInfo.setPadding(new Insets(top, 10, 0, 10));

		flowPane.getChildren().add(frameInfo);

		/*
		 * Configure the buttons .
		 */

		setOnButtonClicked(buttonMoveStart, () -> plotData.moveStart());
		setOnButtonClicked(buttonMoveEnd, () -> plotData.moveEnd());
		setOnButtonClicked(buttonZoomIn, () -> plotData.zoom(-0.05));
		setOnButtonClicked(buttonZoomOut, () -> plotData.zoom(0.05));
		setOnButtonClicked(buttonMoveLeft, () -> plotData.scroll(-0.05));
		setOnButtonClicked(buttonMoveRight, () -> plotData.scroll(0.05));

		setOnButtonPressed(buttonZoomIn, () -> plotData.zoom(-0.05));
		setOnButtonPressed(buttonZoomOut, () -> plotData.zoom(0.05));
		setOnButtonPressed(buttonMoveLeft, () -> plotData.scroll(-0.05));
		setOnButtonPressed(buttonMoveRight, () -> plotData.scroll(0.05));

		setOnButtonReleased(buttonZoomIn);
		setOnButtonReleased(buttonZoomOut);
		setOnButtonReleased(buttonMoveLeft);
		setOnButtonReleased(buttonMoveRight);

		/*
		 * Put the flow pane in top pane.
		 */

		pane.setTop(flowPane);
	}

	/**
	 * Helper to configure the clicked event.
	 *
	 * @param button The button.
	 * @param action The action perform.
	 */
	private void setOnButtonClicked(Pane button, Runnable action) {
		button.setOnMouseClicked(ev -> {
			action.run();
			Platform.runLater(() -> plot());
		});
	}
	/**
	 * Helper to configure the pressed event.
	 *
	 * @param button The button.
	 * @param action The action.
	 */
	private void setOnButtonPressed(Pane button, Runnable action) {
		button.setOnMousePressed(ev -> {
			TimerPack pack = new TimerPack(new TaskRefresh(action));
			button.setUserData(pack);
		});
	}
	/**
	 * Helper to configure the released event.
	 *
	 * @param button The button
	 */
	private void setOnButtonReleased(Pane button) {
		button.setOnMouseReleased(ev -> {
			TimerPack pack = (TimerPack) button.getUserData();
			if (pack != null) pack.terminate();
		});
	}
	/**
	 * Configure and return the zoom in button.
	 *
	 * @return The zoom in button.
	 */
	private Pane getButtonZoomIn() {

		Pane pane = getButtonPane("FRAME-ZOOM-IN");

		double margHorz = (buttonWidth - buttonFrame) / 2;
		double margVert = (buttonHeight - buttonFrame) / 2;

		double xh1 = margHorz;
		double yh1 = buttonHeight / 2;
		double xh2 = xh1 + buttonFrame;
		double yh2 = yh1;

		double xv1 = buttonWidth / 2;
		double yv1 = margVert;
		double xv2 = xv1;
		double yv2 = yv1 + buttonFrame;

		pane.getChildren().add(getButtonLine(xh1, yh1, xh2, yh2));
		pane.getChildren().add(getButtonLine(xv1, yv1, xv2, yv2));

		return pane;
	}
	/**
	 * Configure and return the zoom out button.
	 *
	 * @return The zoom out button.
	 */
	private Pane getButtonZoomOut() {

		Pane pane = getButtonPane("FRAME-ZOOM-OUT");

		double margHorz = (buttonWidth - buttonFrame) / 2;

		double xh1 = margHorz;
		double yh1 = buttonHeight / 2;
		double xh2 = xh1 + buttonFrame;
		double yh2 = yh1;

		pane.getChildren().add(getButtonLine(xh1, yh1, xh2, yh2));

		return pane;
	}
	/**
	 * Configure and return the move start button.
	 *
	 * @return The move start button.
	 */
	private Pane getButtonMoveStart() {

		Pane pane = getButtonPane("FRAME-MOVE-START");

		double margHorz = (buttonWidth - buttonFrame) / 2;
		double margVert = (buttonHeight - buttonFrame) / 2;

		double xh1 = margHorz;
		double yh1 = buttonHeight / 2;
		double xh2 = xh1 + buttonFrame;
		double yh2 = yh1;

		double xv1 = buttonWidth / 2;
		double yv1 = margVert;
		double xv2 = xv1;
		double yv2 = yv1 + buttonFrame;

		pane.getChildren().add(getButtonLine(xh1, yh1, xv1, yv1));
		pane.getChildren().add(getButtonLine(xh1, yh1, xv2, yv2));
		pane.getChildren().add(getButtonLine(xv1, yh1, xh2, yv1));
		pane.getChildren().add(getButtonLine(xv1, yh1, xh2, yv2));

		return pane;
	}
	/**
	 * Configure and return the move end button.
	 *
	 * @return The move end button.
	 */
	private Pane getButtonMoveEnd() {

		Pane pane = getButtonPane("FRAME-MOVE-END");

		double margHorz = (buttonWidth - buttonFrame) / 2;
		double margVert = (buttonHeight - buttonFrame) / 2;

		double xh1 = margHorz;
		double yh1 = buttonHeight / 2;
		double xh2 = xh1 + buttonFrame;
		double yh2 = yh1;

		double xv1 = buttonWidth / 2;
		double yv1 = margVert;
		double xv2 = xv1;
		double yv2 = yv1 + buttonFrame;

		pane.getChildren().add(getButtonLine(xv1, yv1, xh2, yh1));
		pane.getChildren().add(getButtonLine(xh1, yv1, xv1, yh1));
		pane.getChildren().add(getButtonLine(xv1, yv2, xh2, yh1));
		pane.getChildren().add(getButtonLine(xh1, yv2, xv2, yh1));

		return pane;
	}
	/**
	 * Configure and return the move left button.
	 *
	 * @return The move left button.
	 */
	private Pane getButtonMoveLeft() {

		Pane pane = getButtonPane("FRAME-MOVE-LEFT");

		double margHorz = (buttonWidth - buttonFrame) / 2;
		double margVert = (buttonHeight - buttonFrame) / 2;
		double frameStep = buttonFrame / 4;

		double x1, y1, x2, y2;

		x1 = margHorz + frameStep;
		y1 = buttonHeight / 2;
		x2 = margHorz + (frameStep * 3);
		y2 = margVert;
		pane.getChildren().add(getButtonLine(x1, y1, x2, y2));

		y2 = margVert + buttonFrame;
		pane.getChildren().add(getButtonLine(x1, y1, x2, y2));

		return pane;
	}
	/**
	 * Configure and return the move right button.
	 *
	 * @return The move right button.
	 */
	private Pane getButtonMoveRight() {

		Pane pane = getButtonPane("FRAME-MOVE-RIGHT");

		double margHorz = (buttonWidth - buttonFrame) / 2;
		double margVert = (buttonHeight - buttonFrame) / 2;
		double frameStep = buttonFrame / 4;

		double x1, y1, x2, y2;

		x1 = margHorz + (frameStep * 3);
		y1 = buttonHeight / 2;
		x2 = margHorz + frameStep;
		y2 = margVert;
		pane.getChildren().add(getButtonLine(x1, y1, x2, y2));

		y2 = margVert + buttonFrame;
		pane.getChildren().add(getButtonLine(x1, y1, x2, y2));

		return pane;
	}
	/**
	 * Returns the base button as a pane.
	 *
	 * @param id The string id.
	 * @return The base button.
	 */
	private Pane getButtonPane(String id) {
		Pane pane = new Pane();
		pane.setId(id);
		pane.setPrefSize(buttonWidth, buttonHeight);
		pane.setOnMouseEntered(ev -> {
			pane.setBackground(Background.fill(Color.LIGHTGRAY));
		});
		pane.setOnMousePressed(ev -> {
			pane.setBackground(Background.fill(Color.GRAY));
		});
		pane.setOnMouseReleased(ev -> {
			boolean inThePane = ev.getX() >= 0 && ev.getX() <= pane.getWidth();
			inThePane &= ev.getY() >= 0 && ev.getY() <= pane.getHeight();
			if (inThePane) {
				pane.setBackground(Background.fill(Color.LIGHTGRAY));
			} else {
				pane.setBackground(Background.fill(Color.WHITE));
			}
		});
		pane.setOnMouseExited(ev -> {
			pane.setBackground(Background.fill(Color.WHITE));
		});
		return pane;
	}
	private Line getButtonLine(double x1, double y1, double x2, double y2) {
		Line line = new Line(x1, y1, x2, y2);
		line.setStrokeWidth(0.5);
		return line;
	}
	private Canvas getViewPortCanvas() {
		Canvas canvas = new Canvas();
		canvas.setId("FRAME-VIEWPORT");
		double width = (viewportWidth) + (2 * buttonFrame);
		double height = buttonHeight;
		canvas.setWidth(width);
		canvas.setHeight(height);
		return canvas;
	}

	private Text getText(String id, String str) {
		Text text = new Text(str);
		text.setFont(textFont);
		text.setId(id);
		return text;
	}

	/**
	 * Add a plot frame.
	 *
	 * @param plotters The list of plotters of the frame.
	 */
	public void addPlotFrame(DataPlotter... plotters) {
		PlotFrame plotFrame = new PlotFrame();

		plotFrame.plotArea.pane.setOnScroll(ev -> {
			double factor = -1.0 * ev.getDeltaY() / 100;
			plotData.zoom(factor);
			Platform.runLater(() -> plot());
		});
		plotFrame.plotArea.pane.setOnMousePressed(ev -> lastX = ev.getSceneX());
		plotFrame.plotArea.pane.setOnMouseDragged(ev -> {
			double currentX = ev.getSceneX();
			double deltaX = -1.0 * (currentX - lastX);
			lastX = currentX;
			double width = plotFrame.plotArea.pane.getWidth();
			double factor = deltaX / width;
			plotData.scroll(factor);
			Platform.runLater(() -> plot());
		});

		for (DataPlotter plotter : plotters) {
			plotFrame.plotters.add(plotter);
			for (DataSource source : plotter.getDataSources()) {
				plotData.addDataSource(source);
			}
		}
		plotFrames.add(plotFrame);
		if (plotFrames.size() == 1) {
			pane.setCenter(plotFrame.pane);
		}
	}

	/**
	 * Calculate the horizontal margins common to all plot areas.
	 */
	private void calculateHorizontalMargins() {
		if (plotFrames.isEmpty()) {
			throw new IllegalStateException("No plot frames available");
		}
		double width = plotFrames.get(0).plotArea.pane.getWidth();
		marginRight = Numbers.round(width * insets.getRight(), 0);
		marginLeft = Numbers.round(width * insets.getLeft(), 0);
	}

	/**
	 * Calculates the coordinate X given the index within the plot data indexes.
	 *
	 * @param index The index.
	 * @return The coordinate X.
	 */
	private double getCoordinateX(int index) {
		if (plotFrames.isEmpty()) {
			throw new IllegalStateException("No plot frames available");
		}
		double width = plotFrames.get(0).plotArea.pane.getWidth();
		double startIndex = plotData.getStartIndex();
		double endIndex = plotData.getEndIndex();
		double indexFactor = ((double) index - startIndex) / (endIndex - startIndex);
		double plotWidth = width - marginLeft - marginRight;
		double relativeX = indexFactor * plotWidth;
		double coordinateX = Numbers.round(marginLeft + relativeX, 0);
		return coordinateX;
	}

	/**
	 * Returns the main frame pane.
	 *
	 * @return The main frame pane.
	 */
	public Pane getPane() {
		return pane;
	}

	/**
	 * Set the range of start and end indexes to N periods that finish at the end of the available
	 * periods.
	 *
	 * @param periods The number of visible periods.
	 */
	public void setIndexesRangeFromEnd(int periods) {
		plotData.setIndexesRangeFromEnd(periods);
	}
	/**
	 * Set the range of start and end indexes to N periods that start at the beginning of the
	 * available periods.
	 *
	 * @param periods The number of visible periods.
	 */
	public void setIndexesRangeFromStart(int periods) {
		plotData.setIndexesRangeFromStart(periods);
	}

	/**
	 * Do plot the chart components.
	 */
	public void plot() {

		plotPool = new Pool("CHART_FRAME", parallelism);
		for (PlotFrame plotFrame : plotFrames) {
			plotFrame.calculateMinMaxValues(plotData.getStartIndex(), plotData.getEndIndex());
			plotFrame.calculateVerticalMargins();
		}
		calculateHorizontalMargins();
		for (PlotFrame plotFrame : plotFrames) {
			plotFrame.plot();
		}
		plotPool.shutdown();
		plotPool = null;

		plotViewPort();
		plotFrameInfo();
	}
	/**
	 * Plot the viewport that displays the relative position of the visible data.
	 */
	private void plotViewPort() {
		Canvas canvas = (Canvas) pane.lookup("#FRAME-VIEWPORT");
		GraphicsContext gc = canvas.getGraphicsContext2D();

		double width = canvas.getWidth();
		double height = canvas.getHeight();

		/* Background. */
		gc.setLineWidth(0.5);
		gc.setFill(Color.WHITE);
		gc.fillRect(0, 0, width, height);

		double widthPlot = width - (2 * buttonFrame);
		double heightPlot = height - (2 * buttonFrame);

		/* Central rectangle that shows the total available range of data. */
		double xd = buttonFrame;
		double yd = buttonFrame;
		double wd = widthPlot;
		double hd = heightPlot;

		/* Rectangle of the visible data. */
		double dataSize = plotData.getDataSize();
		double startIndex = plotData.getStartIndex();
		double periods = plotData.getPeriods();
		double xv = buttonFrame + widthPlot * (startIndex / dataSize);
		double yv = buttonFrame;
		double wv = widthPlot * (periods / dataSize);
		double hv = heightPlot;

		if (xv + wv > xd + wd) {
			wv = xd + wd - xv;
		}
		if (xv < xd) {
			wv -= (xd - xv);
			xv = xd;
		}
		if (wv < 0.5) {
			wv = 0.5;
		}

		gc.setFill(Color.BLACK);
		gc.fillRect(xv, yv, wv, hv);

		/* Stroke the central rectangle. */
		gc.strokeRect(xd, yd, wd, hd);
	}
	/**
	 * Plot the frame info.
	 */
	private void plotFrameInfo() {
		((Text) pane.lookup("#FRAME-TEXT-SIZE")).setText("Size: " + plotData.getDataSize());
		((Text) pane.lookup("#FRAME-TEXT-PERIODS")).setText("Periods: " + plotData.getPeriods());
		((Text) pane.lookup("#FRAME-TEXT-START")).setText("Start: " + plotData.getStartIndex());
		((Text) pane.lookup("#FRAME-TEXT-END")).setText("End: " + plotData.getEndIndex());
	}

}
