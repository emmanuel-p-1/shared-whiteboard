package remote;

import client.Tool;
import javafx.scene.paint.Paint;

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
}
