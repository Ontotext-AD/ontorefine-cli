package com.ontotext.refine.cli.project.aliases;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.ontotext.refine.cli.BaseProcessTest;
import com.ontotext.refine.cli.test.support.ExpectedSystemExit;
import com.ontotext.refine.cli.test.support.RefineResponder;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import org.apache.http.HttpStatus;
import org.apache.http.protocol.HttpRequestHandler;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import picocli.CommandLine.ExitCode;

/**
 * Test for {@link UpdateAliases}.
 *
 * @author Antoniy Kunchev
 */
class UpdateAliasesTest extends BaseProcessTest {

  private static RefineResponder responder = new RefineResponder();
  private static boolean shouldRespondWithError;
  private static boolean shouldThrowException;

  @BeforeAll
  static void beforeAll() throws IOException {
    shouldRespondWithError = false;
    shouldThrowException = false;
    responder.start(mockResponses());
  }

  @AfterAll
  static void cleanup() {
    responder.stop();
  }

  @Override
  protected Class<?> command() {
    return UpdateAliases.class;
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
  @ExpectedSystemExit(ExitCode.USAGE)
  void shouldExitWithErrorForMissingArguments() {
    try {
      commandExecutor().accept(args(PROJECT_ID, "-u " + responder.getUri()));
    } finally {
      String actualLine = consoleErrors().split(System.lineSeparator())[0];
      assertEquals(
          "At least one value should be provided for either '--add' or '--remove' arguments.",
          actualLine);
    }
  }

  @Test
  @ExpectedSystemExit(ExitCode.OK)
  void shouldPassSuccessfully() {
    try {
      String uriArg = "-u " + responder.getUri();
      String addArg = "-a test-alias, other-test-alias";
      String removeArg = "-r another-test-alias";

      commandExecutor().accept(args(PROJECT_ID, addArg, removeArg, uriArg));
    } finally {
      String errors = consoleErrors();
      assertTrue(errors.isEmpty(), "Expected no errors but there were: " + errors);

      String[] outputArray = consoleOutput().split(System.lineSeparator());
      String lastLine = outputArray[outputArray.length - 1];

      assertEquals("Successfully updated the aliases of project: 1812661014997", lastLine);
    }
  }

  @Test
  @ExpectedSystemExit(ExitCode.SOFTWARE)
  void shouldReturnError() {
    try {
      shouldRespondWithError = true;

      String uriArg = "-u " + responder.getUri();
      commandExecutor().accept(args(PROJECT_ID, "-a test-alias", uriArg));
    } finally {
      shouldRespondWithError = false;

      String error = consoleErrors().split(System.lineSeparator())[0];
      assertEquals(
          "Failed to update aliases of project '1812661014997' due to: Internal Server Error",
          error);
    }
  }

  @Test
  @ExpectedSystemExit(ExitCode.SOFTWARE)
  void shouldFailDueToException() {
    try {
      shouldThrowException = true;

      String uriArg = "-u " + responder.getUri();
      commandExecutor().accept(args(PROJECT_ID, "-a test-alias", uriArg));
    } finally {
      shouldThrowException = false;

      String error = consoleErrors().split(System.lineSeparator())[0];
      assertEquals(
          "Failed to update the alias data of project: '1812661014997' due to: Connection reset",
          error);
    }
  }

  private static Map<String, HttpRequestHandler> mockResponses() {
    Map<String, HttpRequestHandler> handlers = new HashMap<>(1);
    handlers.put("/project-aliases", handleUpdateAliases());
    return handlers;
  }

  private static HttpRequestHandler handleUpdateAliases() {
    return (request, response, ctx) -> {
      if (shouldThrowException) {
        throw new IOException();
      }

      int code = shouldRespondWithError ? HttpStatus.SC_INTERNAL_SERVER_ERROR : HttpStatus.SC_OK;
      response.setStatusCode(code);
    };
  }
}
