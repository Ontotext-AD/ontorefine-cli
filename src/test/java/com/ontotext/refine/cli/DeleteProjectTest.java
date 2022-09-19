package com.ontotext.refine.cli;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.ontotext.refine.cli.test.support.ExpectedSystemExit;
import com.ontotext.refine.cli.test.support.RefineResponder;
import com.ontotext.refine.cli.test.support.RefineResponder.HandlerContext;
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
 * Test for {@link DeleteProject}.
 *
 * @author Antoniy Kunchev
 */
class DeleteProjectTest extends BaseProcessTest {

  private static RefineResponder responder = new RefineResponder();
  private static boolean failCsrfRequest;
  private static boolean shouldRespondWithError;

  @BeforeAll
  static void beforeAll() throws IOException {
    failCsrfRequest = false;
    shouldRespondWithError = false;
    responder.start(mockResponses());
  }

  @AfterAll
  static void cleanup() {
    responder.stop();
  }

  @Override
  protected Class<?> command() {
    return DeleteProject.class;
  }

  @Test
  @ExpectedSystemExit(ExitCode.USAGE)
  void shouldExitWithErrorOnMissingFileArg() {
    try {
      commandExecutor().accept(args("-u " + responder.getUri()));
    } finally {
      assertTrue(assertMissingArgError().contains("PROJECT"));
    }
  }

  @Test
  @ExpectedSystemExit(ExitCode.SOFTWARE)
  void shouldFailToGetCrsfToken() {
    try {
      failCsrfRequest = true;

      commandExecutor().accept(args(PROJECT_ID, "-u " + responder.getUri()));
    } finally {
      failCsrfRequest = false;

      String error = consoleErrors().split(System.lineSeparator())[0];
      assertEquals(
          "Failed to retrieve CSRF token due to: Unexpected response :"
              + " HTTP/1.1 500 Internal Server Error",
          error);
    }
  }

  @Test
  @ExpectedSystemExit(ExitCode.SOFTWARE)
  void shouldReturnError() {
    try {
      shouldRespondWithError = true;

      commandExecutor().accept(args(PROJECT_ID, "-u " + responder.getUri()));
    } finally {
      shouldRespondWithError = false;

      String[] errorsArray = consoleErrors().split(System.lineSeparator());
      String lastLine = errorsArray[errorsArray.length - 1];
      assertEquals(
          "Failed to delete project with identifier '1812661014997' due to: Failed!",
          lastLine);
    }
  }

  @Test
  @ExpectedSystemExit(ExitCode.OK)
  void shouldPassSuccessfully() {
    try {
      commandExecutor().accept(args(PROJECT_ID, "-u " + responder.getUri()));
    } finally {
      assertEquals(
          "Successfully deleted project with identifier: 1812661014997",
          consoleOutput().trim());
    }
  }

  private static Map<String, HttpRequestHandler> mockResponses() {
    Map<String, HttpRequestHandler> responses = new HashMap<>(2);
    HandlerContext context = new HandlerContext().setFailCsrfRequest(() -> failCsrfRequest);
    responses.put("/orefine/command/core/get-csrf-token", RefineResponder.csrfToken(context));
    responses.put("/orefine/command/core/delete-project", deleteProjectHandler());
    return responses;
  }

  private static HttpRequestHandler deleteProjectHandler() {
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
