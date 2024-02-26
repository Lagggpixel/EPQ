package me.reid.plots;

import lombok.SneakyThrows;
import me.reid.utils.ExcelUtils;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.jzy3d.chart.Chart;
import org.jzy3d.chart.factories.AWTChartFactory;
import org.jzy3d.chart.factories.IChartFactory;
import org.jzy3d.colors.Color;
import org.jzy3d.maths.BoundingBox3d;
import org.jzy3d.maths.Coord3d;
import org.jzy3d.plot3d.primitives.Scatter;
import org.jzy3d.plot3d.rendering.canvas.Quality;

import java.io.File;
import java.io.FileInputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

@SuppressWarnings("FieldCanBeLocal")
public class Plot3D {

  private final int LENGTH = 400;

  public int counter = 0;
  private final Color lineColor = Color.BLUE;

  int x_max = 350;
  int y_max = 350;
  int marg = 20;

  private final XSSFSheet sheet;
  private final Coord3d[] coords;
  private final Chart chart;
  private final Scatter scatter;

  private final Color pointColor = Color.RED;
  private final Color[] pointColors = new Color[10];

  @SneakyThrows
  public Plot3D(String sheetName) {

    for (int i = 0; i < 10; i++) {
      pointColors[i] = pointColor.alphaSelf((float) ((i + 1) * 10) / 255);
    }

    File dataFile = new File("Databook.xlsx");
    XSSFWorkbook workbook = new XSSFWorkbook(new FileInputStream(dataFile));
    this.sheet = workbook.getSheet(sheetName);
    List<Float> x_values = ExcelUtils.getColumnValues(sheet, 6);
    List<Float> y_values = ExcelUtils.getColumnValues(sheet, 7);

    List<Float> z_values = new ArrayList<>();
    if (y_values.size() != x_values.size()) {
      throw new RuntimeException("X and Y values must be the same size of array");
    }
    for (int i = 0; i < x_values.size(); i++) {
      z_values.add((float) - Math.sqrt(Math.pow(LENGTH, 2) - Math.pow(x_values.get(i), 2) - Math.pow(y_values.get(i), 2)));
    }

    if (y_values.size() != z_values.size()) {
      throw new RuntimeException("X and Y and Z values must be the same size of array");
    }

    coords = new Coord3d[x_values.size()];
    for (int i = 0; i < coords.length; i++) {
      coords[i] = new Coord3d(x_values.get(i), y_values.get(i), z_values.get(i));
    }


    Quality q = Quality.Fastest();
    IChartFactory f = new AWTChartFactory();
    this.scatter = new Scatter(coords);
    this.scatter.setColor(pointColor);
    this.scatter.setWidth(3);
    this.chart = f.newChart(q);
    this.chart.getScene().add(scatter);

    BoundingBox3d bb = new BoundingBox3d();
    bb.setXmax(x_max);
    bb.setXmin(-x_max);
    bb.setYmax(y_max);
    bb.setYmin(-y_max);
    bb.setZmax(0);
    bb.setZmin(-LENGTH);
    this.chart.view().setBoundManual(bb);

    this.chart.getAxisLayout().setAxisLabelDistance(5);
    this.chart.getAxisLayout().setXAxisLabel("X axis");
    this.chart.getAxisLayout().setYAxisLabel("Y axis");
    this.chart.getAxisLayout().setZAxisLabel("Z axis");

    // this.chart.setAxeDisplayed(false);

    this.chart.open("3d Modeling");
    this.chart.addMouse();

    ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(1);

    scheduler.scheduleAtFixedRate(this::repaint,
        5000, 200, TimeUnit.MILLISECONDS);
  }

  private void repaint() {
    this.counter++;
    this.scatter.setColors(pointColors);
    this.scatter.setData(Arrays.stream(coords).toList().subList(counter, counter + 10));
    this.scatter.setWidth(3);
  }
}
