package client.GUI.whiteboard;

import javafx.scene.Node;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.ColorPicker;
import javafx.scene.control.Slider;
import javafx.scene.paint.Color;

/**
 * COMP90015 Assignment 2
 * Implemented by Emmanuel Pinca 1080088
 *
 * Whiteboard tools properties.
 *
 */

public enum ToolProperty {
  // Colour of brush.
  COLOUR {
    @Override
    Node getNode(GraphicsContext gc) {
      ColorPicker colorPicker = new ColorPicker();
      colorPicker.setValue(Color.BLACK);

      colorPicker.setOnAction(e -> {
        gc.setStroke(colorPicker.getValue());
      });

      return colorPicker;
    }
  },
  // Size of brush.
  SIZE {
    @Override
    Node getNode(GraphicsContext gc) {
      Slider slider = new Slider(1, 100, gc.getLineWidth());
      slider.setMaxWidth(Double.MAX_VALUE);
      slider.setMajorTickUnit(99);
      slider.setShowTickLabels(true);

      slider.setOnMouseReleased(e -> {
        gc.setLineWidth(slider.getValue());
      });

      return slider;
    }
  };

  // Gets the node for the toolbox.
  abstract Node getNode(GraphicsContext gc);
}
