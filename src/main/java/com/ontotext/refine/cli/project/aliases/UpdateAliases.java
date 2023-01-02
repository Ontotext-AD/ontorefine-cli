package com.ontotext.refine.cli.project.aliases;

import static com.ontotext.refine.cli.utils.PrintUtils.error;
import static com.ontotext.refine.cli.utils.PrintUtils.info;

import com.ontotext.refine.cli.Process;
import com.ontotext.refine.client.RefineClient;
import com.ontotext.refine.client.ResponseCode;
import com.ontotext.refine.client.command.RefineCommands;
import com.ontotext.refine.client.command.project.aliases.UpdateProjectAliasesResponse;
import com.ontotext.refine.client.exceptions.RefineException;
import org.apache.commons.lang3.ArrayUtils;
import picocli.CommandLine.Command;
import picocli.CommandLine.ExitCode;
import picocli.CommandLine.Option;
import picocli.CommandLine.Parameters;

/**
 * Defines the update project aliases process and all of the required arguments for it.
 *
 * @author Antoniy Kunchev
 */
@Command(
    name = "update-aliases",
    description = "Updates project aliases. The command can add and remove values in a single"
        + " invocation.",
    separator = " ",
    sortOptions = false,
    headerHeading = "Usage:%n",
    synopsisHeading = "%n",
    descriptionHeading = "%nDescription:%n",
    parameterListHeading = "%nParameters:%n",
    optionListHeading = "%nOptions:%n",
    mixinStandardHelpOptions = true)
public class UpdateAliases extends Process {

  @Parameters(
      index = "0",
      paramLabel = "PROJECT",
      description = "The identifier of the project to which the transformation"
          + " operations will be applied.")
  private String project;

  @Option(
      names = {"-a", "--add"},
      description = "Aliases to add to the project. The argument accepts multiple comma separated"
          + " values.",
      split = ",|, ",
      splitSynopsisLabel = ",")
  private String[] add;

  @Option(
      names = {"-r", "--remove"},
      description = "Aliases to remove from the project. The argument accepts multiple comma"
          + " separated values.",
      split = ",|, ",
      splitSynopsisLabel = ",")
  private String[] remove;

  @Override
  public Integer call() throws Exception {
    if (ArrayUtils.isEmpty(add) && ArrayUtils.isEmpty(remove)) {
      error("At least one value should be provided for either '--add' or '--remove' arguments.");
      return ExitCode.USAGE;
    }

    try (RefineClient client = getClient()) {

      UpdateProjectAliasesResponse response = RefineCommands
          .updateProjectAliases()
          .setProject(project)
          .setAdd(add)
          .setRemove(remove)
          .build()
          .execute(client);

      if (ResponseCode.ERROR.equals(response.getCode())) {
        error(
            "Failed to update aliases of project '%s' due to: %s",
            project,
            response.getMessage());
        return ExitCode.SOFTWARE;
      }

      info("Successfully updated the aliases of project: %s", project);
      return ExitCode.OK;
    } catch (RefineException re) {
      error(re.getMessage());
    }
    return ExitCode.SOFTWARE;
  }
}
