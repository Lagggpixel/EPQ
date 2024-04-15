package me.reid.dataAnalysis;

import lombok.SneakyThrows;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellType;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.io.File;
import java.io.FileInputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;

/**
 * @author Reid
 * @since January 24th, 2024
 * Method used to generate peak values of the location curve
 */
public class PeakGeneration {

  private static int lastNumber = 0;
  private static Phase phase;

  @SneakyThrows
  public static void generate(String sheetName, int inputColumn, int outputColumn) {
    File dataFile = new File("Databook.xlsx");

    XSSFWorkbook workbook = new XSSFWorkbook(new FileInputStream(dataFile));
    XSSFSheet sheet = workbook.getSheet(sheetName);

    boolean continueRunning = true;
    int dataRowInt = 3;

    // Check if the data start direction
    Double firstRowValue = getRowValue(sheet, 1, inputColumn);
    Double secondRowValue = getRowValue(sheet, 2, inputColumn);

    // Last row value registry
    double lastRowValue;
    // Cache for a list of maxima values
    List<Integer> data = new ArrayList<>();

    // Checks if the first second row is not null
    if (firstRowValue == null || secondRowValue == null) {
      throw new NullPointerException("The data must in the first and second row must be not null");
    }

    // Compare the first and second row to gain initial starting direction information
    // of increasing or decreasing function
    if (firstRowValue > secondRowValue) {
      phase = Phase.DOWN;
    } else if (firstRowValue < secondRowValue) {
      phase = Phase.UP;
    } else {
      throw new RuntimeException("The data must in the first and second row must be not the same value");
    }

    lastRowValue = secondRowValue;

    // Repeat until the end of the sheet
    while (continueRunning) {
      Double rowValue = getRowValue(sheet, dataRowInt, inputColumn);
      if (rowValue == null) {
        continueRunning = false;
        continue;
      }

      if ((phase == Phase.UP && rowValue < lastRowValue)) {
        // A maximum value is found
        data.add(dataRowInt-1);
        phase = Phase.DOWN;
      }
      else if ((phase == Phase.DOWN && rowValue > lastRowValue)) {
        // A minimum value is found
        // data.put(dataRowInt, rowValue);
        phase = Phase.UP;
      }

      lastRowValue = rowValue;

      dataRowInt++;
    }

    System.out.println(data);

    for (Integer datum : data) {
      Row row = sheet.getRow(datum);
      double fromCell = row.getCell(inputColumn).getNumericCellValue();
      Cell valueCell = row.createCell(outputColumn, CellType.NUMERIC);
      valueCell.setCellValue(fromCell);
    }

    OutputStream fileOut = new java.io.FileOutputStream(dataFile);
    workbook.write(fileOut);
    fileOut.close();
  }

  private static Double getRowValue(XSSFSheet sheet, int rowNumber, int columnNumber) {
    Row row = sheet.getRow(rowNumber);
    if (row == null) {
      return null;
    }
    Cell cell = row.getCell(columnNumber);
    if (cell == null) {
      return null;
    }
    if (cell.getCellType() != CellType.NUMERIC && cell.getCellType() != CellType.FORMULA) {
      return null;
    }

    return cell.getNumericCellValue();
  }

  enum Phase {
    UP, DOWN
  }
}
