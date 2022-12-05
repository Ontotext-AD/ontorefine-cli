package com.ontotext.refine.cli.extract;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.ontotext.refine.cli.BaseProcessTest;
import com.ontotext.refine.cli.test.support.ExpectedSystemExit;
import com.ontotext.refine.cli.test.support.RefineResponder;
import java.io.IOException;
import java.io.InputStream;
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
 * Test for {@link ExtractConfigurations}.
 *
 * @author Antoniy Kunchev
 */
class ExtractConfigurationsTest extends BaseProcessTest {

  private static RefineResponder responder = new RefineResponder();
  private static boolean shouldFailExportRequest;
  private static boolean shouldUseProjectConfigurationJson;

  @BeforeAll
  static void beforeAll() throws IOException {
    shouldFailExportRequest = false;
    responder.start(mockResponses());
  }

  @AfterAll
  static void cleanup() {
    responder.stop();
  }

  @Override
  protected Class<?> command() {
    return ExtractConfigurations.class;
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
  void shouldFailOnRefineRequestFailure() {
    try {
      shouldFailExportRequest = true;

      commandExecutor().accept(args(PROJECT_ID, "-u " + responder.getUri()));
    } finally {
      shouldFailExportRequest = false;

      assertEquals(
          "Failed to retrieve the operations for project: '1812661014997' due to:"
              + " Unexpected response : HTTP/1.1 500 Internal Server Error",
          consoleErrors().trim());
    }
  }

  @Test
  @ExpectedSystemExit(ExitCode.OK)
  void shouldPassSuccessfully_defaultMode() {
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
  void shouldPassSuccessfully_operationsMode() {
    try {
      commandExecutor().accept(args(PROJECT_ID, "-m operations", "-u " + responder.getUri()));
    } finally {
      String errors = consoleErrors();
      assertTrue(errors.isEmpty(), "Expected no errors but there were: " + errors);

      assertFalse(consoleOutput().isEmpty(), "The output should not be empty, but it was.");
    }
  }

  @Test
  @ExpectedSystemExit(ExitCode.OK)
  void shouldPassSuccessfully_importOptionsMode() {
    try {
      shouldUseProjectConfigurationJson = true;
      commandExecutor().accept(args(PROJECT_ID, "-m import-options", "-u " + responder.getUri()));
    } finally {
      shouldUseProjectConfigurationJson = false;

      String errors = consoleErrors();
      assertTrue(errors.isEmpty(), "Expected no errors but there were: " + errors);

      assertFalse(consoleOutput().isEmpty(), "The output should not be empty, but it was.");
    }
  }

  @Test
  @ExpectedSystemExit(ExitCode.OK)
  void shouldPassSuccessfully_fullMode() {
    try {
      shouldUseProjectConfigurationJson = true;
      commandExecutor().accept(args(PROJECT_ID, "-m full", "-u " + responder.getUri()));
    } finally {
      shouldUseProjectConfigurationJson = false;

      String errors = consoleErrors();
      assertTrue(errors.isEmpty(), "Expected no errors but there were: " + errors);

      assertFalse(consoleOutput().isEmpty(), "The output should not be empty, but it was.");
    }
  }

  private static Map<String, HttpRequestHandler> mockResponses() {
    Map<String, HttpRequestHandler> responses = new HashMap<>(2);
    responses.put("/orefine/command/core/get-operations", exportOperationsHandler());
    responses.put("/orefine/command/project-configurations/export", exportOperationsHandler());
    return responses;
  }

  private static HttpRequestHandler exportOperationsHandler() {
    return (request, response, context) -> {
      if (shouldFailExportRequest) {
        response.setStatusCode(HttpStatus.SC_INTERNAL_SERVER_ERROR);
      } else {
        response.setStatusCode(HttpStatus.SC_OK);
        response.setEntity(getModeBasedEntity());
      }
    };
  }

  private static BasicHttpEntity getModeBasedEntity() {
    BasicHttpEntity entity = new BasicHttpEntity();
    InputStream stream = null;
    if (shouldUseProjectConfigurationJson) {
      stream = loadFile("project-configurations-variations/csv-full-configuration.json");
    } else {
      stream = loadFile("operations.json");
    }
    entity.setContent(stream);
    entity.setContentType(ContentType.APPLICATION_JSON.getMimeType());
    return entity;
  }

  private static InputStream loadFile(String filename) {
    return ExtractConfigurationsTest.class.getClassLoader().getResourceAsStream(filename);
  }
}
