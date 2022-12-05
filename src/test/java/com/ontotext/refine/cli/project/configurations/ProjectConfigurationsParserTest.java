package com.ontotext.refine.cli.project.configurations;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.fasterxml.jackson.databind.JsonNode;
import com.ontotext.refine.cli.project.configurations.ProjectConfigurationsParser.Configuration;
import com.ontotext.refine.cli.utils.JsonUtils;
import java.io.File;
import java.net.URL;
import java.util.Optional;
import org.apache.commons.io.IOUtils;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

/**
 * Tests for {@link ProjectConfigurationsParser}.
 *
 * @author Antoniy Kunchev
 */
class ProjectConfigurationsParserTest {

  private static final String RESOURCES_DIR = "/project-configurations-variations/";

  @Test
  void isProjectConfiguration() throws Exception {
    JsonNode json = loadAsJson("csv-full-configuration.json");
    assertTrue(ProjectConfigurationsParser.isProjectConfiguration(json));

    json = loadAsJson("operations-as-plain-object.json");
    assertFalse(ProjectConfigurationsParser.isProjectConfiguration(json));
  }

  @ParameterizedTest
  @ValueSource(strings = {
      "csv-full-configuration.json",
      "csv-only-import-options.json",
      "only-operations.json"})
  void get(String filename) throws Exception {
    JsonNode json = loadAsJson(filename);

    assertTrue(ProjectConfigurationsParser.get(json, Configuration.IMPORT_OPTIONS).isPresent());
    assertTrue(ProjectConfigurationsParser.get(json, Configuration.OPERATIONS).isPresent());
  }

  @Test
  void get_optionsFromPlain() throws Exception {
    JsonNode json = loadAsJson("csv-import-options-as-plain-object.json");
    Optional<JsonNode> options =
        ProjectConfigurationsParser.get(json, Configuration.IMPORT_OPTIONS);
    assertTrue(options.isPresent());
    assertNotNull(options.get().findValue("projectName"));
  }

  @Test
  void get_operationsFromPlain() throws Exception {
    JsonNode json = loadAsJson("operations-as-plain-object.json");
    Optional<JsonNode> operations = ProjectConfigurationsParser.get(json, Configuration.OPERATIONS);
    assertTrue(operations.isPresent());
    assertNotNull(operations.get().findValue("op"));
  }

  private static JsonNode loadAsJson(String name) throws Exception {
    return JsonUtils.toJson(load(name));
  }

  private static File load(String name) throws Exception {
    URL url = IOUtils.resourceToURL(RESOURCES_DIR + name);
    return new File(url.toURI());
  }
}
