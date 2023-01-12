package com.ontotext.refine.cli.project.configurations;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.JsonNodeFactory;
import com.ontotext.refine.cli.utils.JsonUtils;
import java.io.File;
import java.io.IOException;
import java.util.Optional;

/**
 * Contains logic for parsing and retrieving of project configurations.
 *
 * @author Antoniy Kunchev
 */
public class ProjectConfigurationsParser {

  private ProjectConfigurationsParser() {
    throw new IllegalStateException("Utility class should not be instantiated.");
  }

  /**
   * Checks whether the provided node is a project configuration or not.
   *
   * @param node to check
   * @return <code>true</code> if the node corresponds to the project configuration structure,
   *         <code>false</code> otherwise
   */
  public static boolean isProjectConfiguration(JsonNode node) {
    return node.has("importOptions") || node.has("operations") || node.has("aliases");
  }

  /**
   * Retrieves specific part of the project configurations JSON. When the format of the JSON is not
   * strict, the logic will try to detect and return the desired configuration based on the fields
   * that the input JSON contains.
   *
   * @param file containing the project configurations
   * @param configuration that should be retrieved
   * @return {@link Optional} containing the found configuration or {@link Optional#empty()} when
   *         the given configuration wasn't found in the JSON
   * @throws IOException when error occurs during the file parsing
   */
  public static Optional<JsonNode> get(File file, Configuration configuration) throws IOException {
    return get(JsonUtils.toJson(file), configuration);
  }

  /**
   * Retrieves specific part of the project configurations JSON. When the format of the JSON is not
   * strict, the logic will try to detect and return the desired configuration based on the fields
   * that the input JSON contains.
   *
   * @param json containing the project configurations
   * @param configuration that should be retrieved
   * @return {@link Optional} containing the found configuration or {@link Optional#empty()} when
   *         the given configuration wasn't found in the JSON
   */
  public static Optional<JsonNode> get(JsonNode json, Configuration configuration) {
    JsonNode config = json.get(configuration.getValue());
    if (config == null || config.isNull()) {
      return ConfigurationDetector.tryToDetect(json, configuration);
    }
    return Optional.of(config);
  }

  /**
   * Provides field names of the nodes that the project configuration JSON has.
   *
   * @author Antoniy Kunchev
   */
  public enum Configuration {

    /**
     * Project import options configurations.
     */
    IMPORT_OPTIONS("importOptions"),

    /**
     * Operations history configuration.
     */
    OPERATIONS("operations"),

    /**
     * Project aliases.
     */
    ALIASES("aliases");

    private final String value;

    private Configuration(String value) {
      this.value = value;
    }

    public String getValue() {
      return value;
    }
  }

  /**
   * Detects the correct configuration node based on the fields that it contains.
   *
   * @author Antoniy Kunchev
   */
  private static class ConfigurationDetector {

    private ConfigurationDetector() {
      throw new IllegalStateException("Utility class should not be instantiated.");
    }

    private static Optional<JsonNode> tryToDetect(JsonNode json, Configuration configuration) {
      if (Configuration.IMPORT_OPTIONS.equals(configuration)) {
        return detectImportOptions(json);
      }

      if (Configuration.OPERATIONS.equals(configuration)) {
        return detectOperations(json);
      }

      if (Configuration.ALIASES.equals(configuration)) {
        return Optional.ofNullable(json.get(Configuration.ALIASES.toString()));
      }

      return Optional.empty();
    }

    /**
     * Tries to find an object that has standard fields for the import options.<br>
     * The method handles simple cases for array and object JSON nodes.
     */
    private static Optional<JsonNode> detectImportOptions(JsonNode json) {

      // if it is an array with at least one element
      if (json.isArray() && json.has(0)) {
        JsonNode config = json.get(0);
        if (hasImportOptionsFields(config)) {
          return Optional.of(json);
        }
      }

      // if it is directly the object with import options
      if (hasImportOptionsFields(json)) {
        // wrap it in array before returning
        return Optional.of(JsonNodeFactory.instance.arrayNode().add(json));
      }

      return Optional.empty();
    }

    private static boolean hasImportOptionsFields(JsonNode config) {
      return config.has("projectName") || config.has("projectTags") || config.has("fileSource");
    }

    private static Optional<JsonNode> detectOperations(JsonNode json) {
      if (containsOperations(json) && json.isArray()) {
        return Optional.of(json);
      }

      // when the array is wrapped in an object, try to unwrap it
      if (json.elements().hasNext()) {
        JsonNode node = json.elements().next();
        return detectOperations(node);
      }

      return Optional.empty();
    }

    private static boolean containsOperations(JsonNode json) {
      return !json.findValues("operation").isEmpty() || !json.findValues("op").isEmpty();
    }
  }
}
