package client.GUI.whiteboard;

import client.Client;
import client.GUI.users.UserPane;
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
import remote.serializable.Action;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;

/**
 * COMP90015 Assignment 2
 * Implemented by Emmanuel Pinca 1080088
 *
 * Shared whiteboard canvas.
 *
 */

public class Whiteboard {
  // Canvas for confirmed actions.
  private final Canvas canvas = new Canvas(800, 800);
  // Canvas for local actions.
  private final Canvas editLayer = new Canvas(800, 800);
  // Text (canvas) for local text.
  private final TextField textLayer = new TextField();
  // Stack of all components.
  private final StackPane canvasContainer = new StackPane(canvas, editLayer,
          textLayer);

  // Brush toolbox.
  private final VBox toolbox = new VBox();
  // Brush properties.
  private final VBox toolProperties = new VBox();
  // File menu.
  private final VBox options = new VBox();

  // The brush.
  private final GraphicsContext gc = canvas.getGraphicsContext2D();

  // Initial tool.
  private Tool tool = Tool.PAINT;

  // Create whiteboard with all tools.
  public Whiteboard(Client client) {
    canvasContainer.setBorder(getBorder());
    toolbox.setFillWidth(true);
    textLayer.setVisible(false);
    textLayer.setBackground(Background.EMPTY);
    textLayer.setMaxWidth(800);

    toolProperties.setSpacing(10);

    gc.setLineWidth(10);
    gc.setLineCap(StrokeLineCap.ROUND);

    // Get tool buttons.
    for (Tool t : Tool.values()) {
      Button btn = new Button(t.name());
      btn.setMaxWidth(Double.MAX_VALUE);
      btn.setPrefHeight(40);
      toolbox.getChildren().add(btn);

      btn.setOnAction(e -> {
        tool = t;
      });
    }

    // Get tool property buttons.
    for (ToolProperty t : ToolProperty.values()) {
      toolProperties.getChildren().add(t.getNode(gc));
    }

    // Get file menu buttons.
    for (File file : File.values()) {
      options.getChildren().add(file.getNode(canvas, client.getStage()));
    }
  }

  // Get stack of all "canvases"
  public StackPane getCanvas() {
    return canvasContainer;
  }

  // Set canvas border
  private Border getBorder() {
    Color color = Color.BLACK;
    BorderStrokeStyle borderStyle = BorderStrokeStyle.SOLID;
    CornerRadii corner = CornerRadii.EMPTY;
    BorderWidths width = BorderWidths.DEFAULT;

    return new Border(new BorderStroke(color, borderStyle, corner, width));
  }

  // Get toolbox
  public VBox getToolbox() {
    VBox box = new VBox(toolbox, toolProperties);
    box.setSpacing(50);
    box.setMaxWidth(100);
    return box;
  }

  // Get toolbox with file menu (for admins)
  public VBox getAdminToolbox() {
    VBox box = new VBox(options, toolbox, toolProperties);
    box.setSpacing(50);
    box.setMaxWidth(100);
    return box;
  }

  // When mouse dragged across canvas.
  public void draw(MouseEvent e) {
    tool.useDragTool(editLayer, gc, e);
  }

  // When mouse clicks on canvas.
  public void click(MouseEvent e) {
    tool.useClickTool(editLayer, gc, e);
    tool.useClickTool(textLayer, gc, e);
  }

  // When mouse button is released.
  public void release(MouseEvent e) {
    tool.useReleaseTool(editLayer, gc, e);
  }

  // For received data when read from remote.
  public void processActions(ArrayList<Action> actions) {
    Paint paint = gc.getStroke();
    double lineWidth = gc.getLineWidth();

    actions.forEach(action -> {
      if (action.getTool() != null && action.getOption() == null) {
        switch (action.getTool()) {
          case PAINT -> {
            gc.setStroke(Color.web(action.getPaint()));
            gc.setLineWidth(action.getSize());
            gc.strokeLine(action.getX1(), action.getY1(), action.getX1(),
                    action.getY1());
          }
          case ERASE -> gc.clearRect(action.getX1(), action.getY1(),
                  action.getSize(), action.getSize());
          case LINE -> {
            gc.setStroke(Color.web(action.getPaint()));
            gc.setLineWidth(action.getSize());
            gc.strokeLine(action.getX1(), action.getY1(), action.getX2(),
                    action.getY2());
          }
          case CIRCLE -> {
            gc.setStroke(Color.web(action.getPaint()));
            gc.setLineWidth(action.getSize());
            gc.strokeOval(action.getX1(), action.getY1(), action.getX2(),
                    action.getY2());
          }
          case TRIANGLE -> {
            gc.setStroke(Color.web(action.getPaint()));
            gc.setLineWidth(action.getSize());
            gc.strokeLine((action.getX2() + action.getX1()) / 2,
                    action.getY2(), action.getX2(), action.getY1());
            gc.strokeLine((action.getX2() + action.getX1()) / 2,
                    action.getY2(), action.getX1(), action.getY1());
            gc.strokeLine(action.getX2(), action.getY1(), action.getX1(),
                    action.getY1());
          }
          case RECTANGLE -> {
            gc.setStroke(Color.web(action.getPaint()));
            gc.setLineWidth(action.getSize());
            gc.strokeRect(action.getX1(), action.getY1(), action.getX2(),
                    action.getY2());
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
          case NEW, CLOSE -> {
            gc.clearRect(0, 0, canvas.getWidth(), canvas.getHeight());
          }
          case OPEN -> {
            InputStream is = new ByteArrayInputStream(action.getCanvas());
            try {
              BufferedImage bi = ImageIO.read(is);
              Image image = SwingFXUtils.toFXImage(bi, null);
              gc.drawImage(image, 0, 0, canvas.getWidth(), canvas.getHeight());
            } catch (IOException e) {
              UserPane.appendOutput("Error uploading image");
            }
          }
        }
      }
    });

    gc.setStroke(paint);
    gc.setLineWidth(lineWidth);
  }
}
