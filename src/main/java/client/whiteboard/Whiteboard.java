package client.whiteboard;

import javafx.embed.swing.SwingFXUtils;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.paint.Paint;
import javafx.scene.shape.StrokeLineCap;
import javafx.scene.text.Font;
import remote.Action;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;

public class Whiteboard {
  private final Canvas canvas = new Canvas(1000, 1000);
  private final Canvas editLayer = new Canvas(1000, 1000);
  private final TextField textLayer = new TextField();
  private final StackPane canvasContainer = new StackPane(canvas, editLayer, textLayer);
  private final VBox toolbox = new VBox();
  private final VBox toolProperties = new VBox();
  private final VBox options = new VBox();

  private final GraphicsContext gc = canvas.getGraphicsContext2D();

  private Tool tool = Tool.PAINT;

  public Whiteboard() {
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

    for (Option option : Option.values()) {
      options.getChildren().add(option.getNode(canvas));
    }
  }

  public StackPane getCanvas() {
    return canvasContainer;
  }

  private Border getBorder() {
    return new Border(new BorderStroke(Color.BLACK, BorderStrokeStyle.SOLID, CornerRadii.EMPTY, BorderWidths.DEFAULT));
  }

  public VBox getToolbox() {
    VBox box = new VBox(options, toolbox, toolProperties);
    box.setSpacing(50);
    return box;
  }

  public void draw(MouseEvent e) {
    tool.useDragTool(editLayer, gc, e);
  }

  public void click(MouseEvent e) {
    tool.useClickTool(editLayer, gc, e);
    tool.useClickTool(textLayer, gc, e);
  }

  public void release(MouseEvent e) {
    tool.useReleaseTool(editLayer, gc, e);
  }

  public void addActionsToCanvas(ArrayList<Action> actions) {
    Paint paint = gc.getStroke();
    double lineWidth = gc.getLineWidth();

    actions.forEach(action -> {
      if (action.getTool() != null && action.getOption() == null) {
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
            gc.setFill(Color.web(action.getPaint()));
            gc.setLineWidth(1);
            gc.setFont(Font.font("System", action.getSize()));
            gc.fillText(action.getText(), action.getX1(), action.getY1());
          }
        }
      } else if (action.getTool() == null && action.getOption() != null) {
        switch (action.getOption()) {
          case NEW -> {
            gc.clearRect(0, 0, canvas.getWidth(), canvas.getHeight());
          }
          case OPEN -> {
            InputStream is = new ByteArrayInputStream(action.getCanvas());
            try {
              BufferedImage bi = ImageIO.read(is);
              Image image = SwingFXUtils.toFXImage(bi, null);
              gc.drawImage(image, 0, 0, canvas.getWidth(), canvas.getHeight());
            } catch (IOException e) {
              // Unhandled Exception
              e.printStackTrace();
            }
          }
        }
      }
    });

    gc.setStroke(paint);
    gc.setLineWidth(lineWidth);
  }
}
