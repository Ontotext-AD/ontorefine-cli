package com.ontotext.refine.cli;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.ontotext.refine.cli.RefineResponder.HandlerContext;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import org.apache.http.HttpStatus;
import org.apache.http.entity.BasicHttpEntity;
import org.apache.http.protocol.HttpRequestHandler;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import picocli.CommandLine.ExitCode;


/**
 * Test for {@link Export}.
 *
 * @author Antoniy Kunchev
 */
class ExportTest extends BaseProcessTest {

  private static RefineResponder responder = new RefineResponder();
  private static boolean shouldFailOperationsExtraction;

  @BeforeAll
  static void beforeAll() throws IOException {
    shouldFailOperationsExtraction = false;
    responder.start(mockResponses());
  }

  @AfterAll
  static void cleanup() {
    responder.stop();
  }

  @Override
  protected Class<?> command() {
    return Export.class;
  }

  @Test
  @ExpectSystemExit(ExitCode.USAGE)
  void shouldExitWithErrorOnMissingProjectArg() {
    try {
      commandExecutor().accept(args("-u " + responder.getUri()));
    } finally {
      assertTrue(assertMissingArgError().contains("PROJECT"));
    }
  }

  @Test
  @ExpectSystemExit(ExitCode.USAGE)
  void shouldExitWithErrorOnMissingFormatArg() {
    try {
      commandExecutor().accept(args(PROJECT_ID, "-u " + responder.getUri()));
    } finally {
      assertTrue(assertMissingArgError().contains("FORMAT"));
    }
  }

  @Test
  @ExpectSystemExit(ExitCode.USAGE)
  void shouldExitWithErrorOnInvalidFormatArg() {
    try {
      commandExecutor().accept(args(PROJECT_ID, "xml", "-u " + responder.getUri()));
    } finally {
      assertEquals(
          "The format: 'xml' is currently not supported.",
          consoleErrors().trim());
    }
  }

  @Test
  @ExpectSystemExit(ExitCode.SOFTWARE)
  void shouldFailDuringOperationsExtraction() {
    try {
      shouldFailOperationsExtraction = true;

      commandExecutor().accept(args(PROJECT_ID, "csv", "-u " + responder.getUri()));
    } finally {
      shouldFailOperationsExtraction = false;

      assertEquals(
          "Failed to export data for project: '1812661014997' due to: 'Connection reset'",
          consoleErrors().trim());
    }
  }

  @Test
  @ExpectSystemExit(ExitCode.OK)
  void shouldPassSuccessfully() throws IOException {
    try {
      commandExecutor().accept(args(PROJECT_ID, "csv", "-u " + responder.getUri()));
    } finally {
      String errors = consoleErrors();
      assertTrue(errors.isEmpty(), "Expected no errors but there were: " + errors);

      assertEquals("CSV-content", consoleOutput().trim());
    }
  }

  private static Map<String, HttpRequestHandler> mockResponses() {
    Map<String, HttpRequestHandler> responses = new HashMap<>();
    HandlerContext context = new HandlerContext();
    responses.put("/orefine/command/core/get-csrf-token", RefineResponder.csrfToken(context));
    responses.put("/orefine/command/core/export-rows", exportHandler());
    return responses;
  }

  private static HttpRequestHandler exportHandler() {
    return (request, response, context) -> {
      if (shouldFailOperationsExtraction) {
        throw new RuntimeException();
      } else {
        response.setStatusCode(HttpStatus.SC_OK);
        BasicHttpEntity entity = new BasicHttpEntity();
        entity.setContent(new ByteArrayInputStream("CSV-content".getBytes()));
        response.setEntity(entity);
      }
    };
  }
}
