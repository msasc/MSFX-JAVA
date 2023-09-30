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
import javafx.geometry.BoundingBox;
import javafx.geometry.Bounds;
import javafx.geometry.Insets;
import javafx.scene.Node;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.ContextMenu;
import javafx.scene.control.MenuItem;
import javafx.scene.input.ContextMenuEvent;
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
import msfx.mkt.DataSource;
import msfx.mkt.Unit;
import msfx.mkt.info.OutputInfo;

import java.time.LocalDateTime;
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
	private class PlotFrame {

		/**
		 * Index within the list of plot frames, necessary to have a unique ID for all its
		 * components.
		 */
		private int index;

		/**
		 * Effective border pane that contains the info pane, the plot area pane and the vertical
		 * axis.
		 */
		private final BorderPane pane;
		/**
		 * The plot area where the data charts are plotted.
		 */
		private final CanvasPane plotArea;
		/**
		 * The associated vertical axis.
		 */
		private final CanvasPane vaxis;
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

		private ContextMenu contextMenu;

		/**
		 * List of data plotters.
		 */
		private final List<DataPlotter> plotters;

		/**
		 * Constructor.
		 *
		 * @param index The index with the chart frame list of plot frames.
		 */
		private PlotFrame(int index) {
			this.index = index;

			plotters = new ArrayList<>();
			scale = PlotScale.LOGARITHMIC;

			plotArea = new CanvasPane();
			vaxis = new CanvasPane();
			vaxis.pane.setBorder(getBorder(0.0, 0.0, 0.0, 0.5));
			vaxis.pane.setPrefWidth(50);
			vaxis.pane.setMinWidth(50);

			pane = new BorderPane();

			/*
			 * Flow pane on top to display related information.
			 */

			infoPane = new FlowPane();
			infoPane.setId(getId("FLOW-PANE"));
			infoPane.setBorder(getBorder(0.0, 0.0, 0.5, 0.0));
			infoPane.setPrefHeight(buttonHeight);

			TextFlow textFlow = new TextFlow();
			textFlow.setId(getId("TEXT-FLOW"));
			textFlow.setMaxHeight(buttonHeight);
			textFlow.setMinHeight(buttonHeight);

			Bounds bounds = FX.getStringBounds("Some data", textFont);
			double top = (buttonHeight - bounds.getHeight()) / 2;
			textFlow.setPadding(new Insets(top, 10, 0, 10));

			infoPane.getChildren().add(textFlow);

			pane.setTop(infoPane);
			pane.setCenter(plotArea.pane);
			pane.setRight(vaxis.pane);

			plotArea.pane.setOnMouseMoved(ev -> onMouseMoved(ev));
			plotArea.pane.setOnContextMenuRequested(ev -> onContextMenu(ev));
		}

		/**
		 * Returns a suitable unique ID for components of this plot frame.
		 *
		 * @param root The root of the ID.
		 * @return The unique ID.
		 */
		private String getId(String root) {
			return root + "-PF-" + index;
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
			if (value < minValue) {
				value = minValue;
			}
			if (value > maxValue) {
				value = maxValue;
			}
			double factor = (maxValue - value) / (maxValue - minValue);
			double relativeY = 0;
			double height = plotArea.pane.getHeight();
			double chartHeight = height - marginTop - marginBottom;
			relativeY = factor * chartHeight;

			double coordinateY = marginTop + (chartHeight * factor);
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
			clearPlot();
			PlotContext context = getContext();
			for (DataPlotter plotter : plotters) {
				plotter.plot(context);
			}
		}

		private void clearPlot() {
			double width = plotArea.canvas.getWidth();
			double height = plotArea.canvas.getHeight();
			GraphicsContext gc = plotArea.canvas.getGraphicsContext2D();
			gc.clearRect(0, 0, width, height);
		}

		/**
		 * Response on context menu within a plot frame pane.
		 *
		 * @param ev The context menu event.
		 */
		private void onContextMenu(ContextMenuEvent ev) {

			if (contextMenu == null) {
				contextMenu = new ContextMenu();
			}
			contextMenu.getItems().clear();
			Node node;

			/*
			 * The info pane can be removed exclusively for the current plot frame.
			 */
			node = pane.getTop();

			/* Info pane is present and thus visible, request hide. */
			if (node != null) {
				MenuItem item = new MenuItem("Hide info pane");
				item.setOnAction(e -> {
					clearPlot();
					pane.setTop(null);
					plotDelay();
				});
				contextMenu.getItems().add(item);
			}
			/* Info pane is not present, request show. */
			if (node == null) {
				MenuItem item = new MenuItem("Show info pane");
				item.setOnAction(e -> {
					clearPlot();
					pane.setTop(infoPane);
					plotDelay();
				});
				contextMenu.getItems().add(item);
			}

			/* Values pane must be removed from all plot frames. */
			node = pane.getRight();

			/* Values pane is present, request hide. */
			if (node != null) {
				MenuItem item = new MenuItem("Hide values pane");
				item.setOnAction(e -> {
					for (PlotFrame plotFrame : plotFrames) {
						clearPlot();
						plotFrame.pane.setRight(null);
					}
					plotDelay();
				});
				contextMenu.getItems().add(item);
			}

			/* Values pane is not present, request show. */
			if (node == null) {
				MenuItem item = new MenuItem("Show values pane");
				item.setOnAction(e -> {
					for (PlotFrame plotFrame : plotFrames) {
						clearPlot();
						plotFrame.pane.setRight(plotFrame.vaxis.pane);
					}
					plotDelay();
				});
				contextMenu.getItems().add(item);
			}

			/* HAxis. */
			node = ChartFrame.this.pane.getBottom();

			/* HAxis is present and thus visible, request hide. */
			if (node != null) {
				MenuItem item = new MenuItem("Hide time pane");
				item.setOnAction(e -> {
					clearPlot();
					ChartFrame.this.pane.setBottom(null);
					plotDelay();
				});
				contextMenu.getItems().add(item);
			}
			/* HAxis is not present, request show. */
			if (node == null) {
				MenuItem item = new MenuItem("Show time pane");
				item.setOnAction(e -> {
					clearPlot();
					ChartFrame.this.pane.setBottom(ChartFrame.this.haxis.pane);
					plotDelay();
				});
				contextMenu.getItems().add(item);
			}

			contextMenu.show(pane, ev.getScreenX(), ev.getScreenY());
		}

		/**
		 * Show chart info in the text flow pane.
		 *
		 * @param ev The mouse event.
		 */
		private void onMouseMoved(MouseEvent ev) {

			if (pane.getTop() == null) return;

			TextFlow textFlow = (TextFlow) infoPane.lookup(getId("#TEXT-FLOW"));
			textFlow.getChildren().clear();

			textFlow.getChildren().add(FX.getText("X: " + Numbers.round(ev.getX(), 1), textFont));
			textFlow.getChildren().add(FX.getText("  ", textFont));
			textFlow.getChildren().add(FX.getText("Y: " + Numbers.round(ev.getY(), 1), textFont));

			PlotContext context = getContext();
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
				for (DataPlotter plotter : plotters) {
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
	}

	/**
	 * Horizontal axis, the component where the timeline is shown.
	 */
	private class HAxis {
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
		private HAxis() {
			pane = new Pane();
			canvas = new Canvas();
			pane.getChildren().add(canvas);
			canvas.widthProperty().bind(Bindings.selectDouble(canvas.parentProperty(), "width"));
			canvas.heightProperty().bind(Bindings.selectDouble(canvas.parentProperty(), "height"));
			Bounds bounds = FX.getStringBounds("Sample data", textFont);
			double height = (bounds.getHeight() * 2) + 5 + 3;
			pane.setPrefHeight(height);
			BorderStroke borderStroke = new BorderStroke(
					Color.BLACK,
					BorderStrokeStyle.SOLID,
					CornerRadii.EMPTY,
					new BorderWidths(0.5, 0.0, 0.0, 0.0));
			pane.setBorder(new Border(borderStroke));
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
	public class Context extends PlotContext {
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
	private final HAxis haxis;

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
	/**
	 * Default background for all components.
	 */
	private Background background = new Background(new BackgroundFill(Color.WHITE, null, null));

	private double lastX;

	/**
	 * Constructor.
	 *
	 * @param plotter The starting data plotter.
	 */
	public ChartFrame(DataPlotter plotter) {

		plotData = new PlotData();
		plotFrames = new ArrayList<>();
		haxis = new HAxis();
		pane = new BorderPane();
		pane.setBackground(background);

		textFont = new Font(10);

		addPlotFrame(plotter);
		setIndexesRangeFromEnd(500);

		/*
		 * Size listener to launch the plot when the size changes.
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
		flowPane.setPrefHeight(buttonHeight);

		/*
		 * Add the buttons to zoom in and out, and to move start, end, left and right.
		 */

		Pane buttonZoomIn = getFrameButton("FRAME-ZOOM-IN");
		Pane buttonZoomOut = getFrameButton("FRAME-ZOOM-OUT");
		Pane buttonMoveLeft = getFrameButton("FRAME-MOVE-LEFT");
		Pane buttonMoveRight = getFrameButton("FRAME-MOVE-RIGHT");
		Pane buttonMoveStart = getFrameButton("FRAME-MOVE-START");
		Pane buttonMoveEnd = getFrameButton("FRAME-MOVE-END");

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
		 * Put the flow pane in the top pane.
		 */

		pane.setTop(flowPane);

		/*
		 * Put the horizontal axis in the bottom pane.
		 */
		pane.setBottom(haxis.pane);
	}

	/**
	 * Helper to configure the clicked event.
	 *
	 * @param button The button.
	 * @param action The action to perform.
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
			TimerTaskRun task = new TimerTaskRun(() -> {
				action.run();
				Platform.runLater(() -> plot());
			});
			Timer timer = new Timer(true);
			button.setUserData(timer);
			timer.schedule(task, refreshPeriod * 4, refreshPeriod);
		});
	}
	/**
	 * Helper to configure the released event.
	 *
	 * @param button The button
	 */
	private void setOnButtonReleased(Pane button) {
		button.setOnMouseReleased(ev -> {
			Timer timer = (Timer) button.getUserData();
			if (timer != null) timer.cancel();
		});
	}
	/**
	 * Configure and return the zoom-in button.
	 *
	 * @return The zoom-in button.
	 */
	private Line getButtonLine(double x1, double y1, double x2, double y2) {
		Line line = new Line(x1, y1, x2, y2);
		line.setStrokeWidth(0.5);
		return line;
	}

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

		if (id.equals("FRAME-ZOOM-IN")) {

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

		}
		if (id.equals("FRAME-ZOOM-OUT")) {

			double margHorz = (buttonWidth - buttonFrame) / 2;

			double xh1 = margHorz;
			double yh1 = buttonHeight / 2;
			double xh2 = xh1 + buttonFrame;
			double yh2 = yh1;

			pane.getChildren().add(getButtonLine(xh1, yh1, xh2, yh2));

		}
		if (id.equals("FRAME-MOVE-START")) {

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

		}
		if (id.equals("FRAME-MOVE-END")) {

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

		}
		if (id.equals("FRAME-MOVE-LEFT")) {

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

		}
		if (id.equals("FRAME-MOVE-RIGHT")) {

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

		}
		return pane;
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

	/**
	 * Add a plot frame.
	 *
	 * @param plotters The list of plotters of the frame.
	 */
	public void addPlotFrame(DataPlotter... plotters) {
		PlotFrame plotFrame = new PlotFrame(plotFrames.size());

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
	 * Returns the main frame pane to be installed in the scene.
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
		plotHAxis();
	}
	/**
	 * Plot the viewport that displays the relative position of the visible data.
	 */
	private void plotViewPort() {
		Canvas canvas = (Canvas) pane.lookup("#FRAME-VIEWPORT");
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

		TextFlow textFlow = (TextFlow) pane.lookup("#FRAME-INFO");
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
		PlotContext context = new Context(plotFrames.get(0));

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
	 * Returns a black solid border.
	 *
	 * @param top    Top line width.
	 * @param right  Right line width.
	 * @param bottom Bottom line width.
	 * @param left   Left line width.
	 * @return The border.
	 */
	private Border getBorder(double top, double right, double bottom, double left) {
		BorderStroke borderStroke = new BorderStroke(
				Color.BLACK,
				BorderStrokeStyle.SOLID,
				CornerRadii.EMPTY,
				new BorderWidths(top, right, bottom, left));
		return new Border(borderStroke);
	}
}
