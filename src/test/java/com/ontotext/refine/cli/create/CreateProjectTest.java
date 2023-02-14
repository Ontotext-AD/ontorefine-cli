package com.ontotext.refine.cli.create;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.fasterxml.jackson.databind.node.JsonNodeFactory;
import com.fasterxml.jackson.databind.node.ObjectNode;
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
import org.apache.http.entity.ContentType;
import org.apache.http.entity.StringEntity;
import org.apache.http.protocol.HttpRequestHandler;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
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
  private static boolean failAliasesAssignment;

  @BeforeAll
  static void beforeAll() throws IOException {
    failCsrfRequest = false;
    failAliasesAssignment = false;
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
      commandExecutor().accept(args(resource.getPath(), "-n Restaurants", "-f ods", uriArg));
    } finally {
      String[] errorsArray = consoleErrors().split(System.lineSeparator());
      String lastLine = errorsArray[0];
      assertEquals(
          "Invalid value for option '--format': expected one of [CSV, TSV, EXCEL, JSON, XML]"
              + " (case-insensitive) but was 'ods'",
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

  @Test
  @ExpectedSystemExit(ExitCode.OK)
  void shouldPassSuccessfully_csvWithAliases() {
    try {
      URL resource = getClass().getClassLoader().getResource("Netherlands_restaurants.csv");

      String uriArg = "-u " + responder.getUri();
      String nameArg = "-n Restaurants";
      String aliasesArg = "-a test_alias, other_test_alias, test_alias_one";
      commandExecutor()
          .accept(args(resource.getPath(), nameArg, "-f csv", aliasesArg, uriArg));
    } finally {
      String errors = consoleErrors();
      assertTrue(errors.isEmpty(), "Expected no errors but there were: " + errors);

      assertEquals(
          "Successfully created project with identifier: 1812661014997" + System.lineSeparator()
              + "and aliases: test_alias, other_test_alias, test_alias_one",
          consoleOutput().trim());
    }
  }

  @Test
  @ExpectedSystemExit(ExitCode.SOFTWARE)
  void shouldFail_aliasAssignment() {
    try {
      failAliasesAssignment = true;
      URL resource = getClass().getClassLoader().getResource("Netherlands_restaurants.csv");

      String uriArg = "-u " + responder.getUri();
      String nameArg = "-n Restaurants";
      String aliasesArg = "-a test_alias";
      commandExecutor().accept(args(resource.getPath(), nameArg, "-f csv", aliasesArg, uriArg));
    } finally {
      failAliasesAssignment = false;

      String[] errorsArray = consoleErrors().split(System.lineSeparator());
      String lastLine = errorsArray[errorsArray.length - 1];
      assertEquals("Test error", lastLine);
    }
  }

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

  @Test
  @ExpectedSystemExit(ExitCode.SOFTWARE)
  void shouldFail_missingConfigurationsForXml() {
    try {
      URL resource = getClass().getClassLoader().getResource("test-example.xml");

      String uriArg = "-u " + responder.getUri();
      commandExecutor().accept(args(resource.getPath(), "-n test-xml", "-f xml", uriArg));
    } finally {
      String[] errorsArray = consoleErrors().split(System.lineSeparator());
      String lastLine = errorsArray[errorsArray.length - 1];
      assertEquals(
          "There should be configuration with import options for file format: xml",
          lastLine);
    }
  }

  @Test
  @ExpectedSystemExit(ExitCode.SOFTWARE)
  void shouldFail_missingImportOptionsInConfiguration() {
    try {
      URL resource = getClass().getClassLoader().getResource("test-example.xml");
      URL configurations = getClass().getClassLoader()
          .getResource("invalid-configurations/xml-without-import-opts.json");

      String uriArg = "-u " + responder.getUri();
      String configsArg = "-c " + configurations.getPath();
      commandExecutor()
          .accept(args(resource.getPath(), "-n test-xml", "-f xml", configsArg, uriArg));
    } finally {
      String[] errorsArray = consoleErrors().split(System.lineSeparator());
      String lastLine = errorsArray[errorsArray.length - 1];
      assertEquals("Import options configurations are required for files of type: xml", lastLine);
    }
  }

  @Test
  @ExpectedSystemExit(ExitCode.SOFTWARE)
  void shouldFail_invalidImportConfigurations() {
    try {
      URL resource = getClass().getClassLoader().getResource("test-example.xml");
      URL importOptsJson = getClass().getClassLoader()
          .getResource("invalid-configurations/xml-without-recordPath-property.json");

      String uriArg = "-u " + responder.getUri();
      String importOptsArg = "-c " + importOptsJson.getPath();
      commandExecutor()
          .accept(args(resource.getPath(), "-n test-xml", "-f xml", importOptsArg, uriArg));
    } finally {
      String[] errorsArray = consoleErrors().split(System.lineSeparator());
      String lastLine = errorsArray[errorsArray.length - 1];
      assertEquals(
          "The import options should contain property 'recordPath' of type Array, containing set of"
              + " elements to be used for parsing of the dataset in tabular format.",
          lastLine);
    }
  }

  private static Map<String, HttpRequestHandler> mockResponses() {
    Map<String, HttpRequestHandler> responses = new HashMap<>(4);
    HandlerContext context = new HandlerContext().setFailCsrfRequest(() -> failCsrfRequest);
    responses.put("/orefine/command/core/get-csrf-token", RefineResponder.csrfToken(context));
    responses.put("/orefine/command/core/create-project-from-upload", createProjectHandler());
    responses.put("/project-aliases", assignAliasesHandler());
    responses.put("/orefine/command/core/delete-project", deleteProjectHandler());
    return responses;
  }

  private static HttpRequestHandler createProjectHandler() {
    return (request, response, context) -> {
      response.setStatusCode(HttpStatus.SC_MOVED_TEMPORARILY);
      response.addHeader("Location", "?projectId=" + PROJECT_ID);
    };
  }

  private static HttpRequestHandler assignAliasesHandler() {
    return (request, response, context) -> {
      int code = HttpStatus.SC_OK;

      if (failAliasesAssignment) {
        code = HttpStatus.SC_INTERNAL_SERVER_ERROR;

        ObjectNode errorJson = JsonNodeFactory.instance.objectNode().put("message", "Test error");
        response.setEntity(new StringEntity(errorJson.toString(), ContentType.APPLICATION_JSON));
      }

      response.setStatusCode(code);
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
