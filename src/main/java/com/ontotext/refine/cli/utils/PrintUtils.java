package com.ontotext.refine.cli.utils;

import static java.lang.String.format;

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

  /**
   * Prints an information message in the console.
   *
   * @param value to be printed
   * @see System#out
   */
  public static void info(Object value) {
    System.out.println(value);
  }

  /**
   * Prints an information message in the console.
   *
   * @param template for the message
   * @param args to be replaced in the template
   * @see System#out
   * @see String#format(String, Object...)
   */
  public static void info(String template, Object... args) {
    info(format(template, args));
  }

  /**
   * Prints an error message in the console.
   *
   * @param template for the message
   * @param args to be replaced in the template
   * @see System#err
   * @see String#format(String, Object...)
   */
  public static void error(String template, Object... args) {
    System.err.println(format(template, args));
  }
}
