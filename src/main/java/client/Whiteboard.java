package client;

import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.paint.Paint;
import javafx.scene.shape.StrokeLineCap;
import javafx.scene.text.Font;
import remote.Action;

import java.util.ArrayList;

class Whiteboard {
  private final Canvas canvas = new Canvas(1000, 1000);
  private final Canvas editLayer = new Canvas(1000, 1000);
  private final TextField textLayer = new TextField();
  private final StackPane canvasContainer = new StackPane(canvas, editLayer, textLayer);
  private final VBox toolbox = new VBox();
  private final VBox toolProperties = new VBox();

  private final GraphicsContext gc = canvas.getGraphicsContext2D();

  private Tool tool = Tool.PAINT;

  Whiteboard() {
    canvasContainer.setBorder(getBorder());
    toolbox.setFillWidth(true);
    textLayer.setVisible(false);
    textLayer.setBackground(Background.EMPTY);

    gc.setLineWidth(10);
    gc.setLineCap(StrokeLineCap.ROUND);

    for (Tool t : Tool.values()) {
      Button btn = new Button(t.name());
      btn.setMaxWidth(Double.MAX_VALUE);
      btn.setPrefHeight(40);
      toolbox.getChildren().add(btn);

      btn.setOnAction(e -> {
        tool = t;
      });
    }

    for (ToolProperty t : ToolProperty.values()) {
      toolProperties.getChildren().add(t.getNode(gc));
    }
  }

  StackPane getCanvas() {
    return canvasContainer;
  }

  // move to CSS?
  // canvasContainer.getStyleClass().add("canvas");
  // ...
  // .canvas {
  //    -fx-background-color: antiquewhite, white ;
  //    -fx-background-insets: 0, 20 ;
  //    -fx-padding: 20 ;
  //}
  private Border getBorder() {
    return new Border(new BorderStroke(Color.BLACK, BorderStrokeStyle.SOLID, CornerRadii.EMPTY, BorderWidths.DEFAULT));
  }

  VBox getToolbox() {
    VBox box = new VBox(toolbox, toolProperties);
    box.setSpacing(50);
    return box;
  }

  void draw(MouseEvent e) {
    tool.useDragTool(editLayer, gc, e);
  }

  void click(MouseEvent e) {
    tool.useClickTool(editLayer, gc, e);
    tool.useClickTool(textLayer, gc, e);
  }

  void release(MouseEvent e) {
    tool.useReleaseTool(editLayer, gc, e);
  }

  void clearCanvas() {
    gc.clearRect(0, 0, canvas.getWidth(), canvas.getHeight());
  }

  void addActionsToCanvas(ArrayList<Action> actions) {
    Paint paint = gc.getStroke();
    double lineWidth = gc.getLineWidth();

    actions.forEach(action -> {
      switch (action.getTool()) {
        case PAINT -> {
          gc.setStroke(Color.web(action.getPaint()));
          gc.setLineWidth(action.getSize());
          gc.strokeLine(action.getX1(), action.getY1(), action.getX1(), action.getY1());
        }
        case ERASE -> gc.clearRect(action.getX1(), action.getY1(), action.getSize(), action.getSize());
        case LINE -> {
          gc.setStroke(Color.web(action.getPaint()));
          gc.setLineWidth(action.getSize());
          gc.strokeLine(action.getX1(), action.getY1(), action.getX2(), action.getY2());
        }
        case CIRCLE -> {
          gc.setStroke(Color.web(action.getPaint()));
          gc.setLineWidth(action.getSize());
          gc.strokeOval(action.getX1(), action.getY1(), action.getX2(), action.getY2());
        }
        case TRIANGLE -> {
          gc.setStroke(Color.web(action.getPaint()));
          gc.setLineWidth(action.getSize());
          gc.strokeLine((action.getX2() + action.getX1()) / 2, action.getY2(), action.getX2(), action.getY1());
          gc.strokeLine((action.getX2() + action.getX1()) / 2, action.getY2(), action.getX1(), action.getY1());
          gc.strokeLine(action.getX2(), action.getY1(), action.getX1(), action.getY1());
        }
        case RECTANGLE -> {
          gc.setStroke(Color.web(action.getPaint()));
          gc.setLineWidth(action.getSize());
          gc.strokeRect(action.getX1(), action.getY1(), action.getX2(), action.getY2());
        }
        case TEXT -> {
          gc.setStroke(Color.web(action.getPaint()));
          gc.setLineWidth(1);
          gc.setFont(Font.font("System", action.getSize()));
          gc.fillText(action.getText(), action.getX1(), action.getY1());
        }
      }
    });

    gc.setStroke(paint);
    gc.setLineWidth(lineWidth);
  }
}
