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
import javafx.beans.value.ObservableValue;
import javafx.geometry.BoundingBox;
import javafx.geometry.Bounds;
import javafx.geometry.Insets;
import javafx.geometry.Orientation;
import javafx.scene.Node;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.ContextMenu;
import javafx.scene.control.MenuItem;
import javafx.scene.control.SplitPane;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.shape.Line;
import javafx.scene.text.Font;
import javafx.scene.text.TextFlow;
import msfx.lib.fx.FX;
import msfx.lib.task.Pool;
import msfx.lib.task.timer.TimerTaskRun;
import msfx.lib.util.Numbers;
import msfx.lib.util.Strings;
import msfx.lib.util.funtion.Function;
import msfx.mkt.DataSource;
import msfx.mkt.IndicatorSource;
import msfx.mkt.Unit;
import msfx.mkt.info.OutputInfo;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Timer;

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
	 * A pane that has, as its first child, a canvas with the width and height properties binded to
	 * those of its parent pane.
	 */
	private class CanvasPane {
		private Pane pane;
		private Canvas canvas;
		private CanvasPane() {
			pane = new Pane();
			canvas = new Canvas();
			pane.getChildren().add(canvas);
			canvas.widthProperty().bind(Bindings.selectDouble(canvas.parentProperty(), "width"));
			canvas.heightProperty().bind(Bindings.selectDouble(canvas.parentProperty(), "height"));
		}
	}

	/**
	 * A border pane that contains an info pane at the top, a plot pane at the center and a
	 * vertical axis at the right.
	 */
	private class ChartPlot {

		/**
		 * Index within the list of plot frames, necessary to have a unique ID for all its
		 * components.
		 */
		private int index;

		/**
		 * Effective border pane that contains the info pane, the plot area pane and the vertical
		 * axis.
		 */
		private BorderPane panePlot;
		/**
		 * The plot area where the data charts are plotted.
		 */
		private CanvasPane chart;
		/**
		 * The associated vertical axis.
		 */
		private CanvasPane vaxis;
		/**
		 * Info pane.
		 */
		private FlowPane infoPane;

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

		/**
		 * The plot scale that applies to all data plotters that plot on this plot frame area.
		 */
		private PlotScale scale;

		/**
		 * List of data plotters.
		 */
		private List<DataPlotter> plotters;

		/**
		 * Indicator that the cross-cursor horizontal line is on or off.
		 */
		private boolean crossCursorHorz = false;
		/**
		 * Indicator that the cross-cursor vertical line is on or off.
		 */
		private boolean crossCursorVert = false;
	}

	/**
	 * The plot context to pass to data plotters.
	 */
	public class Context extends PlotContext {
		/**
		 * The associated plot frame.
		 */
		private ChartPlot chartPlot;
		/**
		 * Constructor.
		 *
		 * @param chartPlot The plot frame.
		 */
		private Context(ChartPlot chartPlot) {
			this.chartPlot = chartPlot;
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
			return chartPlot.chart.canvas.getGraphicsContext2D();
		}

		/**
		 * Returns the width of the plot area.
		 *
		 * @return The width.
		 */
		public double getWidth() {
			return chartPlot.chart.canvas.getWidth();
		}
		/**
		 * Returns the height of the plot area.
		 *
		 * @return The height.
		 */
		public double getHeight() {
			return chartPlot.chart.canvas.getHeight();
		}

		/**
		 * Returns the top margin.
		 *
		 * @return The top margin.
		 */
		public double getMarginTop() {
			return chartPlot.marginTop;
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
			return chartPlot.marginBottom;
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
			return ChartFrame.this.getCoordinateY(chartPlot, value);
		}

		/**
		 * Return the minimum value within the range of indexes.
		 *
		 * @return The minimum value.
		 */
		public double getMinimumValue() {
			return chartPlot.minimumValue;
		}
		/**
		 * Return the maximum value within the range of indexes.
		 *
		 * @return The maximum value.
		 */
		public double getMaximumValue() {
			return chartPlot.maximumValue;
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
	private final List<ChartPlot> chartPlots;
	/**
	 * Horizontal axis.
	 */
	private final CanvasPane haxis;

	/**
	 * The border pane that contains all the components of the chart frame.
	 */
	private final BorderPane paneFrame;

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
	 * Default font for all texts int this chart frame.
	 */
	private Font textFont = new Font(10);
	/**
	 * Default background for all components.
	 */
	private Background background = new Background(new BackgroundFill(Color.WHITE, null, null));
	/**
	 * Functional to get the same border style.
	 */
	private Function.P4<Border, Double, Double, Double, Double> border = (t, r, b, l) -> {
		BorderStroke borderStroke = new BorderStroke(
				Color.BLACK,
				BorderStrokeStyle.SOLID,
				CornerRadii.EMPTY,
				new BorderWidths(t, r, b, l));
		return new Border(borderStroke);
	};

	private double lastX;
	private ContextMenu contextMenu;
	private boolean crossCursor = false;

	/**
	 * Constructor.
	 *
	 * @param plotters The starting data plotters.
	 */
	public ChartFrame(DataPlotter... plotters) {

		/* Text bounds to calculate sizes. */
		Bounds bounds = FX.getStringBounds("Sample data", textFont);

		plotData = new PlotData();
		chartPlots = new ArrayList<>();
		paneFrame = new BorderPane();
		paneFrame.setBackground(background);
		contextMenu = new ContextMenu();


		/* Horizontal axis in the bottom pane. */
		haxis = new CanvasPane();
		haxis.pane.setBorder(border.call(0.5, 0.0, 0.0, 0.0));
		haxis.pane.setPrefHeight((bounds.getHeight() * 2) + 5 + 3);
		paneFrame.setBottom(haxis.pane);

		/* Pane size listeners. */
		paneFrame.widthProperty().addListener(
				(ObservableValue<? extends Number> ob, Number vo, Number vn) -> {
					Platform.runLater(() -> { plot(); });
				});
		paneFrame.heightProperty().addListener(
				(ObservableValue<? extends Number> ob, Number vo, Number vn) -> {
					Platform.runLater(() -> { plot(); });
				});

		/* Flow pane on top to handle buttons, viewport and info. */
		FlowPane flowPane = new FlowPane();
		flowPane.setId("FRAME-FLOW");
		flowPane.setBorder(border.call(0.0, 0.0, 0.5, 0.0));
		flowPane.setPrefHeight(buttonHeight);

		/* Buttons zoom-in, zoom-out, move-left, move-right, move-start, move-end. */
		flowPane.getChildren().add(getFrameButton("ZOOM-IN"));
		flowPane.getChildren().add(getFrameButton("ZOOM-OUT"));
		flowPane.getChildren().add(getFrameButton("MOVE-LEFT"));
		flowPane.getChildren().add(getFrameButton("MOVE-RIGHT"));
		flowPane.getChildren().add(getFrameButton("MOVE-START"));
		flowPane.getChildren().add(getFrameButton("MOVE-END"));
		flowPane.getChildren().add(getViewPortCanvas());

		/* Text flow pane as a text info pane. */
		TextFlow frameInfo = new TextFlow();
		frameInfo.setId("FRAME-INFO");
		frameInfo.setMaxHeight(buttonHeight);
		frameInfo.setMinHeight(buttonHeight);
		frameInfo.setPadding(new Insets((buttonHeight - bounds.getHeight()) / 2, 10, 0, 10));
		flowPane.getChildren().add(frameInfo);

		/* Put the flow pane in the top pane. */
		paneFrame.setTop(flowPane);

		/* Add the initial plotter. */
		addPlotFrame(plotters);
		setIndexesRangeFromEnd(500);
	}
	/**
	 * Add a plot frame.
	 *
	 * @param plotters The list of plotters of the frame.
	 */
	public void addPlotFrame(DataPlotter... plotters) {

		ChartPlot plot = new ChartPlot();

		plot.index = chartPlots.size();
		plot.plotters = new ArrayList<>();
		plot.scale = PlotScale.LOGARITHMIC;

		plot.chart = new CanvasPane();
		plot.vaxis = new CanvasPane();
		plot.vaxis.pane.setBorder(border.call(0.0, 0.0, 0.0, 0.5));
		plot.vaxis.pane.setPrefWidth(50);
		plot.vaxis.pane.setMinWidth(50);

		plot.panePlot = new BorderPane();

		plot.infoPane = new FlowPane();
		plot.infoPane.setId(getId("FLOW-PANE-CP", plot.index));
		plot.infoPane.setBorder(border.call(0.0, 0.0, 0.5, 0.0));
		plot.infoPane.setPrefHeight(buttonHeight);

		TextFlow textFlow = new TextFlow();
		textFlow.setId(getId("TEXT-FLOW-CP", plot.index));
		textFlow.setMaxHeight(buttonHeight);
		textFlow.setMinHeight(buttonHeight);

		Bounds bounds = FX.getStringBounds("Some data", textFont);
		double top = (buttonHeight - bounds.getHeight()) / 2;
		textFlow.setPadding(new Insets(top, 10, 0, 10));

		plot.infoPane.getChildren().add(textFlow);

		plot.panePlot.setTop(plot.infoPane);
		plot.panePlot.setCenter(plot.chart.pane);
		plot.panePlot.setRight(plot.vaxis.pane);

		plot.chart.pane.setOnMouseEntered(ev -> onMouseEntered(ev, plot));
		plot.chart.pane.setOnMouseExited(ev -> onMouseExited(ev, plot));
		plot.chart.pane.setOnMouseMoved(ev -> onMouseMoved(ev, plot));
		plot.chart.pane.setOnMouseClicked(ev -> onMouseClicked(ev, plot));

		plot.chart.pane.setOnScroll(ev -> {
			double factor = -1.0 * ev.getDeltaY() / 100;
			plotData.zoom(factor);
			Platform.runLater(() -> plot());
		});
		plot.chart.pane.setOnMousePressed(ev -> lastX = ev.getSceneX());
		plot.chart.pane.setOnMouseDragged(ev -> {
			double currentX = ev.getSceneX();
			double deltaX = -1.0 * (currentX - lastX);
			lastX = currentX;
			double width = plot.chart.pane.getWidth();
			double factor = deltaX / width;
			plotData.scroll(factor);
			Platform.runLater(() -> plot());
		});

		for (DataPlotter plotter : plotters) {
			plot.plotters.add(plotter);
			for (DataSource source : plotter.getDataSources()) {
				if (source instanceof IndicatorSource indicatorSource) {
					indicatorSource.calculate(plotData);
				}
				plotData.addDataSource(source);
			}
		}
		chartPlots.add(plot);
		if (chartPlots.size() == 1) {
			paneFrame.setCenter(plot.panePlot);
		} else {
			SplitPane splitPane = new SplitPane();
			splitPane.setBackground(background);
			splitPane.setOrientation(Orientation.VERTICAL);
			for (ChartPlot p : chartPlots) {
				splitPane.getItems().add(p.panePlot);
			}
			for (SplitPane.Divider d : splitPane.getDividers()) {
				d.positionProperty().addListener(
						(ObservableValue<? extends Number> ob, Number vo, Number vn) -> {
							Platform.runLater(() -> { plot(); });
						});
			}

			paneFrame.setCenter(splitPane);
		}
	}
	/**
	 * Calculate the horizontal margins common to all plot areas.
	 */
	private void calculateHorizontalMargins() {
		if (chartPlots.isEmpty()) {
			throw new IllegalStateException("No plot frames available");
		}
		double width = chartPlots.get(0).chart.pane.getWidth();
		marginRight = Numbers.round(width * insets.getRight(), 0);
		marginLeft = Numbers.round(width * insets.getLeft(), 0);
	}
	/**
	 * Calculates the minimum and maximum values within the range indexes.
	 *
	 * @param plot       The chart plot component.
	 * @param startIndex Start index, included.
	 * @param endIndex   End index included.
	 */
	private void calculateMinMaxValues(ChartPlot plot, int startIndex, int endIndex) {

		int dataSize = plotData.getDataSize();
		plot.minimumValue = Numbers.MAX_DOUBLE;
		plot.maximumValue = Numbers.MIN_DOUBLE;

		for (int index = startIndex; index <= endIndex; index++) {
			if (index < 0) continue;
			if (index >= dataSize) continue;
			for (DataPlotter plotter : plot.plotters) {
				double[] values = plotter.getValues(index);
				for (double value : values) {
					if (value < plot.minimumValue) {
						plot.minimumValue = value;
					}
					if (value > plot.maximumValue) {
						plot.maximumValue = value;
					}
				}
			}
		}
	}
	/**
	 * Calculate the vertical margins.
	 *
	 * @param plot The chart plot component.
	 */
	private void calculateVerticalMargins(ChartPlot plot) {
		plot.marginTop = Numbers.round(plot.chart.pane.getHeight() * insets.getTop(), 0);
		plot.marginBottom = Numbers.round(plot.chart.pane.getHeight() * insets.getBottom(), 0);
	}
	/**
	 * Clear the canvas of the argument chart-plot plot area.
	 *
	 * @param plot The chart-plot.
	 */
	private void clearPlotArea(ChartPlot plot) {
		double width = plot.chart.canvas.getWidth();
		double height = plot.chart.canvas.getHeight();
		GraphicsContext gc = plot.chart.canvas.getGraphicsContext2D();
		gc.clearRect(0, 0, width, height);
	}

	/**
	 * Returns a suitable plot context.
	 *
	 * @param plot The chart-plot with horizontal info.
	 * @return The plot context to draw.
	 */
	private PlotContext getContext(ChartPlot plot) {
		return new Context(plot);
	}
	/**
	 * Returns the button of any of the top actions.
	 *
	 * @param id The id.
	 * @return The button pane.
	 */
	private Pane getFrameButton(String id) {

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
				pane.setBackground(background);
			}
		});
		pane.setOnMouseExited(ev -> {
			pane.setBackground(background);
		});

		Function.P4<Line, Double, Double, Double, Double> line = (x1, y1, x2, y2) -> {
			Line ln = new Line(x1, y1, x2, y2);
			ln.setStrokeWidth(0.5);
			return ln;
		};

		Runnable deferredAction = null;
		if (id.equals("ZOOM-IN")) {

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

			pane.getChildren().add(line.call(xh1, yh1, xh2, yh2));
			pane.getChildren().add(line.call(xv1, yv1, xv2, yv2));

			deferredAction = () -> plotData.zoom(-0.05);
		}
		if (id.equals("ZOOM-OUT")) {

			double margHorz = (buttonWidth - buttonFrame) / 2;

			double xh1 = margHorz;
			double yh1 = buttonHeight / 2;
			double xh2 = xh1 + buttonFrame;
			double yh2 = yh1;

			pane.getChildren().add(line.call(xh1, yh1, xh2, yh2));

			deferredAction = () -> plotData.zoom(0.05);
		}
		if (id.equals("MOVE-START")) {

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

			pane.getChildren().add(line.call(xh1, yh1, xv1, yv1));
			pane.getChildren().add(line.call(xh1, yh1, xv2, yv2));
			pane.getChildren().add(line.call(xv1, yh1, xh2, yv1));
			pane.getChildren().add(line.call(xv1, yh1, xh2, yv2));

			deferredAction = () -> plotData.moveStart();

		}
		if (id.equals("MOVE-END")) {

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

			pane.getChildren().add(line.call(xv1, yv1, xh2, yh1));
			pane.getChildren().add(line.call(xh1, yv1, xv1, yh1));
			pane.getChildren().add(line.call(xv1, yv2, xh2, yh1));
			pane.getChildren().add(line.call(xh1, yv2, xv2, yh1));

			deferredAction = () -> plotData.moveEnd();
		}
		if (id.equals("MOVE-LEFT")) {

			double margHorz = (buttonWidth - buttonFrame) / 2;
			double margVert = (buttonHeight - buttonFrame) / 2;
			double frameStep = buttonFrame / 4;

			double x1, y1, x2, y2;

			x1 = margHorz + frameStep;
			y1 = buttonHeight / 2;
			x2 = margHorz + (frameStep * 3);
			y2 = margVert;
			pane.getChildren().add(line.call(x1, y1, x2, y2));

			y2 = margVert + buttonFrame;
			pane.getChildren().add(line.call(x1, y1, x2, y2));

			deferredAction = () -> plotData.scroll(-0.05);
		}
		if (id.equals("MOVE-RIGHT")) {

			double margHorz = (buttonWidth - buttonFrame) / 2;
			double margVert = (buttonHeight - buttonFrame) / 2;
			double frameStep = buttonFrame / 4;

			double x1, y1, x2, y2;

			x1 = margHorz + (frameStep * 3);
			y1 = buttonHeight / 2;
			x2 = margHorz + frameStep;
			y2 = margVert;
			pane.getChildren().add(line.call(x1, y1, x2, y2));

			y2 = margVert + buttonFrame;
			pane.getChildren().add(line.call(x1, y1, x2, y2));

			deferredAction = () -> plotData.scroll(0.05);
		}

		final Runnable action = deferredAction;
		pane.setOnMouseClicked(ev -> {
			action.run();
			Platform.runLater(() -> plot());
		});
		pane.setOnMousePressed(ev -> {
			TimerTaskRun task = new TimerTaskRun(() -> {
				action.run();
				Platform.runLater(() -> plot());
			});
			Timer timer = new Timer(true);
			pane.setUserData(timer);
			timer.schedule(task, refreshPeriod * 4, refreshPeriod);
		});
		pane.setOnMouseReleased(ev -> {
			Timer timer = (Timer) pane.getUserData();
			if (timer != null) timer.cancel();
		});

		return pane;
	}
	/**
	 * Returns the viewport canvas to show the relative horizontal position of the charts.
	 *
	 * @return The viewport canvas.
	 */
	private Canvas getViewPortCanvas() {
		Canvas canvas = new Canvas();
		canvas.setId("FRAME-VIEWPORT");
		double width = (viewportWidth) + (2 * buttonFrame);
		double height = buttonHeight;
		canvas.setWidth(width);
		canvas.setHeight(height);
		return canvas;
	}
	/**
	 * Returns a suitable unique ID for components of this plot frame.
	 *
	 * @param root  The root of the ID.
	 * @param index An rder index.
	 * @return The unique ID.
	 */
	private String getId(String root, int index) {
		return root + "-" + index;
	}
	/**
	 * Calculates the coordinate X given the index within the plot data indexes.
	 *
	 * @param index The index.
	 * @return The coordinate X.
	 */
	private double getCoordinateX(int index) {
		if (chartPlots.isEmpty()) {
			throw new IllegalStateException("No plot frames available");
		}
		double width = chartPlots.get(0).chart.pane.getWidth();
		double startIndex = plotData.getStartIndex();
		double endIndex = plotData.getEndIndex();
		double indexFactor = ((double) index - startIndex) / (endIndex - startIndex);
		double plotWidth = width - marginLeft - marginRight;
		double relativeX = indexFactor * plotWidth;
		double coordinateX = Numbers.round(marginLeft + relativeX, 0);
		return coordinateX;
	}
	/**
	 * Calculates the coordinate Y given the value and the scale of this area.
	 *
	 * @param value The value.
	 * @return The coordinate Y.
	 */
	private double getCoordinateY(ChartPlot plot, double value) {

		double minValue = plot.minimumValue;
		double maxValue = plot.maximumValue;
		if (plot.scale == PlotScale.LOGARITHMIC) {
			minValue = Math.log1p(minValue);
			maxValue = Math.log1p(maxValue);
			value = Math.log1p(value);
		}
		if (plot.scale == PlotScale.PERCENTAGE) {
			// TODO pending to implement PlotScale.PERCENTAGE.
		}
		if (value < minValue) {
			value = minValue;
		}
		if (value > maxValue) {
			value = maxValue;
		}
		double factor = (maxValue - value) / (maxValue - minValue);
		double relativeY = 0;
		double height = plot.chart.pane.getHeight();
		double chartHeight = height - plot.marginTop - plot.marginBottom;
		relativeY = factor * chartHeight;

		double coordinateY = plot.marginTop + (chartHeight * factor);
		return coordinateY;
	}

	/**
	 * Returns the main frame pane to be installed in the scene.
	 *
	 * @return The main frame pane.
	 */
	public Pane getPaneFrame() {
		return paneFrame;
	}

	/**
	 * Respond to the move clicked event on the chart-plot pane.
	 *
	 * @param ev   The mouse event.
	 * @param plot The chart plot where the mouse event started.
	 */
	private void onMouseClicked(MouseEvent ev, ChartPlot plot) {

		/* Hide if the context menu is showing. */
		if (contextMenu.isShowing()) {
			contextMenu.hide();
			return;
		}

		/* Right button pressed, context menu required. */
		if (ev.getButton() == MouseButton.SECONDARY) {

			/* Clear children to rebuild under status. */
			contextMenu.getItems().clear();

			/* Reference node. */
			Node node;

			/* The info pane. */
			node = plot.panePlot.getTop();
			/* Info pane present, request hide. */
			if (node != null) {
				MenuItem item = new MenuItem("Hide info panes");
				item.setOnAction(e -> {
					clearPlotArea(plot);
					plot.panePlot.setTop(null);
					for (ChartPlot p : chartPlots) {
						clearPlotArea(p);
						p.panePlot.setTop(null);
					}
					plotDelay();
				});
				contextMenu.getItems().add(item);
			}
			/* Info pane not present, request show. */
			if (node == null) {
				MenuItem item = new MenuItem("Show info panes");
				item.setOnAction(e -> {
					clearPlotArea(plot);
					plot.panePlot.setTop(plot.infoPane);
					for (ChartPlot p : chartPlots) {
						clearPlotArea(p);
						p.panePlot.setTop(p.infoPane);
					}
					plotDelay();
				});
				contextMenu.getItems().add(item);
			}

			/* Values pane. */
			node = plot.panePlot.getRight();
			/* Values pane is present, request hide. */
			if (node != null) {
				MenuItem item = new MenuItem("Hide value panes");
				item.setOnAction(e -> {
					for (ChartPlot p : chartPlots) {
						clearPlotArea(p);
						p.panePlot.setRight(null);
					}
					plotDelay();
				});
				contextMenu.getItems().add(item);
			}
			/* Values pane is not present, request show. */
			if (node == null) {
				MenuItem item = new MenuItem("Show value panes");
				item.setOnAction(e -> {
					for (ChartPlot p : chartPlots) {
						clearPlotArea(p);
						p.panePlot.setRight(p.vaxis.pane);
					}
					plotDelay();
				});
				contextMenu.getItems().add(item);
			}

			/* HAxis. */
			node = ChartFrame.this.paneFrame.getBottom();
			/* HAxis is present and thus visible, request hide. */
			if (node != null) {
				MenuItem item = new MenuItem("Hide time pane");
				item.setOnAction(e -> {
					clearPlotArea(plot);
					ChartFrame.this.paneFrame.setBottom(null);
					plotDelay();
				});
				contextMenu.getItems().add(item);
			}
			/* HAxis is not present, request show. */
			if (node == null) {
				MenuItem item = new MenuItem("Show time pane");
				item.setOnAction(e -> {
					clearPlotArea(plot);
					ChartFrame.this.paneFrame.setBottom(ChartFrame.this.haxis.pane);
					plotDelay();
				});
				contextMenu.getItems().add(item);
			}

			/* Cross-cursor. */
			if (crossCursor) {
				MenuItem item = new MenuItem("Hide cross-cursor");
				item.setOnAction(e -> {
					crossCursor = false;
					plot.crossCursorVert = false;
					plot.crossCursorHorz = false;
					setCrossCursor(plot, -1, -1);
					for (ChartPlot p : chartPlots) {
						if (p == plot) continue;
						p.crossCursorVert = false;
						p.crossCursorHorz = false;
						setCrossCursor(p, ev.getX(), -1);
					}
				});
				contextMenu.getItems().add(item);
			} else {
				MenuItem item = new MenuItem("Show cross-cursor");
				item.setOnAction(e -> {
					crossCursor = true;
					plot.crossCursorVert = true;
					plot.crossCursorHorz = true;
					setCrossCursor(plot, ev.getX(), ev.getY());
					for (ChartPlot p : chartPlots) {
						if (p == plot) continue;
						p.crossCursorVert = true;
						p.crossCursorHorz = false;
						setCrossCursor(p, ev.getX(), -1);
					}
				});
				contextMenu.getItems().add(item);
			}

			contextMenu.show(plot.panePlot, ev.getScreenX(), ev.getScreenY());
		}
	}
	private void onMouseEntered(MouseEvent ev, ChartPlot plot) {
		if (crossCursor) {
			plot.crossCursorVert = true;
			plot.crossCursorHorz = true;
			setCrossCursor(plot, ev.getX(), ev.getY());
			for (ChartPlot p : chartPlots) {
				if (p == plot) continue;
				p.crossCursorVert = true;
				p.crossCursorHorz = false;
				setCrossCursor(p, ev.getX(), -1);
			}
		}
	}
	private void onMouseExited(MouseEvent ev, ChartPlot plot) {
		if (crossCursor) {
			plot.crossCursorVert = false;
			plot.crossCursorHorz = false;
			setCrossCursor(plot, ev.getX(), ev.getY());
			for (ChartPlot p : chartPlots) {
				if (p == plot) continue;
				p.crossCursorVert = false;
				p.crossCursorHorz = false;
				setCrossCursor(p, ev.getX(), -1);
			}
		}
	}
	/**
	 * Respond to the move mouse event on the chart-plot pane.
	 *
	 * @param ev   The mouse event.
	 * @param plot The chart plot where the mouse event started.
	 */
	private void onMouseMoved(MouseEvent ev, ChartPlot plot) {

		/* Cross-cursor. */
		if (plot.crossCursorVert) {
			plot.crossCursorHorz = true;
		}
		setCrossCursor(plot, ev.getX(), ev.getY());
		for (ChartPlot p : chartPlots) {
			if (p == plot) continue;
			p.crossCursorVert = plot.crossCursorVert;
			p.crossCursorHorz = false;
			setCrossCursor(p, ev.getX(), -1);
		}

		/* Info. */
		if (plot.panePlot.getTop() == null) return;

		TextFlow textFlow = (TextFlow) plot.infoPane.lookup(getId("#TEXT-FLOW-CP", plot.index));
		textFlow.getChildren().clear();

		textFlow.getChildren().add(FX.getText("X: " + Numbers.round(ev.getX(), 1), textFont));
		textFlow.getChildren().add(FX.getText("  ", textFont));
		textFlow.getChildren().add(FX.getText("Y: " + Numbers.round(ev.getY(), 1), textFont));

		PlotContext context = getContext(plot);
		int scale = context.getPlotData().getPipScale();
		String strValue;

		int index = context.getIndex(ev.getX());
		textFlow.getChildren().add(FX.getText("  ", textFont));
		textFlow.getChildren().add(FX.getText("I: " + index, textFont));

		double value = context.getValue(ev.getY());
		strValue = Strings.toString(value, scale);
		textFlow.getChildren().add(FX.getText("  ", textFont));
		textFlow.getChildren().add(FX.getText("V: " + strValue, textFont));

		LocalDateTime time = plotData.getTime(index);
		strValue = Strings.toString(time, false);
		textFlow.getChildren().add(FX.getText("  ", textFont));
		textFlow.getChildren().add(FX.getText("T: " + strValue, textFont));

		if (index > 0 && index < context.getPlotData().getDataSize()) {
			for (DataPlotter plotter : plot.plotters) {
				double[] values = plotter.getValues(index);
				OutputInfo[] infos = plotter.getInfos(index);
				for (OutputInfo info : infos) {
					int i = info.getIndex();
					strValue = Strings.toString(values[i], scale);
					String name = info.getShortName();
					textFlow.getChildren().add(FX.getText("  ", textFont));
					textFlow.getChildren().add(FX.getText(name + ": " + strValue, textFont));
				}
			}
		}
	}

	/**
	 * Do plot the chart components.
	 */
	public void plot() {

		Node center = paneFrame.getCenter();
		if (center instanceof SplitPane sp) {
			Node div = sp.lookup(".split-pane-divider");
			if (div != null) {
				div.setStyle("-fx-padding: 0.3mm;");
			}
		}

		plotPool = new Pool("CHART_FRAME", parallelism);

		int startIndex = plotData.getStartIndex();
		int endIndex = plotData.getEndIndex();
		for (ChartPlot plot : chartPlots) {
			calculateMinMaxValues(plot, startIndex, endIndex);
			calculateVerticalMargins(plot);
		}
		calculateHorizontalMargins();

		for (ChartPlot plot : chartPlots) {
			plot(plot);
		}
		plotPool.shutdown();
		plotPool = null;

		plotViewPort();
		plotFrameInfo();
		plotHAxis();
	}
	/**
	 * Plot the frame.
	 *
	 * @param plot The chart-plot component.
	 */
	private void plot(ChartPlot plot) {
		setCrossCursor(plot, -1, -1);
		clearPlotArea(plot);
		PlotContext context = getContext(plot);
		for (DataPlotter plotter : plot.plotters) {
			plotter.plot(context);
		}
	}
	/**
	 * Plot the viewport that displays the relative position of the visible data.
	 */
	private void plotViewPort() {
		Canvas canvas = (Canvas) paneFrame.lookup("#FRAME-VIEWPORT");
		GraphicsContext gc = canvas.getGraphicsContext2D();

		double width = canvas.getWidth();
		double height = canvas.getHeight();

		double startIndex = plotData.getStartIndex();
		double endIndex = plotData.getEndIndex();
		double dataSize = plotData.getDataSize();

		gc.setLineWidth(0.5);
		gc.setFill(Color.WHITE);
		gc.fillRect(0, 0, width, height);

		/* Width and height of the plot rectangle. */
		double widthPlot = width - (2 * buttonFrame);
		double heightPlot = height - (2 * buttonFrame);

		/* Border of the rectangle that shows the range of indexes. */
		double x_border = buttonFrame;
		double y_border = buttonFrame;
		double w_border = widthPlot;
		double h_border = heightPlot;
		gc.strokeRect(x_border, y_border, w_border, h_border);

		/* Range of visible indexes. */
		double r_min_index = startIndex >= 0 ? 0 : startIndex;
		double r_max_index = endIndex < dataSize ? dataSize - 1 : endIndex;
		double r_num_indexes = r_max_index - r_min_index - 1;

		/* Indexes of visible data. */
		double d_min_index = startIndex < 0 ? 0 : startIndex;
		double d_max_index = endIndex >= dataSize ? dataSize - 1 : endIndex;
		double d_num_indexes = d_max_index - d_min_index - 1;

		/* Width of the bar that displays the visible data. */
		double w_bar = widthPlot * d_num_indexes / r_num_indexes;
		if (w_bar < 1) w_bar = 1;

		/* Total margin or difference between the bar and the rectangle. */
		double marg = w_border - w_bar;

		/* Number of indexes in the left margin and its width. */
		double marg_indexes = r_num_indexes - d_num_indexes;
		double left_indexes = d_min_index - r_min_index;
		double left_margin = marg * left_indexes / marg_indexes;

		/* Rest of bar. */
		double x_bar = buttonFrame + left_margin;
		double y_bar = buttonFrame;
		double h_bar = heightPlot;

		gc.setFill(Color.BLACK);
		gc.fillRect(x_bar, y_bar, w_bar, h_bar);
	}
	/**
	 * Plot the frame info.
	 */
	private void plotFrameInfo() {

		TextFlow textFlow = (TextFlow) paneFrame.lookup("#FRAME-INFO");
		textFlow.getChildren().clear();
		textFlow.getChildren().add(FX.getText("Size: " + plotData.getDataSize(), textFont));
		textFlow.getChildren().add(FX.getText("  ", textFont));
		textFlow.getChildren().add(FX.getText("Periods: " + plotData.getPeriods(), textFont));
		textFlow.getChildren().add(FX.getText("  ", textFont));
		textFlow.getChildren().add(FX.getText("Start: " + plotData.getStartIndex(), textFont));
		textFlow.getChildren().add(FX.getText("  ", textFont));
		textFlow.getChildren().add(FX.getText("End: " + plotData.getEndIndex(), textFont));
		textFlow.getChildren().add(FX.getText("  ", textFont));

		LocalDateTime t_start = plotData.getTime(plotData.getStartIndex());
		LocalDateTime t_end = plotData.getTime(plotData.getEndIndex());

		String s_start = Strings.toString(t_start, false);
		String s_end = Strings.toString(t_end, false);
		textFlow.getChildren().add(FX.getText(s_start + " - " + s_end, textFont));

	}
	/**
	 * Plot the horizontal axis.
	 */
	private void plotHAxis() {

		/* Use the first plot frame for horizontal calculations. */
		PlotContext context = new Context(chartPlots.get(0));

		/* Get the unit of the plot period. */
		Unit unit = plotData.getPeriod().getUnit();

		/*
		 * If the unit is MONTH, will plot:
		 * 2022-05
		 *
		 * If the unit is WEEK or DAY, will plot;
		 * 2022-05-23
		 *
		 * If the unit is HOUR or MINUTE, will plot:
		 *   12:15
		 * 2022-05-23
		 *
		 * In any case, the width of the bounds will be determined by:
		 * 2022-05 or 2022-05-23
		 *
		 * Calculate the bound to plot the time info.
		 */
		Bounds bounds;
		if (unit == Unit.MONTH) {
			bounds = FX.getStringBounds("2022-05", textFont);
		} else {
			bounds = FX.getStringBounds("2022-05-23", textFont);
			if (unit == Unit.HOUR || unit == Unit.MINUTE) {
				/* Add a second line of text and an small separation. */
				double w = bounds.getWidth();
				double h = bounds.getHeight() * 2 + 2;
				bounds = new BoundingBox(0, 0, w, h);
			}
		}

		/* Get the first and the last visible times. */
		LocalDateTime startTime = plotData.getTime(plotData.getStartIndex());
		LocalDateTime endTime = plotData.getTime(plotData.getEndIndex());
	}
	/**
	 * Force a global refresh with a minimal delay so all FX pulses have been processed.
	 */
	private void plotDelay() {
		Runnable run = () -> {
			try {
				Thread.sleep(30);
			} catch (Exception ignore) { }
			Platform.runLater(() -> plot());
		};
		new Thread(run).start();
	}

	/**
	 * Set the cross-cursor. If the X coordinate is negative, the vertical line is hidden, and
	 * if the Y coordinate is negative, the horizontal line is hidden.
	 *
	 * @param plot The chart-plot.
	 * @param x    X coordinate.
	 * @param y    Y coordinate.
	 */
	private void setCrossCursor(ChartPlot plot, double x, double y) {

		/* Remove if required. */
		if (!plot.crossCursorVert) {
			FX.remove("CROSS-CURSOR-VERT", plot.chart.pane.getChildren());
		}
		if (!plot.crossCursorHorz) {
			FX.remove("CROSS-CURSOR-HORZ", plot.chart.pane.getChildren());
		}
		if (!plot.crossCursorVert && !plot.crossCursorHorz) {
			return;
		}

		/* Possibly required context. */
		PlotContext context = getContext(plot);

		/* Vertical line. */
		if (plot.crossCursorVert) {
			Line line_vert = (Line) plot.chart.pane.lookup("#CROSS-CURSOR-VERT");
			if (line_vert == null) {
				line_vert = new Line();
				line_vert.setId("CROSS-CURSOR-VERT");
				line_vert.setStrokeWidth(0.5);
				plot.chart.pane.getChildren().add(line_vert);
			}
			if (x >= 0) {
				int index = context.getIndex(x);
				line_vert.setUserData(index);
			} else {
				x = plot.chart.pane.getWidth() / 2;
				Integer index = (Integer) line_vert.getUserData();
				if (index != null) {
					x = context.getCoordinateX(index);
				}
			}
			line_vert.setStartX(x);
			line_vert.setStartY(0);
			line_vert.setEndX(x);
			line_vert.setEndY(plot.chart.pane.getHeight());
		}

		/* Horizontal line. */
		if (plot.crossCursorHorz) {
			Line line_horz = (Line) plot.chart.pane.lookup("#CROSS-CURSOR-HORZ");
			if (line_horz == null) {
				line_horz = new Line();
				line_horz.setId("CROSS-CURSOR-HORZ");
				line_horz.setStrokeWidth(0.5);
				plot.chart.pane.getChildren().add(line_horz);
			}
			if (y >= 0) {
				double value = context.getValue(y);
				line_horz.setUserData(value);
			} else {
				y = plot.chart.pane.getHeight() / 2;
				Double value = (Double) line_horz.getUserData();
				if (value != null) {
					y = context.getCoordinateY(value);
				}
			}
			line_horz.setStartX(0);
			line_horz.setStartY(y);
			line_horz.setEndX(plot.chart.pane.getWidth());
			line_horz.setEndY(y);
		}

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
}
