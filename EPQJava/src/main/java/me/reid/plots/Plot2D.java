package me.reid.plots;

import lombok.SneakyThrows;
import me.reid.utils.ExcelUtils;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import javax.swing.*;
import java.awt.*;
import java.awt.geom.Ellipse2D;
import java.awt.geom.Line2D;
import java.io.File;
import java.io.FileInputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

/**
 * @author Reid
 * @since January 24, 2024
 */
public class Plot2D extends JPanel {

  public static int counter = 10;

  private final Color pointColor = Color.RED;
  private final Color lineColor = Color.BLUE;

  int x_max = 400;
  int y_max = 400;
  int marg = 20;

  private final List<Float> x_values;
  private final List<Float> y_values;
  private List<Float> currentPointsX = new ArrayList<>();
  private List<Float> currentPointsY = new ArrayList<>();

  @SneakyThrows
  public Plot2D(String sheetName) {
    super();
    File dataFile = new File("Databook.xlsx");
    XSSFWorkbook workbook = new XSSFWorkbook(new FileInputStream(dataFile));
    XSSFSheet sheet = workbook.getSheet(sheetName);
    x_values = ExcelUtils.getColumnValues(sheet,6);
    y_values = ExcelUtils.getColumnValues(sheet,7);
    JFrame frame = new JFrame();
    frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    frame.add(this);
    frame.setSize(600, 600);
    frame.setLocation(200, 200);
    frame.setVisible(true);

    ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(1);

    scheduler.scheduleAtFixedRate(
        frame::repaint,
        1000, 50,
        TimeUnit.MILLISECONDS);
  }


  protected void paintComponent(Graphics grf) {
    super.paintComponent(grf);
    Graphics2D graph = (Graphics2D) grf;

    graph.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

    int width = getWidth();
    int height = getHeight();

    // Find value of x and scale to plot points
    double x_scale = (double) (width - 2 * marg) / (2 * x_max);
    double y_scale = (double) (height - 2 * marg) / (2 * y_max);

    graph.setPaint(pointColor);

    // Drw axis
    graph.setPaint(Color.BLACK);
    graph.setFont(new Font("Arial", Font.PLAIN, 12));
    graph.draw(new Line2D.Double(marg, (double) height / 2, getWidth() - marg, (double) height / 2));
    graph.draw(new Line2D.Double((double) width / 2, marg, (double) width / 2, height - marg));
    for (int i = -x_max; i <= x_max; i++) {
      if (i % 100 != 0) {
        continue;
      }
      graph.draw(new Line2D.Double(((double) width / 2) + i * x_scale, (double) getHeight() / 2, ((double) width / 2) + i * x_scale, (double) getHeight() / 2 + 5));
      if (i != 0) {
        graph.drawString(String.valueOf(i), (int) (((double) width / 2) + i * x_scale) - 15, getHeight() / 2 - 7);
      }
    }
    for (int i = -y_max; i <= y_max; i++) {
      if (i % 100 != 0) {
        continue;
      }
      graph.draw(new Line2D.Double((double) getWidth() / 2, (double) (height / 2) - i*y_scale, (double) getWidth() / 2 - 5, (double) (height / 2) - i*y_scale));
      if (i != 0) {
        graph.drawString(String.valueOf(i), getWidth() / 2 + 7, (int) ((double) (height / 2) - i*y_scale)-5);
      }
    }

    updatePoints();

    double lastX = 0;
    double lastY = 0;
    for (int i = 0; i < currentPointsX.size(); i++) {
      double x = currentPointsX.get(i);
      double y = currentPointsY.get(i);
      double x1 = (double) (width / 2) + x * x_scale;
      double y1 = (double) (height / 2) - y * y_scale;
      graph.setColor(new Color(lineColor.getRed(), lineColor.getGreen(), lineColor.getBlue(), (i + 1) * 100 / currentPointsX.size() + 155));
      if (lastX != 0 && lastY != 0) {
        graph.draw(new Line2D.Float((float) lastX, (float) lastY, (float) x1, (float) y1));
      }
      lastX = x1;
      lastY = y1;
      graph.setColor(new Color(pointColor.getRed(), pointColor.getGreen(), pointColor.getBlue(), (i + 1) * 100 / currentPointsX.size() + 155));
      graph.fill(new Ellipse2D.Double(x1 - 2, y1 - 2, 4, 4));
    }
  }

  private void updatePoints() {
    if (currentPointsX.isEmpty()) {
      currentPointsX = x_values.subList(0, 10);
    } else {
      currentPointsX.remove(0);
      if (x_values.size() > counter) {
        currentPointsX.add(x_values.get(counter));
      }
    }
    if (currentPointsY.isEmpty()) {
      currentPointsY = y_values.subList(0, 10);
    } else {
      currentPointsY.remove(0);
      if (x_values.size() > counter) {
        currentPointsY.add(y_values.get(counter));
      }
    }
    counter++;
  }
}

