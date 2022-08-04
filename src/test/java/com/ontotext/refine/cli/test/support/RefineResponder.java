package com.ontotext.refine.cli.test.support;

import static java.util.Collections.emptyList;

import com.fasterxml.jackson.databind.node.JsonNodeFactory;
import com.fasterxml.jackson.databind.node.ObjectNode;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Map;
import java.util.concurrent.TimeUnit;
import java.util.function.Supplier;
import org.apache.http.HttpStatus;
import org.apache.http.entity.BasicHttpEntity;
import org.apache.http.impl.bootstrap.HttpServer;
import org.apache.http.protocol.HttpRequestHandler;

/**
 * Responder which acts like Refine server and handles the responses for those request.
 *
 * @author Antoniy Kunchev
 */
public class RefineResponder {

  private HttpServer server;

  /**
   * Starts the responder. The methods starts HTTP server on randomized port and starts it.
   *
   * @param responses for the requests
   * @throws IOException when error occurs during server creation
   */
  public void start(Map<String, HttpRequestHandler> responses) throws IOException {
    server = HttpTestUtils.createTestServer(responses, 0);
    server.start();
  }

  /**
   * Shutdowns the server of the responder.
   */
  public void stop() {
    if (server != null) {
      server.shutdown(200, TimeUnit.MILLISECONDS);
    }
  }

  /**
   * Provides the full URI of the responder. Example: http://localhost:8080
   *
   * @return the URI of the responder as string
   */
  public String getUri() {
    return HttpTestUtils.buildUri(server, "").toString();
  }

  /**
   * Stub for the CSRF token request with the option to fail, if required. The option is controlled
   * through the context.
   *
   * @param context provides different options for the stub
   * @return request handler stub
   */
  public static HttpRequestHandler csrfToken(HandlerContext context) {
    return (httpRequest, httpResponse, httpContext) -> {
      if (context.failCsrfRequest.get()) {
        httpResponse.setStatusCode(HttpStatus.SC_INTERNAL_SERVER_ERROR);
      } else {
        httpResponse.setStatusCode(HttpStatus.SC_OK);
        BasicHttpEntity entity = new BasicHttpEntity();
        entity.setContent(new ByteArrayInputStream(
            "{\"token\": \"tBElPXvVJc4K0G8YtAKSZNFvYJYtRgj1\" }".getBytes()));
        httpResponse.setEntity(entity);
      }
    };
  }

  /**
   * Stub for the project processes retrieval. The response that will be returned is going to be
   * empty list of processes.
   *
   * @return request handler stub
   */
  public static HttpRequestHandler noProcesses() {
    return (httpRequest, httpResponse, httpContext) -> {
      httpResponse.setStatusCode(HttpStatus.SC_OK);
      BasicHttpEntity entity = new BasicHttpEntity();
      ObjectNode node = JsonNodeFactory.instance.objectNode().putPOJO("processes", emptyList());
      entity.setContent(new ByteArrayInputStream(node.toString().getBytes()));
      httpResponse.setEntity(entity);
    };
  }

  /**
   * Stub for the RDF export command.
   *
   * @return handler for the export RDF command
   */
  public static HttpRequestHandler exportHandler() {
    return (request, response, context) -> {
      response.setStatusCode(HttpStatus.SC_OK);
      BasicHttpEntity entity = new BasicHttpEntity();
      entity.setContent(loadResource("exportedRdf.ttl"));
      entity.setContentLength(69);
      response.setEntity(entity);
    };
  }

  private static InputStream loadResource(String resource) {
    return RefineResponder.class.getClassLoader().getResourceAsStream(resource);
  }

  /**
   * Stub for the create project command.
   *
   * @param project identifier that should be returned as response from the command execution
   * @return handler for the create project command
   */
  public static HttpRequestHandler createProjectHandler(String project) {
    return (httpRequest, httpResponse, httpContext) -> {
      httpResponse.setStatusCode(HttpStatus.SC_MOVED_TEMPORARILY);
      httpResponse.addHeader("Location", "?projectId=" + project);
    };
  }

  /**
   * Used to provide various options for the request stubbing.
   *
   * @author Antoniy Kunchev
   */
  public static class HandlerContext {

    private Supplier<Boolean> failCsrfRequest = () -> Boolean.FALSE;

    public HandlerContext setFailCsrfRequest(Supplier<Boolean> failCsrfRequest) {
      this.failCsrfRequest = failCsrfRequest;
      return this;
    }
  }
}
