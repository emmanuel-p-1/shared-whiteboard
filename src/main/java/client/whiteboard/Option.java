package client.whiteboard;

import client.Client;
import javafx.embed.swing.SwingFXUtils;
import javafx.scene.Node;
import javafx.scene.SnapshotParameters;
import javafx.scene.canvas.Canvas;
import javafx.scene.control.Button;
import javafx.scene.image.WritableImage;
import javafx.scene.paint.Color;
import javafx.stage.FileChooser;
import remote.Action;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.awt.image.RenderedImage;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;


public enum Option {
  NEW {
    @Override
    Node getNode(Canvas canvas) {
      Button btn = new Button("NEW");
      btn.setMaxWidth(Double.MAX_VALUE);
      btn.setPrefHeight(40);

      btn.setOnAction(e -> {
        Client.recentActions.add(new Action(Option.NEW, null));
      });

      return btn;
    }
  },
  OPEN {
    @Override
    Node getNode(Canvas canvas) {
      Button btn = new Button("OPEN");
      btn.setMaxWidth(Double.MAX_VALUE);
      btn.setPrefHeight(40);

      btn.setOnAction(e -> {
        FileChooser fileChooser = new FileChooser();
        File file = fileChooser.showOpenDialog(Client.getStage());

        if (file != null) {
          try {
            BufferedImage img = ImageIO.read(file);
            ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
            ImageIO.write(img, "png", byteArrayOutputStream);
            Client.recentActions.add(new Action(Option.OPEN, byteArrayOutputStream.toByteArray()));
          } catch (IOException ex) {
            ex.printStackTrace();
          }
        }
      });

      return btn;
    }
  },
  SAVE {
    @Override
    Node getNode(Canvas canvas) {
      Button btn = new Button("SAVE");
      btn.setMaxWidth(Double.MAX_VALUE);
      btn.setPrefHeight(40);
      return btn;
    }
  },
  SAVE_AS {
    @Override
    Node getNode(Canvas canvas) {
      Button btn = new Button("SAVE AS");
      btn.setMaxWidth(Double.MAX_VALUE);
      btn.setPrefHeight(40);

      btn.setOnAction(e -> {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Save Canvas");

        FileChooser.ExtensionFilter extensionFilter = new FileChooser.ExtensionFilter("PNG files", "*.PNG");
        fileChooser.getExtensionFilters().add(extensionFilter);

        File file = fileChooser.showSaveDialog(Client.getStage());

        if (file != null) {
          try {
            WritableImage writableImage = new WritableImage((int)Math.round(canvas.getHeight()), (int)Math.round(canvas.getWidth()));

            SnapshotParameters snapshotParameters = new SnapshotParameters();
            snapshotParameters.setFill(Color.TRANSPARENT);
            canvas.snapshot(snapshotParameters, writableImage);

            RenderedImage renderedImage = SwingFXUtils.fromFXImage(writableImage, null);
            ImageIO.write(renderedImage, "png", file);
          } catch (IOException ex) {
            ex.printStackTrace();
          }
        }
      });

      return btn;
    }
  },
  CLOSE {
    @Override
    Node getNode(Canvas canvas) {
      Button btn = new Button("CLOSE");
      btn.setMaxWidth(Double.MAX_VALUE);
      btn.setPrefHeight(40);
      return btn;
    }
  };

  abstract Node getNode(Canvas canvas);
}
