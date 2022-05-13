package client;

import javafx.scene.canvas.GraphicsContext;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseEvent;

public enum Tool {
  PAINT {
    @Override
    public void useClickTool(GraphicsContext gc, MouseEvent e) {
      gc.strokeLine(e.getX(), e.getY(), e.getX(), e.getY());
    }

    @Override
    public void useDragTool(GraphicsContext gc, MouseEvent e) {
      gc.strokeLine(e.getX(), e.getY(), e.getX(), e.getY());
    }
  },
  ERASE {
    @Override
    public void useClickTool(GraphicsContext gc, MouseEvent e) {
      gc.strokeLine(e.getX(), e.getY(), e.getX(), e.getY());
    }

    @Override
    public void useDragTool(GraphicsContext gc, MouseEvent e) {
      gc.clearRect(e.getX(), e.getY(), 1, 1);
    }
  },
  LINE {
    double x, y;

    @Override
    public void useClickTool(GraphicsContext gc, MouseEvent e) {
      x = e.getX();
      y = e.getY();
    }

    @Override
    public void useReleaseTool(GraphicsContext gc, MouseEvent e) {
      gc.strokeLine(e.getX(), e.getY(), x, y);
    }
  },
  CIRCLE {
    double x, y;

    @Override
    public void useClickTool(GraphicsContext gc, MouseEvent e) {
      x = e.getX();
      y = e.getY();
    }

    @Override
    public void useReleaseTool(GraphicsContext gc, MouseEvent e) {
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
    public void useClickTool(GraphicsContext gc, MouseEvent e) {
      x = e.getX();
      y = e.getY();
    }

    @Override
    public void useReleaseTool(GraphicsContext gc, MouseEvent e) {
      gc.strokeLine((x + e.getX()) / 2, y, x, e.getY());
      gc.strokeLine((x + e.getX()) / 2, y, e.getX(), e.getY());
      gc.strokeLine(x, e.getY(), e.getX(), e.getY());
    }
  },
  RECTANGLE {
    double x, y;

    @Override
    public void useClickTool(GraphicsContext gc, MouseEvent e) {
      x = e.getX();
      y = e.getY();
    }

    @Override
    public void useReleaseTool(GraphicsContext gc, MouseEvent e) {
      double topLeftX = Math.min(x, e.getX());
      double topLeftY = Math.min(y, e.getY());
      double bottomRightX = Math.max(x, e.getX());
      double bottomRightY = Math.max(y, e.getY());

      double width = bottomRightX - topLeftX;
      double height = bottomRightY - topLeftY;

      gc.strokeRect(topLeftX, topLeftY, width, height);
    }
  },
  TEXT {
    double x, y;
    boolean pointSelected = false;

    @Override
    public void useClickTool(GraphicsContext gc, MouseEvent e) {
      x = e.getX();
      y = e.getY();
      pointSelected = true;
    }

    @Override
    public void useTypeTool(GraphicsContext gc, KeyEvent e) {
      if (pointSelected) {
        gc.strokeText(e.getText(), x, y);
        x += 8;
      }
    }
  };

  public void useDragTool(GraphicsContext gc, MouseEvent e) {}

  public void useClickTool(GraphicsContext gc, MouseEvent e) {}

  public void useReleaseTool(GraphicsContext gc, MouseEvent e) {}

  public void useTypeTool(GraphicsContext gc, KeyEvent e) {}
}
