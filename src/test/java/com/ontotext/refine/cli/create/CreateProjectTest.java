package com.ontotext.refine.cli.create;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.ontotext.refine.cli.BaseProcessTest;
import com.ontotext.refine.cli.test.support.ExpectedSystemExit;
import com.ontotext.refine.cli.test.support.RefineResponder;
import com.ontotext.refine.cli.test.support.RefineResponder.HandlerContext;
import java.io.IOException;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;
import org.apache.http.HttpStatus;
import org.apache.http.protocol.HttpRequestHandler;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import picocli.CommandLine.ExitCode;

/**
 * Test for the {@link CreateProject}.
 *
 * @author Antoniy Kunchev
 */
class CreateProjectTest extends BaseProcessTest {

  private static RefineResponder responder = new RefineResponder();
  private static boolean failCsrfRequest;

  @BeforeAll
  static void beforeAll() throws IOException {
    failCsrfRequest = false;
    responder.start(mockResponses());
  }

  @AfterAll
  static void cleanup() {
    responder.stop();
  }

  @Override
  protected Class<?> command() {
    return CreateProject.class;
  }

  @Test
  @ExpectedSystemExit(ExitCode.USAGE)
  void shouldExitWithErrorOnMissingFileArg() {
    try {
      commandExecutor().accept(args("-u " + responder.getUri()));
    } finally {
      assertTrue(assertMissingArgError().contains("FILE"));
    }
  }

  @Test
  @ExpectedSystemExit(ExitCode.USAGE)
  void shouldExitWithErrorOnNullFileArg() {
    try {
      commandExecutor().accept(args("null", "-u " + responder.getUri()));
    } finally {
      String[] errorsArray = consoleErrors().split(System.lineSeparator());
      String lastLine = errorsArray[0];
      assertTrue(
          lastLine.contains("File with name 'null', provided for argument 'FILE' is unavailable."),
          "Expected the error message to contain information about unavailable 'FILE' argument");
    }
  }

  @Test
  @ExpectedSystemExit(ExitCode.USAGE)
  void shouldFailForUnsupportedFormat() {
    try {
      URL resource = getClass().getClassLoader().getResource("Netherlands_restaurants.csv");

      String uriArg = "-u " + responder.getUri();
      commandExecutor().accept(args(resource.getPath(), "-n Restaurants", "-f tsv", uriArg));
    } finally {
      String[] errorsArray = consoleErrors().split(System.lineSeparator());
      String lastLine = errorsArray[0];
      assertEquals(
          "Invalid value for option '--format': expected one of [CSV] (case-insensitive)"
              + " but was 'tsv'",
          lastLine);
    }
  }

  @Test
  @ExpectedSystemExit(ExitCode.SOFTWARE)
  void shouldFailToGetCrsfToken() {
    try {
      failCsrfRequest = true;
      URL resource = getClass().getClassLoader().getResource("Netherlands_restaurants.csv");

      String uriArg = "-u " + responder.getUri();
      commandExecutor().accept(args(resource.getPath(), "-n Restaurants", uriArg));
    } finally {
      failCsrfRequest = false;

      String[] errorsArray = consoleErrors().split(System.lineSeparator());
      String lastLine = errorsArray[errorsArray.length - 1];
      assertEquals(
          "Failed to retrieve CSRF token due to: Unexpected response :"
              + " HTTP/1.1 500 Internal Server Error",
          lastLine);
    }
  }

  @Test
  @ExpectedSystemExit(ExitCode.OK)
  void shouldPassSuccessfully_csv() {
    try {
      URL resource = getClass().getClassLoader().getResource("Netherlands_restaurants.csv");

      String uriArg = "-u " + responder.getUri();
      commandExecutor().accept(args(resource.getPath(), "-n Restaurants", "-f csv", uriArg));
    } finally {
      String errors = consoleErrors();
      assertTrue(errors.isEmpty(), "Expected no errors but there were: " + errors);

      assertEquals(
          "Successfully created project with identifier: 1812661014997",
          consoleOutput().trim());
    }
  }

  @Disabled("Not yet introduced.")
  @Test
  @ExpectedSystemExit(ExitCode.OK)
  void shouldPassSuccessfully_xml() {
    try {
      URL resource = getClass().getClassLoader().getResource("test-example.xml");
      URL importOptsJson =
          getClass().getClassLoader().getResource("xml-project-configurations.json");

      String uriArg = "-u " + responder.getUri();
      String importOptsArg = "-c " + importOptsJson.getPath();
      commandExecutor()
          .accept(args(resource.getPath(), "-n test-xml", "-f xml", importOptsArg, uriArg));
    } finally {
      String errors = consoleErrors();
      assertTrue(errors.isEmpty(), "Expected no errors but there were: " + errors);

      assertEquals(
          "Successfully created project with identifier: 1812661014997",
          consoleOutput().trim());
    }
  }

  private static Map<String, HttpRequestHandler> mockResponses() {
    Map<String, HttpRequestHandler> responses = new HashMap<>(2);
    HandlerContext context = new HandlerContext().setFailCsrfRequest(() -> failCsrfRequest);
    responses.put("/orefine/command/core/get-csrf-token", RefineResponder.csrfToken(context));
    responses.put("/orefine/command/core/create-project-from-upload", createProjectHandler());
    return responses;
  }

  private static HttpRequestHandler createProjectHandler() {
    return (httpRequest, httpResponse, httpContext) -> {
      httpResponse.setStatusCode(HttpStatus.SC_MOVED_TEMPORARILY);
      httpResponse.addHeader("Location", "?projectId=" + PROJECT_ID);
    };
  }
}
