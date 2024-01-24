package me.reid.utils;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellType;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

public class ExcelUtils {

  public static @NotNull List<Float> getColumnValues(XSSFSheet sheet, int column) {
    List<Float> temp = new ArrayList<>();
    for (Row r : sheet) {
      Cell c = r.getCell(column);
      if (c != null) {
        if (c.getCellType().equals(CellType.STRING)) {
          try {
            temp.add(Float.parseFloat(c.getStringCellValue()));
          } catch (NumberFormatException | NullPointerException e) {
            temp.add(0.0f);
          }
        } else if (c.getCellType().equals(CellType.NUMERIC) || c.getCellType().equals(CellType.FORMULA)) {
          temp.add((float) c.getNumericCellValue());
        }
      }
    }
    return temp;
  }
}
