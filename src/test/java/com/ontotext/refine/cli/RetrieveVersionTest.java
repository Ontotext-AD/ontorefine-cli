package com.ontotext.refine.cli;

import static java.util.Collections.singletonMap;
import static org.junit.jupiter.api.Assertions.assertEquals;

import com.fasterxml.jackson.databind.node.JsonNodeFactory;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.ontotext.refine.cli.test.support.ExpectedSystemExit;
import com.ontotext.refine.cli.test.support.RefineResponder;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.util.Map;
import org.apache.http.HttpStatus;
import org.apache.http.entity.BasicHttpEntity;
import org.apache.http.protocol.HttpRequestHandler;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import picocli.CommandLine.ExitCode;

/**
 * Test for {@link RetrieveVersion}.
 *
 * @author Antoniy Kunchev
 */
class RetrieveVersionTest extends BaseProcessTest {

  private static RefineResponder responder = new RefineResponder();
  private static boolean shouldFail;

  @BeforeAll
  static void beforeAll() throws IOException {
    shouldFail = false;
    responder.start(mockResponses());
  }

  @AfterAll
  static void cleanup() {
    responder.stop();
  }

  @Override
  protected Class<?> command() {
    return RetrieveVersion.class;
  }

  @Test
  @ExpectedSystemExit(ExitCode.OK)
  void shouldPassSuccessfully() {
    try {
      commandExecutor().accept(args("-u " + responder.getUri()));
    } finally {
      String expected =
          "Name: OpenRefine 2.6 [1]\nFull version: 2.6 [1]\nVersion: 2.6\nRevision: 1";
      String consoleOutput = consoleOutput();
      assertEquals(expected, consoleOutput.trim());
    }
  }

  @Test
  @ExpectedSystemExit(ExitCode.SOFTWARE)
  void shouldFail() {
    try {
      shouldFail = true;

      commandExecutor().accept(args("-u " + responder.getUri()));
    } finally {
      shouldFail = false;

      String expected = "Failed to retrieve the version data due to: "
          + "Unexpected response : HTTP/1.1 500 Internal Server Error";
      assertEquals(expected, consoleErrors().trim());
    }
  }

  private static Map<String, HttpRequestHandler> mockResponses() {
    return singletonMap("/orefine/command/core/get-version", versionHandler());
  }

  private static HttpRequestHandler versionHandler() {
    return (httpRequest, httpResponse, httpContext) -> {
      if (shouldFail) {
        httpResponse.setStatusCode(HttpStatus.SC_INTERNAL_SERVER_ERROR);
      } else {
        httpResponse.setStatusCode(HttpStatus.SC_OK);
        BasicHttpEntity entity = new BasicHttpEntity();
        ObjectNode json = JsonNodeFactory.instance
            .objectNode()
            .put("full_name", "OpenRefine 2.6 [1]")
            .put("full_version", "2.6 [1]")
            .put("version", "2.6")
            .put("revision", "1");
        entity.setContent(new ByteArrayInputStream(json.toString().getBytes()));
        httpResponse.setEntity(entity);
      }
    };
  }
}
