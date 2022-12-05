package com.ontotext.refine.cli.operations;

import static com.ontotext.refine.cli.project.configurations.ProjectConfigurationsParser.get;
import static com.ontotext.refine.cli.project.configurations.ProjectConfigurationsParser.isProjectConfiguration;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.JsonNodeFactory;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.ontotext.refine.cli.project.configurations.ProjectConfigurationsParser.Configuration;
import com.ontotext.refine.cli.utils.JsonUtils;
import java.io.File;
import java.io.IOException;

/**
 * Contains convenient methods used in the commands related to the operations history.
 *
 * @author Antoniy Kunchev
 */
public class OperationsUtil {

  private OperationsUtil() {
    throw new IllegalStateException("Utility class should not be instantiated.");
  }

  /**
   * Retrieves the operations history from the input file, if there are any. Otherwise the result
   * will be empty representation of {@link ObjectNode}.
   *
   * @param file containing operations history
   * @return string representation of the operations history JSON or empty object if the operations
   *         can't be resolved
   * @throws IOException when an error occurs during the parsing of the file
   */
  public static String getOperations(File file) throws IOException {
    JsonNode json = JsonUtils.toJson(file);
    if (!isProjectConfiguration(json)) {
      return json.toString();
    }

    return get(json, Configuration.OPERATIONS)
        .orElseGet(JsonNodeFactory.instance::objectNode)
        .toString();
  }
}
