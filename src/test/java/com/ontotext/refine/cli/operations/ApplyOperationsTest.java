package com.ontotext.refine.cli.operations;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.ontotext.refine.cli.BaseProcessTest;
import com.ontotext.refine.cli.test.support.ExpectedSystemExit;
import com.ontotext.refine.cli.test.support.RefineResponder;
import com.ontotext.refine.cli.test.support.RefineResponder.HandlerContext;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;
import org.apache.http.HttpStatus;
import org.apache.http.entity.BasicHttpEntity;
import org.apache.http.protocol.HttpRequestHandler;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import picocli.CommandLine.ExitCode;

/**
 * Test for {@link ApplyOperations}.
 *
 * @author Antoniy Kunchev
 */
class ApplyOperationsTest extends BaseProcessTest {

  private static RefineResponder responder = new RefineResponder();
  private static boolean shouldRespondWithError;
  private static boolean shouldFailToGetCsrf;

  @BeforeAll
  static void beforeAll() throws IOException {
    shouldRespondWithError = false;
    shouldFailToGetCsrf = false;
    responder.start(mockResponses());
  }

  @AfterAll
  static void cleanup() {
    responder.stop();
  }

  @Override
  protected Class<?> command() {
    return ApplyOperations.class;
  }

  @Test
  @ExpectedSystemExit(ExitCode.USAGE)
  void shouldExitWithErrorOnMissingOperationsArg() {
    try {
      commandExecutor().accept(args("-u " + responder.getUri()));
    } finally {
      assertTrue(assertMissingArgError().contains("OPERATIONS"));
    }
  }

  @Test
  @ExpectedSystemExit(ExitCode.USAGE)
  void shouldExitWithErrorOnNullOperationsFileArg() {
    try {
      commandExecutor().accept(args("null", PROJECT_ID, "-u " + responder.getUri()));
    } finally {
      String[] errorsArray = consoleErrors().split(System.lineSeparator());
      String lastLine = errorsArray[0];
      assertTrue(
          lastLine.contains(
              "File with name 'null', provided for argument 'OPERATIONS' is unavailable."),
          "Expected the error message to contain information about"
          + " unavailable 'OPERATIONS' argument");
    }
  }

  @Test
  @ExpectedSystemExit(ExitCode.USAGE)
  void shouldExitWithErrorOnMissingProjectArg() {
    try {
      commandExecutor().accept(args("operations.json", "-u " + responder.getUri()));
    } finally {
      assertTrue(assertMissingArgError().contains("PROJECT"));
    }
  }

  @ParameterizedTest
  @ExpectedSystemExit(ExitCode.OK)
  @ValueSource(strings = {
      "operations.json",
      "project-configurations-variations/csv-full-configuration.json"})
  void shouldPassSuccessfully(String file) {
    try {
      URL resource = getClass().getClassLoader().getResource(file);

      String uriArg = "-u " + responder.getUri();
      commandExecutor().accept(args(resource.getPath(), PROJECT_ID, uriArg));
    } finally {
      String errors = consoleErrors();
      assertTrue(errors.isEmpty(), "Expected no errors but there were: " + errors);

      String[] outputArray = consoleOutput().split(System.lineSeparator());
      String lastLine = outputArray[outputArray.length - 1];

      assertEquals(
          "The transformations were successfully applied to project: 1812661014997",
          lastLine);
    }
  }

  @Test
  @ExpectedSystemExit(ExitCode.SOFTWARE)
  void shouldReturnError() {
    try {
      shouldRespondWithError = true;
      URL resource = getClass().getClassLoader().getResource("operations.json");

      String uriArg = "-u " + responder.getUri();
      commandExecutor().accept(args(resource.getPath(), PROJECT_ID, uriArg));
    } finally {
      shouldRespondWithError = false;

      String error = consoleErrors().split(System.lineSeparator())[0];
      assertEquals(
          "Failed to apply transformation to project '1812661014997' due to: Failed!", error);
    }
  }

  @Test
  @ExpectedSystemExit(ExitCode.SOFTWARE)
  void shouldFailToGetCrsfToken() {
    try {
      shouldFailToGetCsrf = true;
      URL resource = getClass().getClassLoader().getResource("operations.json");

      String uriArg = "-u " + responder.getUri();
      commandExecutor().accept(args(resource.getPath(), PROJECT_ID, uriArg));
    } finally {
      shouldFailToGetCsrf = false;

      String error = consoleErrors().split(System.lineSeparator())[0];
      assertEquals(
          "Failed to retrieve CSRF token due to: Unexpected response :"
              + " HTTP/1.1 500 Internal Server Error",
          error);
    }
  }

  private static Map<String, HttpRequestHandler> mockResponses() {
    Map<String, HttpRequestHandler> handlers = new HashMap<>(2);
    HandlerContext context = new HandlerContext().setFailCsrfRequest(() -> shouldFailToGetCsrf);
    handlers.put("/orefine/command/core/get-csrf-token", RefineResponder.csrfToken(context));
    handlers.put("/orefine/command/core/apply-operations", applyOperations());
    return handlers;
  }

  private static HttpRequestHandler applyOperations() {
    return (httpRequest, httpResponse, httpContext) -> {
      httpResponse.setStatusCode(HttpStatus.SC_OK);
      BasicHttpEntity entity = new BasicHttpEntity();
      if (shouldRespondWithError) {
        entity.setContent(new ByteArrayInputStream(
            "{ \"code\" : \"error\", \"message\" : \"Failed!\" }".getBytes()));
      } else {
        entity.setContent(new ByteArrayInputStream("{ \"code\" : \"ok\" }".getBytes()));
      }
      httpResponse.setEntity(entity);
    };
  }
}
