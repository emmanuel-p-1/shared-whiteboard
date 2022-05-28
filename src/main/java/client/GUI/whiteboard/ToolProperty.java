package client.GUI.whiteboard;

import javafx.geometry.Orientation;
import javafx.scene.Node;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.Button;
import javafx.scene.control.Slider;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;

enum ToolProperty {
  COLOUR {
    private final Color[] colors = {
            Color.BLACK, Color.WHITE, Color.AQUA, Color.BLUE,
            Color.VIOLET, Color.BURLYWOOD, Color.CYAN, Color.DARKBLUE,
            Color.DARKGREEN, Color.RED, Color.LIME, Color.MAGENTA,
            Color.MAROON, Color.ORANGE, Color.PLUM, Color.YELLOW};

    @Override
    Node getNode(GraphicsContext gc) {
      int n = 0;
      GridPane palette = new GridPane();

      // Set column width
      for (int i = 0; i < 2; i++) {
        ColumnConstraints columnConstraints = new ColumnConstraints();
        columnConstraints.setPercentWidth(50);
        palette.getColumnConstraints().add(i, columnConstraints);
      }

      for (int i = 0; i < 8; i++) {
        for (int j = 0; j < 2; j++) {
          Color color = colors[n];

          Button btn = new Button();
          btn.setMaxWidth(Double.MAX_VALUE);
          btn.setBackground(new Background(new BackgroundFill(color, null, null)));

          palette.add(btn, j, i);

          btn.setOnAction(e -> {
            gc.setStroke(color);
          });

          n++;
        }
      }

      return palette;
    }
  },
  SIZE {
    @Override
    Node getNode(GraphicsContext gc) {
      Slider slider = new Slider(1, 100, gc.getLineWidth());
      slider.setOrientation(Orientation.VERTICAL);
      slider.setMaxWidth(Double.MAX_VALUE);
      slider.setMajorTickUnit(99);
      slider.setShowTickLabels(true);

      slider.setOnMouseReleased(e -> {
        gc.setLineWidth(slider.getValue());
      });

      return slider;
    }
  };

  abstract Node getNode(GraphicsContext gc);
}
