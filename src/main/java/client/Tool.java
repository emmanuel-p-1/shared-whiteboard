package client;

import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.Button;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.Background;
import javafx.scene.layout.BackgroundFill;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;

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
      gc.clearRect(e.getX(), e.getY(), 1, 1);
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
  },
  COLOUR {
    private final Color[] colors = {
            Color.BLACK, Color.WHITE, Color.AQUA, Color.BLUE,
            Color.VIOLET, Color.BURLYWOOD, Color.CYAN, Color.DARKBLUE,
            Color.DARKGREEN, Color.RED, Color.LIME, Color.MAGENTA,
            Color.MAROON, Color.ORANGE, Color.PLUM, Color.YELLOW};

    @Override
    public void onButtonClick(VBox toolbox, Button button, GraphicsContext gc) {
      toolbox.getChildren().remove(button);

      int n = 0;
      GridPane palette = new GridPane();
      for (int i = 0; i < 8; i++) {
        for (int j = 0; j < 2; j++) {
          Color color = colors[n];

          Button btn = new Button();
          btn.setMaxWidth(Double.MAX_VALUE);
          btn.setBackground(new Background(new BackgroundFill(color, null, null)));

          palette.add(btn, j, i);

          btn.setOnAction(e -> {
            gc.setStroke(color);
            toolbox.getChildren().remove(palette);
            toolbox.getChildren().add(COLOUR.ordinal(), button);
          });

          n++;
        }
      }

      toolbox.getChildren().add(COLOUR.ordinal(), palette);
    }
  };

  public void useDragTool(GraphicsContext gc, MouseEvent e) {}

  public void useClickTool(GraphicsContext gc, MouseEvent e) {}

  public void useReleaseTool(GraphicsContext gc, MouseEvent e) {}

  public void useTypeTool(GraphicsContext gc, KeyEvent e) {}

  public void onButtonClick(VBox toolbox, Button button, GraphicsContext gc) {}
}
