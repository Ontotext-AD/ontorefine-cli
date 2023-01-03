package com.ontotext.refine.cli;

import com.ontotext.refine.cli.utils.PrintUtils;
import com.ontotext.refine.client.RefineClient;
import com.ontotext.refine.client.RefineClients;
import com.ontotext.refine.client.command.RefineCommands;
import com.ontotext.refine.client.command.csrf.GetCsrfTokenCommand;
import com.ontotext.refine.client.command.project.aliases.IdentifyProjectResponse;
import com.ontotext.refine.client.exceptions.RefineException;
import java.util.concurrent.Callable;
import picocli.CommandLine.Option;

/**
 * Base abstraction for all CLI processes. It defines common arguments for all processes and
 * provides some convenient methods used in the processes.
 *
 * @author Antoniy Kunchev
 */
public abstract class Process implements Callable<Integer> {

  // cached instance to avoid re-initialization, when there are multiple request within one process
  private RefineClient client;

  @Option(
      names = {"-u", "--url"},
      description = "The URL of the Ontotext Refine instance to connect to, e.g. http://localhost:7333.",
      required = true)
  private String url;

  /**
   * Creates {@link RefineClient} instance and caches it in order to be reused if there are
   * subsequent uses within the same process.
   *
   * @return a {@link RefineClient} instance
   * @throws RefineException when there is an issue with the creation of the client
   */
  protected RefineClient getClient() throws RefineException {
    if (client == null) {
      try {
        client = RefineClients.standard(url);
      } catch (Exception exc) {
        throw new RefineException("Failed to get client instance due to: " + exc.getMessage());
      }
    }
    return client;
  }

  /**
   * Executes {@link GetCsrfTokenCommand} and returns the result token.
   *
   * @return a CSRF token as string
   * @throws RefineClientException when the retrieval of the token fails
   */
  protected String getToken() throws RefineException {
    try {
      return RefineCommands.getCsrfToken().build().execute(getClient()).getToken();
    } catch (RefineException re) {
      throw re;
    } catch (Exception exc) {
      throw new RefineException("Failed to retrieve CSRF token due to: " + exc.getMessage());
    }
  }

  /**
   * Tries to retrieve the project identity using the provided input value. The value could be alias
   * or project identifier. It can also be prefixed with any of the allowed prefixes for the
   * Ontotext Refine.<br>
   * The method is useful, when the specific process should allow usage of the aliases instead of
   * identifiers for the project argument.
   *
   * @param value to be used for resolving the project
   * @return string representation of the project identifier or the original value if any error
   *         occurs during the resolving
   */
  protected final String resolveProject(String value) {
    // TODO maybe we don't need to make the request if the value consist of only numbers, but then
    // again the aliases are not restricted and they can be only numbers...

    try {
      IdentifyProjectResponse response =
          RefineCommands.identifyProject().setValue(value).build().execute(getClient());
      return String.valueOf(response.getIdentifier());
    } catch (RefineException re) {
      PrintUtils.info(
          "WARNING! Failed to resolve project using the provided value: '%s' due to: %s."
              + " As fallback the process will try to use the value as project identifier.",
          value,
          re.getMessage());
    }
    return value;
  }
}
