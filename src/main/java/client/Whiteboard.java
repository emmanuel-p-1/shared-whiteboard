package client;

import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.Button;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;

public class Whiteboard {
  private final Canvas canvas = new Canvas(1000, 1000);
  private final StackPane canvasContainer = new StackPane(canvas);
  private final VBox toolbox = new VBox();

  private final GraphicsContext gc = canvas.getGraphicsContext2D();

  private Tool tool = Tool.PAINT;

  public Whiteboard() {
    canvasContainer.setBorder(getBorder());
    toolbox.setFillWidth(true);

    for (Tool t : Tool.values()) {
      Button btn = new Button(t.name());
      btn.setMaxWidth(Double.MAX_VALUE);
      btn.setPrefHeight(40);
      toolbox.getChildren().add(btn);

      btn.setOnAction(e -> {
        tool = t;
        t.onButtonClick(toolbox, btn, gc);
      });
    }
  }

  public StackPane getCanvas() {
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

  public VBox getToolbox() {
    return toolbox;
  }

  public void draw(MouseEvent e) {
    tool.useDragTool(gc, e);
  }

  public void click(MouseEvent e) {
    tool.useClickTool(gc, e);
  }

  public void release(MouseEvent e) {
    tool.useReleaseTool(gc, e);
  }

  public void type(KeyEvent e) {
    tool.useTypeTool(gc, e);
  }
}
