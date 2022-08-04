package com.ontotext.refine.cli.utils;

import java.io.IOException;
import java.io.InputStream;
import org.apache.commons.io.IOUtils;

/**
 * Utility containing continent logic for console printing.
 *
 * @author Antoniy Kunchev
 */
public class PrintUtils {

  private PrintUtils() {
    throw new IllegalStateException("Utility class should not be instantiated.");
  }

  /**
   * Prints the given {@link InputStream} to the console.<br>
   * The method handles printing of streams larger then 2GB.<br>
   * Also the stream will be consumed and closed, regardless of the result of the operation.
   *
   * @param is stream to print out
   * @throws IOException when error occurs during the printing process
   * @see IOUtils#copy(java.io.InputStream, java.io.OutputStream)
   */
  public static void print(InputStream is) throws IOException {
    try (InputStream toPrint = is) {
      IOUtils.copy(toPrint, System.out);
    }
  }
}
