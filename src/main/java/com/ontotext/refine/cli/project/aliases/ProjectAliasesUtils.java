package com.ontotext.refine.cli.project.aliases;

import static com.ontotext.refine.cli.project.configurations.ProjectConfigurationsParser.Configuration.ALIASES;
import static com.ontotext.refine.cli.project.configurations.ProjectConfigurationsParser.get;

import com.fasterxml.jackson.databind.JsonNode;
import com.ontotext.refine.client.RefineClient;
import com.ontotext.refine.client.ResponseCode;
import com.ontotext.refine.client.command.RefineCommands;
import com.ontotext.refine.client.command.project.aliases.UpdateProjectAliasesResponse;
import com.ontotext.refine.client.exceptions.RefineException;
import java.io.File;
import java.io.IOException;
import java.util.function.Function;
import org.apache.commons.lang3.ArrayUtils;

/**
 * Contains convenient logic related to project aliases and different commands which are processing
 * aliases.
 *
 * @author Antoniy Kunchev
 */
public final class ProjectAliasesUtils {

  private ProjectAliasesUtils() {
    // no-op
  }

  /**
   * Tries to extract project aliases from the provided aliases argument and project configurations
   * file. Primary used when the project have to be created and all aliases assigned afterwards.
   *
   * @param aliasesArgument command argument containing aliases
   * @param configurations file containing project configurations in JSON format
   * @return all of the collected aliases, if there are any. May return <code>null</code> or empty
   *         array based on the provided arguments
   * @throws IOException when an error occurs during the parsing of the configuration file
   */
  public static String[] extractAliases(String[] aliasesArgument, File configurations)
      throws IOException {
    if (configurations == null) {
      return aliasesArgument;
    }

    return get(configurations, ALIASES)
        .map(toStrArray())
        .map(configAliasesArray -> ArrayUtils.addAll(configAliasesArray, aliasesArgument))
        .orElse(aliasesArgument);
  }

  private static Function<JsonNode, String[]> toStrArray() {
    return configAliases -> {
      String[] result = new String[configAliases.size()];
      for (int i = 0; i < configAliases.size(); i++) {
        result[i] = configAliases.get(i).asText();
      }
      return result;
    };
  }

  /**
   * Assigns the provided aliases to a project. If the <code>aliases</code> argument does not
   * contain any information, the operation will just return.<br>
   * The assignment is done using the {@link RefineCommands#updateProjectAliases()} command.
   *
   * @param project the identifier of the project, which aliases should be set
   * @param aliases for the project
   * @param client used for execution of the command
   * @return <code>true</code> if the assignment is successful, <code>false</code> otherwise
   * @throws RefineException when an error occurs during the execution of the command or the command
   *                         response is an error
   */
  public static boolean assignAliases(String project, String[] aliases, RefineClient client)
      throws RefineException {
    if (ArrayUtils.isEmpty(aliases)) {
      return false;
    }

    UpdateProjectAliasesResponse aliasesResponse = RefineCommands
        .updateProjectAliases()
        .setProject(project)
        .setAdd(aliases)
        .build()
        .execute(client);

    if (ResponseCode.ERROR.equals(aliasesResponse.getCode())) {
      throw new RefineException(aliasesResponse.getMessage());
    }

    return true;
  }
}
