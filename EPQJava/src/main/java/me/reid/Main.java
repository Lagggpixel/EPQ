package me.reid;

import lombok.SneakyThrows;
import me.reid.dataAnalysis.MaximaValue;

/**
 * @author Reid
 * Created on January 24, 2024
 */
public class Main {

  @SneakyThrows
  public static void main(String[] args) {
    // new Plot2D("Trial 2");
    // new Plot3D("Trial 3");

    MaximaValue.generate("1_10cm_1");
    //PeakGeneration.generate("1_10cm_1", 5, 6);
  }
} 