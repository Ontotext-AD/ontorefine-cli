package com.ontotext.refine.cli.validation;

import com.fasterxml.jackson.databind.JsonNode;
import com.ontotext.refine.cli.create.InputDataFormat;
import java.util.LinkedList;
import java.util.List;

/**
 * Contains logic for validation of the import options based on the input data format.
 *
 * @author Antoniy Kunchev
 */
public class ImportOptionsValidator {

  private ImportOptionsValidator() {
    throw new IllegalStateException("Utility class should not be instantiated.");
  }

  /**
   * Validates the import options for specific format and collects the errors.
   *
   * @param importOptions to validate
   * @param format of the imported data
   * @return list containing the validation errors. Empty list means valid import options
   */
  public static List<String> isValid(JsonNode importOptions, InputDataFormat format) {
    List<String> errors = new LinkedList<>();
    switch (format) {
      case JSON:
      case XML:
        containsRecordPath(importOptions, format, errors);
        return errors;
      default:
        return errors;
    }
  }

  private static void containsRecordPath(
      JsonNode importOptions, InputDataFormat format, List<String> errors) {
    if (importOptions == null || importOptions.isEmpty() || importOptions.isNull()) {
      errors.add(
          "Import options configurations are required for files of type: "
              + format.toString().toLowerCase());
      return;
    }

    boolean hasRecordPath = importOptions.isArray()
        ? hasRecordPath(importOptions.get(0))
        : hasRecordPath(importOptions);

    if (!hasRecordPath) {
      errors.add(
          "The import options should contain property 'recordPath' of type Array, containing set of"
              + " elements to be used for parsing of the dataset in tabular format.");
    }
  }

  private static boolean hasRecordPath(JsonNode importOptions) {
    JsonNode recordPath = importOptions.get("recordPath");
    return recordPath != null && recordPath.isArray() && !recordPath.isEmpty();
  }
}
