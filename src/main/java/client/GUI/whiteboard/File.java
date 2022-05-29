package client.GUI.whiteboard;

import client.Client;
import client.GUI.users.UserPane;
import javafx.embed.swing.SwingFXUtils;
import javafx.scene.Node;
import javafx.scene.SnapshotParameters;
import javafx.scene.canvas.Canvas;
import javafx.scene.control.Button;
import javafx.scene.image.WritableImage;
import javafx.scene.paint.Color;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import remote.serializable.Action;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.awt.image.RenderedImage;
import java.io.ByteArrayOutputStream;
import java.io.IOException;

/**
 * COMP90015 Assignment 2
 * Implemented by Emmanuel Pinca 1080088
 *
 * Admin tools for whiteboard.
 *
 */

public enum File {
  // Clear page.
  NEW {
    @Override
    Node getNode(Canvas canvas, Stage stage) {
      Button btn = new Button("NEW");
      btn.setMaxWidth(Double.MAX_VALUE);
      btn.setPrefHeight(40);

      btn.setOnAction(e -> {
        File.filepath = null;
        Client.addAction(new Action(File.NEW, null));
      });

      return btn;
    }
  },
  // Open image as page.
  OPEN {
    @Override
    Node getNode(Canvas canvas, Stage stage) {
      Button btn = new Button("OPEN");
      btn.setMaxWidth(Double.MAX_VALUE);
      btn.setPrefHeight(40);

      btn.setOnAction(e -> {
        FileChooser fileChooser = new FileChooser();
        java.io.File file = fileChooser.showOpenDialog(stage);

        if (file == null) return;

        File.filepath = file.getPath();

        // Image to byte array.
        try {
          BufferedImage img = ImageIO.read(file);
          ByteArrayOutputStream byteArrayOutputStream = new
                  ByteArrayOutputStream();
          ImageIO.write(img, "png", byteArrayOutputStream);
          Client.addAction(new Action(File.OPEN,
                  byteArrayOutputStream.toByteArray()));
        } catch (IOException ex) {
          UserPane.appendOutput("Error opening image");
        }
      });

      return btn;
    }
  },
  // Save canvas as image on local.
  SAVE {
    @Override
    Node getNode(Canvas canvas, Stage stage) {
      Button btn = new Button("SAVE");
      btn.setMaxWidth(Double.MAX_VALUE);
      btn.setPrefHeight(40);

      btn.setOnAction(e -> {
        if (File.filepath == null) {
          File.saveAs(canvas, stage);
        } else {
          java.io.File file = new java.io.File(filepath);
          File.save(canvas, file);
        }
      });

      return btn;
    }
  },
  // Save canvas as image on local.
  SAVE_AS {
    @Override
    Node getNode(Canvas canvas, Stage stage) {
      Button btn = new Button("SAVE AS");
      btn.setMaxWidth(Double.MAX_VALUE);
      btn.setPrefHeight(40);

      btn.setOnAction(e -> {
        File.saveAs(canvas, stage);
      });

      return btn;
    }
  },
  // Clear and lock canvas.
  CLOSE {
    @Override
    Node getNode(Canvas canvas, Stage stage) {
      Button btn = new Button("CLOSE");
      btn.setMaxWidth(Double.MAX_VALUE);
      btn.setPrefHeight(40);

      btn.setOnAction(e -> {
        Client.addAction(new Action(File.CLOSE, null));
      });

      return btn;
    }
  };

  // Filepath for current canvas.
  private static String filepath = null;

  // Get button for each file action.
  abstract Node getNode(Canvas canvas, Stage stage);

  // Save at specified filepath.
  private static void saveAs(Canvas canvas, Stage stage) {
    FileChooser fileChooser = new FileChooser();
    fileChooser.setTitle("Save Canvas");

    FileChooser.ExtensionFilter extensionFilter = new
            FileChooser.ExtensionFilter("PNG files", "*.PNG");
    fileChooser.getExtensionFilters().add(extensionFilter);

    java.io.File file = fileChooser.showSaveDialog(stage);

    if (file == null) return;

    File.filepath = file.getPath();

    save(canvas, file);
  }

  // Save at filepath.
  private static void save(Canvas canvas, java.io.File file) {
    try {
      WritableImage writableImage = new WritableImage((int)Math.round(
              canvas.getHeight()), (int)Math.round(canvas.getWidth()));

      SnapshotParameters snapshotParameters = new SnapshotParameters();
      snapshotParameters.setFill(Color.TRANSPARENT);
      canvas.snapshot(snapshotParameters, writableImage);

      RenderedImage renderedImage = SwingFXUtils.fromFXImage(writableImage,
              null);
      ImageIO.write(renderedImage, "png", file);
    } catch (IOException ex) {
      UserPane.appendOutput("Error saving image");
    }
  }
}
