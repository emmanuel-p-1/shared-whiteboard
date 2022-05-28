package remote.serializable;

import client.GUI.whiteboard.File;
import client.GUI.whiteboard.Tool;

import java.io.Serializable;

public class Action implements Serializable {
  private final Tool tool;
  private final String text;
  private final double x1;
  private final double y1;
  private final double x2;
  private final double y2;
  private final double size;
  private final String paint;

  private final File file;
  private final byte[] canvas;

  // Erase
  public Action(Tool tool, double x1, double y1, double size) {
    this.tool = tool;
    this.text = null;
    this.x1 = x1;
    this.y1 = y1;
    this.x2 = 0;
    this.y2 = 0;
    this.size = size;
    this.paint = null;
    this.file = null;
    this.canvas = null;
  }

  // Paint
  public Action(Tool tool, double x1, double y1, double size, String paint) {
    this.tool = tool;
    this.text = null;
    this.x1 = x1;
    this.y1 = y1;
    this.x2 = 0;
    this.y2 = 0;
    this.size = size;
    this.paint = paint;
    this.file = null;
    this.canvas = null;
  }

  // Text
  public Action(Tool tool, String text, double x1, double y1, double size, String paint) {
    this.tool = tool;
    this.text = text;
    this.x1 = x1;
    this.y1 = y1;
    this.x2 = 0;
    this.y2 = 0;
    this.size = size;
    this.paint = paint;
    this.file = null;
    this.canvas = null;
  }

  // Line, Circle, Triangle, Rectangle
  public Action(Tool tool, double x1, double y1, double x2, double y2, double size, String paint) {
    this.tool = tool;
    this.text = null;
    this.x1 = x1;
    this.y1 = y1;
    this.x2 = x2;
    this.y2 = y2;
    this.size = size;
    this.paint = paint;
    this.file = null;
    this.canvas = null;
  }

  // New/Open canvas
  public Action(File file, byte[] canvas) {
    this.tool = null;
    this.text = null;
    this.x1 = 0;
    this.y1 = 0;
    this.x2 = 0;
    this.y2 = 0;
    this.size = 0;
    this.paint = null;
    this.file = file;
    this.canvas = canvas;
  }

  public Tool getTool() {
    return tool;
  }

  public String getText() {
    return text;
  }

  public double getX1() {
    return x1;
  }

  public double getY1() {
    return y1;
  }

  public double getSize() {
    return size;
  }

  public String getPaint() {
    return paint;
  }

  public double getX2() {
    return x2;
  }

  public double getY2() {
    return y2;
  }

  public File getOption() {
    return file;
  }

  public byte[] getCanvas() {
    return canvas;
  }
}
