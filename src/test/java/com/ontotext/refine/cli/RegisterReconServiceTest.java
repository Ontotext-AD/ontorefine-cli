package com.ontotext.refine.cli;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.ontotext.refine.cli.test.support.ExpectedSystemExit;
import com.ontotext.refine.cli.test.support.RefineResponder;
import com.ontotext.refine.cli.test.support.RefineResponder.HandlerContext;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
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
 * Test for {@link RegisterReconService}.
 *
 * @author Antoniy Kunchev
 */
class RegisterReconServiceTest extends BaseProcessTest {

  private static RefineResponder responder = new RefineResponder();
  private static String reconServiceUri;
  private static boolean setPreferenceShouldFail;
  private static boolean shouldThrowErrorDuringExecution;

  @BeforeAll
  static void beforeAll() throws IOException {
    setPreferenceShouldFail = false;
    shouldThrowErrorDuringExecution = false;
    responder.start(responses());
    reconServiceUri = responder.getUri() + "/reconcile";
  }

  @AfterAll
  static void cleanup() {
    responder.stop();
  }

  @Override
  protected Class<RegisterReconService> command() {
    return RegisterReconService.class;
  }

  @Test
  @ExpectedSystemExit(ExitCode.USAGE)
  void shouldExitWithErrorOnMissingServiceArg() {
    try {
      commandExecutor().accept(args("-u " + responder.getUri()));
    } finally {
      assertTrue(assertMissingArgError().contains("SERVICE"));
    }
  }

  @Test
  @ExpectedSystemExit(ExitCode.SOFTWARE)
  void shouldFailServiceRegistration() {
    try {
      setPreferenceShouldFail = true;

      commandExecutor().accept(args(reconServiceUri, "-u " + responder.getUri()));
    } finally {
      setPreferenceShouldFail = false;

      String expected = String.format(
          "Failed to register additional reconciliation service: '%s' due to: %s",
          reconServiceUri,
          "Recon service error");

      assertEquals(expected, consoleErrors().trim());
    }
  }

  @Test
  @ExpectedSystemExit(ExitCode.SOFTWARE)
  void shouldErrorDuringExecution() {
    try {
      shouldThrowErrorDuringExecution = true;

      commandExecutor().accept(args(reconServiceUri, "-u " + responder.getUri()));
    } finally {
      shouldThrowErrorDuringExecution = false;

      assertEquals(
          "Failed to retrieve CSRF token due to: Unexpected response :"
              + " HTTP/1.1 500 Internal Server Error",
          consoleErrors().trim());
    }
  }

  @Test
  @ExpectedSystemExit(ExitCode.OK)
  void shouldPassSuccessfully() throws IOException {
    try {
      commandExecutor().accept(args(reconServiceUri, "-u " + responder.getUri()));
    } finally {
      String errors = consoleErrors();
      assertTrue(errors.isEmpty(), "Expected no errors but there were: " + errors);

      assertEquals(
          "Successfully registered additional reconciliation service: " + reconServiceUri,
          consoleOutput().trim());
    }
  }

  private static Map<String, HttpRequestHandler> responses() {
    Map<String, HttpRequestHandler> responses = new HashMap<>();
    HandlerContext context =
        new HandlerContext().setFailCsrfRequest(() -> shouldThrowErrorDuringExecution);
    responses.put("/orefine/command/core/get-csrf-token", RefineResponder.csrfToken(context));
    responses.put("/orefine/command/core/get-preference", getPreference());
    responses.put("/reconcile", reconcileServiceResponse());
    responses.put("/orefine/command/core/set-preference", setPreference());
    return responses;
  }

  private static HttpRequestHandler getPreference() {
    return (request, response, context) -> {
      response.setStatusCode(HttpStatus.SC_OK);
      BasicHttpEntity entity = new BasicHttpEntity();
      entity.setContent(load("get-preference-recon-services-response.json"));
      response.setEntity(entity);
    };
  }

  private static HttpRequestHandler reconcileServiceResponse() {
    return (request, response, context) -> {
      response.setStatusCode(HttpStatus.SC_OK);
      BasicHttpEntity entity = new BasicHttpEntity();
      entity.setContent(load("recon-service-response.json"));
      response.setEntity(entity);
    };
  }

  private static HttpRequestHandler setPreference() {
    return (request, response, context) -> {
      response.setStatusCode(HttpStatus.SC_OK);
      BasicHttpEntity entity = new BasicHttpEntity();
      if (setPreferenceShouldFail) {
        entity.setContent(new ByteArrayInputStream(
            "{\"code\":\"error\", \"message\":\"Recon service error\"}".getBytes()));
      } else {
        entity.setContent(new ByteArrayInputStream("{\"code\":\"ok\"}".getBytes()));
      }
      response.setEntity(entity);
    };
  }

  private static InputStream load(String resource) {
    return RegisterReconServiceTest.class.getClassLoader().getResourceAsStream(resource);
  }
}
