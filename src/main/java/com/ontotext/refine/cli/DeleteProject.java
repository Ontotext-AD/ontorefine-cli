package com.ontotext.refine.cli;

import static com.ontotext.refine.cli.utils.PrintUtils.error;
import static com.ontotext.refine.cli.utils.PrintUtils.info;

import com.ontotext.refine.client.RefineClient;
import com.ontotext.refine.client.ResponseCode;
import com.ontotext.refine.client.command.RefineCommands;
import com.ontotext.refine.client.command.delete.DeleteProjectResponse;
import com.ontotext.refine.client.exceptions.RefineException;
import picocli.CommandLine.Command;
import picocli.CommandLine.ExitCode;
import picocli.CommandLine.Parameters;

/**
 * Defines the delete project process and all of the required arguments for it.
 *
 * @author Antoniy Kunchev
 */
@Command(
    name = "delete",
    description = "Deletes a project from Ontotext Refine.",
    separator = " ",
    sortOptions = false,
    headerHeading = "Usage:%n",
    synopsisHeading = "%n",
    descriptionHeading = "%nDescription:%n",
    parameterListHeading = "%nParameters:%n",
    optionListHeading = "%nOptions:%n",
    mixinStandardHelpOptions = true)
class DeleteProject extends Process {

  @Parameters(
      index = "0",
      paramLabel = "PROJECT",
      description = "The identifier of the project that should be deleted.")
  private String project;

  @Override
  public Integer call() throws Exception {
    try (RefineClient client = getClient()) {

      DeleteProjectResponse response = RefineCommands.deleteProject()
          .project(project)
          .token(getToken())
          .build()
          .execute(client);

      if (ResponseCode.ERROR.equals(response.getCode())) {
        error(
            "Failed to delete project with identifier '%s' due to: %s",
            project,
            response.getMessage());
        return ExitCode.SOFTWARE;
      }

      info("Successfully deleted project with identifier: %s", project);
      return ExitCode.OK;
    } catch (RefineException re) {
      error(re.getMessage());
    }
    return ExitCode.SOFTWARE;
  }
}
