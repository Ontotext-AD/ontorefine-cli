package com.ontotext.refine.cli.create;

import static com.ontotext.refine.cli.project.configurations.ProjectConfigurationsParser.Configuration.IMPORT_OPTIONS;
import static com.ontotext.refine.cli.project.configurations.ProjectConfigurationsParser.get;

import com.fasterxml.jackson.databind.JsonNode;
import com.ontotext.refine.cli.validation.ImportOptionsValidator;
import com.ontotext.refine.client.exceptions.RefineException;
import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.Optional;
import java.util.function.Consumer;

/**
 * Contains common convenient logic for parsing, validation and setting of import options for the
 * project creation process.
 *
 * @author Antoniy Kunchev
 */
public class ImportOptionsProcessor {

  private ImportOptionsProcessor() {
    throw new IllegalStateException("Utility class should not be instantiated.");
  }

  /**
   * Parses and consumes the import options extracted from the given configuration file. For some
   * data types, the options will be validated and errors will be thrown in case of invalid
   * properties.
   *
   * @param configurations file containing the import option
   * @param format of the input data
   * @param importOptsConsumer for the import options
   * @throws IOException when there are issues with the parsing of the file or there are validation
   *                     errors for the options
   */
  public static void validateAndConsume(
      File configurations, InputDataFormat format, Consumer<JsonNode> importOptsConsumer)
      throws IOException {
    if (configurations == null) {
      throwOnJsonOrXml(format);
      return;
    }

    Optional<JsonNode> importOpts = get(configurations, IMPORT_OPTIONS);
    if (!importOpts.isPresent()) {
      throwOnJsonOrXml(format);
      return;
    }

    JsonNode importOptions = importOpts.get();
    List<String> errors = ImportOptionsValidator.isValid(importOptions, format);
    if (!errors.isEmpty()) {
      throw new RefineException(String.join("\n", errors));
    }

    importOptsConsumer.accept(importOptions);
  }

  private static void throwOnJsonOrXml(InputDataFormat format) throws RefineException {
    if (InputDataFormat.JSON.equals(format) || InputDataFormat.XML.equals(format)) {
      throw new RefineException(
          "There should be configuration with import options for file format: "
              + format.toString().toLowerCase());
    }
  }
}
