package client.GUI.whiteboard;

import client.Client;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.TextField;
import javafx.scene.input.MouseEvent;
import javafx.scene.shape.StrokeLineCap;
import javafx.scene.text.Font;
import remote.serializable.Action;

/**
 * COMP90015 Assignment 2
 * Implemented by Emmanuel Pinca 1080088
 *
 * Whiteboard tools.
 *
 */

public enum Tool {
  // Paint tool.
  PAINT {
    @Override
    void useClickTool(Canvas editLayer, GraphicsContext gc, MouseEvent e) {
      Client.addAction(new Action(Tool.PAINT, e.getX(), e.getY(),
              gc.getLineWidth(), gc.getStroke().toString()));
    }

    @Override
    void useDragTool(Canvas editLayer, GraphicsContext gc, MouseEvent e) {
      Client.addAction(new Action(Tool.PAINT, e.getX(), e.getY(),
              gc.getLineWidth(), gc.getStroke().toString()));
    }
  },
  // Erase tool.
  ERASE {
    @Override
    void useClickTool(Canvas editLayer, GraphicsContext gc, MouseEvent e) {
      double x = e.getX() - (gc.getLineWidth() / 2);
      double y = e.getY() - (gc.getLineWidth() / 2);
      Client.addAction(new Action(Tool.ERASE, x, y, gc.getLineWidth()));
    }

    @Override
    void useDragTool(Canvas editLayer, GraphicsContext gc, MouseEvent e) {
      double x = e.getX() - (gc.getLineWidth() / 2);
      double y = e.getY() - (gc.getLineWidth() / 2);
      Client.addAction(new Action(Tool.ERASE, x, y, gc.getLineWidth()));
    }
  },
  // Line tool.
  LINE {
    double x, y;

    @Override
    void useClickTool(Canvas editLayer, GraphicsContext gc, MouseEvent e) {
      x = e.getX();
      y = e.getY();
    }

    @Override
    void useDragTool(Canvas editLayer, GraphicsContext gc, MouseEvent e) {
      GraphicsContext edit = editLayer.getGraphicsContext2D();
      edit.setLineWidth(gc.getLineWidth());
      edit.setStroke(gc.getStroke());
      edit.setLineCap(StrokeLineCap.ROUND);

      edit.clearRect(0, 0, editLayer.getWidth(), editLayer.getHeight());
      edit.strokeLine(e.getX(), e.getY(), x, y);
    }

    @Override
    void useReleaseTool(Canvas editLayer, GraphicsContext gc, MouseEvent e) {
      GraphicsContext edit = editLayer.getGraphicsContext2D();
      edit.clearRect(0, 0, editLayer.getWidth(), editLayer.getHeight());
      Client.addAction(new Action(Tool.LINE, e.getX(), e.getY(), x, y,
              gc.getLineWidth(), gc.getStroke().toString()));
    }
  },
  // Circle tool.
  CIRCLE {
    double x, y;

    @Override
    void useClickTool(Canvas editLayer, GraphicsContext gc, MouseEvent e) {
      x = e.getX();
      y = e.getY();
    }

    @Override
    void useDragTool(Canvas editLayer, GraphicsContext gc, MouseEvent e) {
      GraphicsContext edit = editLayer.getGraphicsContext2D();
      edit.setLineWidth(gc.getLineWidth());
      edit.setStroke(gc.getStroke());
      edit.setLineCap(StrokeLineCap.ROUND);

      double topLeftX = Math.min(x, e.getX());
      double topLeftY = Math.min(y, e.getY());
      double bottomRightX = Math.max(x, e.getX());
      double bottomRightY = Math.max(y, e.getY());

      double width = bottomRightX - topLeftX;
      double height = bottomRightY - topLeftY;

      edit.clearRect(0, 0, editLayer.getWidth(), editLayer.getHeight());
      edit.strokeOval(topLeftX, topLeftY, width, height);
    }

    @Override
    void useReleaseTool(Canvas editLayer, GraphicsContext gc, MouseEvent e) {
      GraphicsContext edit = editLayer.getGraphicsContext2D();
      edit.clearRect(0, 0, editLayer.getWidth(), editLayer.getHeight());

      double topLeftX = Math.min(x, e.getX());
      double topLeftY = Math.min(y, e.getY());
      double bottomRightX = Math.max(x, e.getX());
      double bottomRightY = Math.max(y, e.getY());

      double width = bottomRightX - topLeftX;
      double height = bottomRightY - topLeftY;
      Client.addAction(new Action(Tool.CIRCLE, topLeftX, topLeftY, width,
              height, gc.getLineWidth(), gc.getStroke().toString()));
    }
  },
  // Triangle tool.
  TRIANGLE {
    double x, y;

    @Override
    void useClickTool(Canvas editLayer, GraphicsContext gc, MouseEvent e) {
      x = e.getX();
      y = e.getY();
    }

    @Override
    void useDragTool(Canvas editLayer, GraphicsContext gc, MouseEvent e) {
      GraphicsContext edit = editLayer.getGraphicsContext2D();
      edit.setLineWidth(gc.getLineWidth());
      edit.setStroke(gc.getStroke());
      edit.setLineCap(StrokeLineCap.ROUND);

      edit.clearRect(0, 0, editLayer.getWidth(), editLayer.getHeight());
      edit.strokeLine((x + e.getX()) / 2, y, x, e.getY());
      edit.strokeLine((x + e.getX()) / 2, y, e.getX(), e.getY());
      edit.strokeLine(x, e.getY(), e.getX(), e.getY());
    }

    @Override
    void useReleaseTool(Canvas editLayer, GraphicsContext gc, MouseEvent e) {
      GraphicsContext edit = editLayer.getGraphicsContext2D();
      edit.clearRect(0, 0, editLayer.getWidth(), editLayer.getHeight());
      Client.addAction(new Action(Tool.TRIANGLE, e.getX(), e.getY(), x, y,
              gc.getLineWidth(), gc.getStroke().toString()));
    }
  },
  // Rectangle tool.
  RECTANGLE {
    double x, y;

    @Override
    void useClickTool(Canvas editLayer, GraphicsContext gc, MouseEvent e) {
      x = e.getX();
      y = e.getY();
    }

    @Override
    void useDragTool(Canvas editLayer, GraphicsContext gc, MouseEvent e) {
      GraphicsContext edit = editLayer.getGraphicsContext2D();
      edit.setLineWidth(gc.getLineWidth());
      edit.setStroke(gc.getStroke());
      edit.setLineCap(StrokeLineCap.ROUND);

      double topLeftX = Math.min(x, e.getX());
      double topLeftY = Math.min(y, e.getY());
      double bottomRightX = Math.max(x, e.getX());
      double bottomRightY = Math.max(y, e.getY());

      double width = bottomRightX - topLeftX;
      double height = bottomRightY - topLeftY;

      edit.clearRect(0, 0, editLayer.getWidth(), editLayer.getHeight());
      edit.strokeRect(topLeftX, topLeftY, width, height);
    }

    @Override
    void useReleaseTool(Canvas editLayer, GraphicsContext gc, MouseEvent e) {
      GraphicsContext edit = editLayer.getGraphicsContext2D();

      double topLeftX = Math.min(x, e.getX());
      double topLeftY = Math.min(y, e.getY());
      double bottomRightX = Math.max(x, e.getX());
      double bottomRightY = Math.max(y, e.getY());

      double width = bottomRightX - topLeftX;
      double height = bottomRightY - topLeftY;

      edit.clearRect(0, 0, editLayer.getWidth(), editLayer.getHeight());
      Client.addAction(new Action(Tool.RECTANGLE, topLeftX, topLeftY, width,
              height, gc.getLineWidth(), gc.getStroke().toString()));
    }
  },
  // Text tool.
  TEXT {
    @Override
    void useClickTool(TextField text, GraphicsContext gc, MouseEvent e) {
      double textSize = gc.getLineWidth();
      double posX = e.getX() + (textSize / 2);
      double posY = e.getY() + (textSize / 2);
      String fontName = text.getFont().getName();

      text.setVisible(true);
      text.clear();
      text.setTranslateX(e.getX());
      text.setTranslateY(e.getY() - (text.getScene().getHeight() / 2));
      text.setFont(Font.font(fontName, textSize));
      text.requestFocus();

      text.setOnAction(ev -> {
        Client.addAction(new Action(Tool.TEXT, text.getText(), posX, posY,
                textSize, gc.getStroke().toString()));
        text.setVisible(false);
      });
    }
  };

  // When mouse dragged across canvas.
  void useDragTool(Canvas editLayer, GraphicsContext gc, MouseEvent e) {}

  // When mouse clicks on canvas.
  void useClickTool(Canvas editLayer, GraphicsContext gc, MouseEvent e) {}

  // When mouse clicks on canvas (for text).
  void useClickTool(TextField textLayer, GraphicsContext gc, MouseEvent e) {}

  // When mouse button is released.
  void useReleaseTool(Canvas editLayer, GraphicsContext gc, MouseEvent e) {}
}
