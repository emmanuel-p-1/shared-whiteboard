package client;

import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.Button;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;

public class Whiteboard {
  private final Canvas canvas = new Canvas(1000, 1000);
  private final StackPane canvasContainer = new StackPane(canvas);
  private final VBox toolbox = new VBox();

  private final GraphicsContext gc = canvas.getGraphicsContext2D();

  public StackPane getCanvas() {
    canvasContainer.setBorder(getBorder());
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

  public void addTools() {
    Button btn1 = new Button("ph");
    Button btn2 = new Button("ph");

    toolbox.getChildren().add(btn1);
    toolbox.getChildren().add(btn2);
  }

  public VBox getToolbox() {
    return toolbox;
  }

  public void draw(MouseEvent e) {
    gc.strokeLine(e.getX(), e.getY(), e.getX(), e.getY());
  }
}
