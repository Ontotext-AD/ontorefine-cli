package com.ontotext.refine.cli.export.rdf;

import static com.ontotext.refine.cli.test.support.RefineResponder.exportHandler;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.fasterxml.jackson.databind.node.ObjectNode;
import com.ontotext.refine.cli.BaseProcessTest;
import com.ontotext.refine.cli.test.support.ExpectedSystemExit;
import com.ontotext.refine.cli.test.support.RefineResponder;
import com.ontotext.refine.client.util.JsonParser;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;
import org.apache.http.HttpStatus;
import org.apache.http.entity.BasicHttpEntity;
import org.apache.http.entity.ContentType;
import org.apache.http.protocol.HttpRequestHandler;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import picocli.CommandLine.ExitCode;

/**
 * Test for {@link ExportRdf}.
 *
 * @author Antoniy Kunchev
 */
class ExportRdfTest extends BaseProcessTest {

  private static RefineResponder responder = new RefineResponder();
  private static boolean shouldFailModelsExtraction;
  private static boolean shouldNotContainOverlayModels;
  private static boolean shouldNotContainMappingDef;

  @BeforeAll
  static void beforeAll() throws IOException {
    shouldFailModelsExtraction = false;
    responder.start(mockResponses());
  }

  @AfterAll
  static void cleanup() {
    responder.stop();
  }

  @Override
  protected Class<?> command() {
    return ExportRdf.class;
  }

  @Test
  @ExpectedSystemExit(ExitCode.USAGE)
  void shouldExitWithErrorOnMissingProjectArg() {
    try {
      commandExecutor().accept(args("-u " + responder.getUri()));
    } finally {
      assertTrue(assertMissingArgError().contains("PROJECT"));
    }
  }

  @Test
  @ExpectedSystemExit(ExitCode.SOFTWARE)
  void shouldFailDuringModelsExtraction() {
    try {
      shouldFailModelsExtraction = true;

      commandExecutor().accept(args(PROJECT_ID, "-u " + responder.getUri()));
    } finally {
      shouldFailModelsExtraction = false;

      String[] errorsArray = consoleErrors().split(System.lineSeparator());
      String lastLine = errorsArray[errorsArray.length - 1];
      assertEquals(
          "Failed to retrieve the models for project: '1812661014997' due to:"
              + " Unexpected response : HTTP/1.1 500 Internal Server Error",
          lastLine.trim());
    }
  }

  @Test
  @ExpectedSystemExit(ExitCode.SOFTWARE)
  void shouldFailDuringMappingFileReading() {
    try {
      // passing directory as file to cause FileNotFoundException
      URL directory = getClass().getClassLoader().getResource("./");

      String modelArg = "-m " + directory.getPath();
      String uriArg = "-u " + responder.getUri();

      commandExecutor().accept(args(PROJECT_ID, modelArg, uriArg));
    } finally {
      String[] errorsArray = consoleErrors().split(System.lineSeparator());
      String lastLine = errorsArray[errorsArray.length - 1];
      assertEquals("Failed to parse JSON file: 'test-classes'.", lastLine.trim());
    }
  }

  @Test
  @ExpectedSystemExit(ExitCode.OK)
  void shouldPassSuccessfully() throws IOException {
    try {
      commandExecutor().accept(args(PROJECT_ID, "-u " + responder.getUri()));
    } finally {
      String errors = consoleErrors();
      assertTrue(errors.isEmpty(), "Expected no errors but there were: " + errors);

      assertFalse(consoleOutput().isEmpty(), "The output should not be empty, but it was.");
    }
  }

  @Test
  @ExpectedSystemExit(ExitCode.OK)
  void shouldPassSuccessfullyProvidedMapping() throws IOException {
    try {
      URL resource = getClass().getClassLoader().getResource("rdf-mapping.json");

      String modelArg = "-m " + resource.getPath();
      String uriArg = "-u " + responder.getUri();

      commandExecutor().accept(args(PROJECT_ID, modelArg, uriArg));
    } finally {
      String errors = consoleErrors();
      assertTrue(errors.isEmpty(), "Expected no errors but there were: " + errors);

      assertFalse(consoleOutput().isEmpty(), "The output should not be empty, but it was.");
    }
  }

  @Test
  @ExpectedSystemExit(ExitCode.OK)
  void shouldPassSuccessfullyProvidedSparql() throws IOException {
    try {
      URL resource = getClass().getClassLoader().getResource("construct.sparql");

      String modelArg = "-q " + resource.getPath();
      String uriArg = "-u " + responder.getUri();

      commandExecutor().accept(args(PROJECT_ID, modelArg, uriArg));
    } finally {
      String errors = consoleErrors();
      assertTrue(errors.isEmpty(), "Expected no errors but there were: " + errors);

      assertFalse(consoleOutput().isEmpty(), "The output should not be empty, but it was.");
    }
  }

  private static Map<String, HttpRequestHandler> mockResponses() {
    Map<String, HttpRequestHandler> handlers = new HashMap<>(4);
    handlers.put("/orefine/command/core/get-models", getModels());
    handlers.put("/rest/rdf-mapper/rdf/ontorefine:" + PROJECT_ID, RefineResponder.exportHandler());
    handlers.put("/orefine/command/core/get-processes", RefineResponder.noProcesses());
    handlers.put("/repositories/ontorefine:" + PROJECT_ID, exportHandler());
    return handlers;
  }

  private static HttpRequestHandler getModels() {
    return (request, response, context) -> {
      if (shouldFailModelsExtraction) {
        response.setStatusCode(HttpStatus.SC_INTERNAL_SERVER_ERROR);
      } else {
        response.setStatusCode(HttpStatus.SC_OK);
        BasicHttpEntity entity = new BasicHttpEntity();
        InputStream models = loadResource("models.json");
        if (shouldNotContainOverlayModels) {
          ObjectNode node = (ObjectNode) JsonParser.JSON_PARSER.parseJson(models);
          node.remove("overlayModels");
          entity.setContent(new ByteArrayInputStream(node.toString().getBytes()));
        } else if (shouldNotContainMappingDef) {
          ObjectNode node = (ObjectNode) JsonParser.JSON_PARSER.parseJson(models);
          ((ObjectNode) node.findValue("overlayModels")).remove("mappingDefinition");
          entity.setContent(new ByteArrayInputStream(node.toString().getBytes()));
        } else {
          entity.setContent(models);
        }
        entity.setContentType(ContentType.APPLICATION_JSON.getMimeType());
        response.setEntity(entity);
      }
    };
  }

  private static InputStream loadResource(String resource) {
    return ExportRdfTest.class.getClassLoader().getResourceAsStream(resource);
  }
}
