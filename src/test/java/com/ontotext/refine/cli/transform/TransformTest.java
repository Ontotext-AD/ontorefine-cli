package com.ontotext.refine.cli.transform;

import static com.ontotext.refine.cli.test.support.RefineResponder.createProjectHandler;
import static com.ontotext.refine.cli.test.support.RefineResponder.csrfToken;
import static com.ontotext.refine.cli.test.support.RefineResponder.exportHandler;
import static com.ontotext.refine.cli.test.support.RefineResponder.noProcesses;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
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
import picocli.CommandLine.ExitCode;

/**
 * Test for {@link Transform}.
 *
 * @author Antoniy Kunchev
 */
class TransformTest extends BaseProcessTest {

  private static RefineResponder responder = new RefineResponder();
  private static boolean failCsrfRequest;
  private static boolean failApplyOperations;

  @BeforeAll
  static void beforeAll() throws IOException {
    failCsrfRequest = false;
    failApplyOperations = false;
    responder.start(mockResponses());
  }

  @AfterAll
  static void cleanup() {
    responder.stop();
  }

  @Override
  protected Class<?> command() {
    return Transform.class;
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
  void shouldExitWithErrorOnMissingOperationsAndSparqlArgs() {
    try {
      URL resource = getClass().getClassLoader().getResource("Netherlands_restaurants.csv");

      String uriArg = "-u " + responder.getUri();
      commandExecutor().accept(args(resource.getPath(), uriArg));
    } finally {
      String[] errorsArray = consoleErrors().split(System.lineSeparator());
      String lastLine = errorsArray[0];
      assertEquals(
          "Expected at least one of '--operations' or '--sparql' parameters to be available.",
          lastLine);
    }
  }

  @Test
  @ExpectedSystemExit(ExitCode.SOFTWARE)
  void shouldFailToGetCrsfToken() {
    try {
      failCsrfRequest = true;
      URL dataset = getClass().getClassLoader().getResource("Netherlands_restaurants.csv");
      URL operations = getClass().getClassLoader().getResource("operations.json");

      String uriArg = "-u " + responder.getUri();
      String operationsArg = "-o " + operations.getPath();

      commandExecutor().accept(args(dataset.getPath(), operationsArg, uriArg));
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
  @ExpectedSystemExit(ExitCode.SOFTWARE)
  void shouldFailOnApplyOperations() {
    try {
      failApplyOperations = true;
      URL dataset = getClass().getClassLoader().getResource("Netherlands_restaurants.csv");
      URL operations = getClass().getClassLoader().getResource("operations.json");

      String uriArg = "-u " + responder.getUri();
      String operationsArg = "-o " + operations.getPath();

      commandExecutor().accept(args(dataset.getPath(), operationsArg, "--no-clean", uriArg));
    } finally {
      failApplyOperations = false;

      String[] errorsArray = consoleErrors().split(System.lineSeparator());
      String lastLine = errorsArray[errorsArray.length - 1];
      assertEquals(
          "Failed to apply transformation to dataset 'Netherlands_restaurants.csv' due to: Failed!",
          lastLine);
    }
  }

  @Test
  @ExpectedSystemExit(ExitCode.SOFTWARE)
  void shouldFailGeneralError() {
    try {
      URL dataset = getClass().getClassLoader().getResource("Netherlands_restaurants.csv");

      String uriArg = "-u " + responder.getUri();
      String operationsArg = "-o nonexistent-file";

      commandExecutor().accept(args(dataset.getPath(), operationsArg, uriArg));
    } finally {

      String[] errorsArray = consoleErrors().split(System.lineSeparator());
      String lastLine = errorsArray[errorsArray.length - 1];
      assertEquals(
          "Unexpected error occurred during transformation of the dataset"
              + " 'Netherlands_restaurants.csv'. Details: nonexistent-file"
              + " (No such file or directory)",
          lastLine);
    }
  }

  @Test
  @ExpectedSystemExit(ExitCode.OK)
  void shouldTransformSuccessfullyUsingMapping() {
    try {
      URL dataset = getClass().getClassLoader().getResource("Netherlands_restaurants.csv");
      URL operations = getClass().getClassLoader().getResource("operations.json");

      String uriArg = "-u " + responder.getUri();
      String operationsArg = "-o " + operations.getPath();

      commandExecutor().accept(args(dataset.getPath(), operationsArg, uriArg));
    } finally {
      String errors = consoleErrors();
      assertTrue(errors.isEmpty(), "Expected no errors but there were: " + errors);

      assertFalse(consoleOutput().isEmpty(), "The output should not be empty, but it was.");
    }
  }

  @Test
  @ExpectedSystemExit(ExitCode.OK)
  void shouldTransformSuccessfullyUsingSparql() {
    try {
      URL dataset = getClass().getClassLoader().getResource("Netherlands_restaurants.csv");
      URL sparql = getClass().getClassLoader().getResource("construct.sparql");

      String uriArg = "-u " + responder.getUri();
      String sparqlArg = "-q " + sparql.getPath();

      commandExecutor().accept(args(dataset.getPath(), sparqlArg, uriArg));
    } finally {
      String errors = consoleErrors();
      assertTrue(errors.isEmpty(), "Expected no errors but there were: " + errors);

      assertFalse(consoleOutput().isEmpty(), "The output should not be empty, but it was.");
    }
  }

  // TODO: prepare TC and remove all of this
  private static Map<String, HttpRequestHandler> mockResponses() {
    Map<String, HttpRequestHandler> responses = new HashMap<>(7);
    HandlerContext context = new HandlerContext().setFailCsrfRequest(() -> failCsrfRequest);
    responses.put("/orefine/command/core/get-csrf-token", csrfToken(context));
    responses.put("/orefine/command/core/create-project-from-upload",
        createProjectHandler(PROJECT_ID));
    responses.put("/orefine/command/core/apply-operations", applyOperations());
    responses.put("/rest/rdf-mapper/rdf/ontorefine:" + PROJECT_ID, exportHandler());
    responses.put("/orefine/command/core/get-processes", noProcesses());
    responses.put("/repositories/ontorefine:" + PROJECT_ID, exportHandler());
    responses.put("/orefine/command/core/delete-project", deleteProjectHandler());
    return responses;
  }

  private static HttpRequestHandler applyOperations() {
    return (httpRequest, httpResponse, httpContext) -> {
      httpResponse.setStatusCode(HttpStatus.SC_OK);
      BasicHttpEntity entity = new BasicHttpEntity();
      if (failApplyOperations) {
        entity.setContent(new ByteArrayInputStream(
            "{ \"code\" : \"error\", \"message\" : \"Failed!\" }".getBytes()));
      } else {
        entity.setContent(new ByteArrayInputStream("{ \"code\" : \"ok\" }".getBytes()));
      }
      httpResponse.setEntity(entity);
    };
  }

  private static HttpRequestHandler deleteProjectHandler() {
    return (httpRequest, httpResponse, httpContext) -> {
      httpResponse.setStatusCode(HttpStatus.SC_OK);
      BasicHttpEntity entity = new BasicHttpEntity();
      entity.setContent(new ByteArrayInputStream("{ \"code\" : \"ok\" }".getBytes()));
      httpResponse.setEntity(entity);
    };
  }
}
