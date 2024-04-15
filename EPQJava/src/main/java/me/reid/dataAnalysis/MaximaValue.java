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
import java.util.*;

public class MaximaValue {

  @SneakyThrows
  public static void generate(String sheetName) {
    File dataFile = new File("Databook.xlsx");

    Map<Double, Double> maximaTimes = new HashMap<>();

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
      if (timeValueCell == null) {
        cont = false;
        continue;
      }
      if (timeValueCell.getCellType() == CellType.BLANK) {
        cont = false;
        continue;
      }
      double timeValue = row.getCell(0).getNumericCellValue();
      if (timeValue == 0) {
        cont = false;
      }
      Cell maximaCell = row.getCell(6);
      if (maximaCell != null && (maximaCell.getCellType() == CellType.FORMULA || maximaCell.getCellType() == CellType.NUMERIC)) {
        maximaTimes.put(timeValueCell.getNumericCellValue(), maximaCell.getNumericCellValue());
      }
      dataRowInt++;
    }


    List<Map.Entry<Double, Double>> entryList = new ArrayList<>(maximaTimes.entrySet());

    entryList.sort((entry1, entry2) -> {
      double size1 = Math.abs(entry1.getKey() - entry1.getValue());
      double size2 = Math.abs(entry2.getKey() - entry2.getValue());
      return Double.compare(size1, size2);
    });

    LinkedHashMap<Double, Double> sortedMap = new LinkedHashMap<>();
    for (Map.Entry<Double, Double> entry : entryList) {
      sortedMap.put(entry.getKey(), entry.getValue());
    }

    System.out.println(sortedMap);
    final int[] outputRowInt = {1};
    sortedMap.forEach((k, v) -> {
      Row row = sheet.getRow(outputRowInt[0]);

      Cell indexCell = row.createCell(12, CellType.NUMERIC);
      indexCell.setCellValue(k);
      Cell valueCell = row.createCell(13, CellType.NUMERIC);
      valueCell.setCellValue(v);

      outputRowInt[0]++;
    });

    OutputStream fileOut = new java.io.FileOutputStream(dataFile);
    workbook.write(fileOut);
    fileOut.close();
  }

}
