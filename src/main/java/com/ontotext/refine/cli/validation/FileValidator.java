package com.ontotext.refine.cli.validation;

import static java.lang.String.format;

import java.io.File;
import org.apache.commons.lang3.StringUtils;

/**
 * Contains convenient methods for files validation.
 *
 * @author Antoniy Kunchev
 */
public class FileValidator {

  private FileValidator() {
    throw new IllegalStateException("Utility class should not be instantiated.");
  }

  /**
   * Checks whether the given file exists or not.
   *
   * @param file to be checked
   * @return <code>true</code> if the file does not exists, <code>false</code> otherwise
   */
  public static boolean doesNotExists(File file) {
    return !exists(file, null);
  }

  /**
   * Checks whether the given file exists or not. The method will print error message, when the file
   * isn't available, before returning <code>true</code>.
   *
   * @param file to be checked
   * @param argName the name of the argument for the file, used for the error message
   * @return <code>true</code> if the file does not exists, <code>false</code> otherwise
   */
  public static boolean doesNotExists(File file, String argName) {
    return !exists(file, argName);
  }

  /**
   * Checks whether the given file exists or not. The method supports an option to print error
   * message, when the file isn't available, before returning <code>false</code>.
   *
   * @param file to be checked
   * @param argName the name of the argument for the file, used for the error message
   * @return <code>true</code> if the file exists, <code>false</code> otherwise
   */
  public static boolean exists(File file, String argName) {
    if (file != null && file.exists()) {
      return true;
    }

    if (StringUtils.isNotBlank(argName)) {
      String error = file == null
          ? format("The provided path for argument '%s' does not lead to existing file.", argName)
          : format(
              "File with name '%s', provided for argument '%s' is unavailable."
                  + " Please check if the path is correct: %s",
              file.getName(),
              argName,
              file.getAbsolutePath());

      System.err.println(error);
    }

    return false;
  }
}
