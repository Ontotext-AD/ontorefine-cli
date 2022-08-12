package com.ontotext.refine.cli;

import com.ontotext.refine.client.RefineClient;
import com.ontotext.refine.client.command.RefineCommands;
import com.ontotext.refine.client.command.operations.GetOperationsResponse;
import com.ontotext.refine.client.exceptions.RefineException;
import picocli.CommandLine.Command;
import picocli.CommandLine.ExitCode;
import picocli.CommandLine.Parameters;

/**
 * Defines the extract operations process and all of the required arguments for it.
 *
 * @author Antoniy Kunchev
 */
@Command(
    name = "extract",
    description = "Extracts the operations of a project in JSON format.",
    separator = " ",
    sortOptions = false,
    headerHeading = "Usage:%n",
    synopsisHeading = "%n",
    descriptionHeading = "%nDescription:%n",
    parameterListHeading = "%nParameters:%n",
    optionListHeading = "%nOptions:%n",
    mixinStandardHelpOptions = true)
class ExtractOperations extends Process {

  @Parameters(
      index = "0",
      paramLabel = "PROJECT",
      description = "The project whose operations to extract.")
  private String project;

  @Override
  public Integer call() throws Exception {
    try (RefineClient client = getClient()) {

      GetOperationsResponse response =
          RefineCommands.getOperations().setProject(project).build().execute(client);

      System.out.println(response.getContent());
      return ExitCode.OK;
    } catch (RefineException re) {
      System.err.println(re.getMessage());
    }
    return ExitCode.SOFTWARE;
  }
}
