package client;

import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.TextField;
import javafx.scene.input.MouseEvent;
import javafx.scene.shape.StrokeLineCap;
import javafx.scene.text.Font;

enum Tool {
  PAINT {
    @Override
    void useClickTool(Canvas editLayer, GraphicsContext gc, MouseEvent e) {
      gc.strokeLine(e.getX(), e.getY(), e.getX(), e.getY());
    }

    @Override
    void useDragTool(Canvas editLayer, GraphicsContext gc, MouseEvent e) {
      gc.strokeLine(e.getX(), e.getY(), e.getX(), e.getY());
    }
  },
  ERASE {
    @Override
    void useClickTool(Canvas editLayer, GraphicsContext gc, MouseEvent e) {
      double x = e.getX() - (gc.getLineWidth() / 2);
      double y = e.getY() - (gc.getLineWidth() / 2);
      gc.clearRect(x, y, gc.getLineWidth(), gc.getLineWidth());
    }

    @Override
    void useDragTool(Canvas editLayer, GraphicsContext gc, MouseEvent e) {
      double x = e.getX() - (gc.getLineWidth() / 2);
      double y = e.getY() - (gc.getLineWidth() / 2);
      gc.clearRect(x, y, gc.getLineWidth(), gc.getLineWidth());
    }
  },
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
      edit.setLineCap(StrokeLineCap.ROUND);

      edit.clearRect(0, 0, editLayer.getWidth(), editLayer.getHeight());
      edit.strokeLine(e.getX(), e.getY(), x, y);
    }

    @Override
    void useReleaseTool(Canvas editLayer, GraphicsContext gc, MouseEvent e) {
      GraphicsContext edit = editLayer.getGraphicsContext2D();
      edit.clearRect(0, 0, editLayer.getWidth(), editLayer.getHeight());

      gc.strokeLine(e.getX(), e.getY(), x, y);
    }
  },
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

      gc.strokeOval(topLeftX, topLeftY, width, height);
    }
  },
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

      gc.strokeLine((x + e.getX()) / 2, y, x, e.getY());
      gc.strokeLine((x + e.getX()) / 2, y, e.getX(), e.getY());
      gc.strokeLine(x, e.getY(), e.getX(), e.getY());
    }
  },
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
      gc.strokeRect(topLeftX, topLeftY, width, height);
    }
  },
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
        gc.setLineWidth(1);
        gc.setFont(Font.font(fontName, textSize));
        gc.fillText(text.getText(), posX, posY);
        gc.setLineWidth(textSize);
        text.setVisible(false);
      });
    }
  };

  void useDragTool(Canvas editLayer, GraphicsContext gc, MouseEvent e) {}

  void useClickTool(Canvas editLayer, GraphicsContext gc, MouseEvent e) {}

  void useClickTool(TextField textLayer, GraphicsContext gc, MouseEvent e) {}

  void useReleaseTool(Canvas editLayer, GraphicsContext gc, MouseEvent e) {}
}
