package me.reid.plots;

import lombok.SneakyThrows;
import me.reid.utils.ExcelUtils;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.awt.*;
import java.io.File;
import java.io.FileInputStream;
import java.util.ArrayList;
import java.util.List;

public class Plot3D {

  public static int counter = 10;

  private final Color pointColor = Color.RED;
  private final Color lineColor = Color.BLUE;

  int x_max = 350;
  int y_max = 350;
  int marg = 20;

  private static XSSFSheet sheet;

  private final List<Float> x_values;
  private final List<Float> y_values;
  private List<Float> currentPointsX = new ArrayList<>();
  private List<Float> currentPointsY = new ArrayList<>();

  @SneakyThrows
  public Plot3D(String sheetName) {
    File dataFile = new File("Databook.xlsx");
    XSSFWorkbook workbook = new XSSFWorkbook(new FileInputStream(dataFile));
    sheet = workbook.getSheet(sheetName);
    x_values = ExcelUtils.getColumnValues(sheet,6);
    y_values = ExcelUtils.getColumnValues(sheet,7);
  }

}
