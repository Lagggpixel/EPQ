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

public class MaximaValue {

  @SneakyThrows
  public static void generate(String sheetName) {
    File dataFile = new File("Databook.xlsx");

    List<Double> maximaTimes = new ArrayList<>();

    XSSFWorkbook workbook = new XSSFWorkbook(new FileInputStream(dataFile));
    XSSFSheet sheet = workbook.getSheet(sheetName);

    boolean cont = true;
    int dataRowInt = 1;

    while (cont) {
      Row row = sheet.getRow(dataRowInt);
      if (row == null) {
        cont = false;
        continue;
      }
      Cell timeValueCell = row.getCell(0);
      if (timeValueCell.getCellType() == CellType.BLANK) {
        cont = false;
        continue;
      }
      double timeValue = row.getCell(0).getNumericCellValue();
      if (timeValue == 0) {
        cont = false;
      }
      Cell maximaCell = row.getCell(9);
      if (maximaCell != null && maximaCell.getCellType() == CellType.FORMULA) {
        maximaTimes.add(timeValueCell.getNumericCellValue());
      }
      dataRowInt++;
    }

    System.out.println(maximaTimes);
    int outputRowInt = 1;
    for (Double maximaTime : maximaTimes) {
      Row row = sheet.getRow(outputRowInt);

      Cell indexCell = row.createCell(34, CellType.NUMERIC);
      indexCell.setCellValue(outputRowInt);
      Cell valueCell = row.createCell(35, CellType.NUMERIC);
      valueCell.setCellValue(maximaTime);

      outputRowInt++;
    }

    OutputStream fileOut = new java.io.FileOutputStream(dataFile);
    workbook.write(fileOut);
    fileOut.close();
  }

}
