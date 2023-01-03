package com.ontotext.refine.cli.extract;

import static com.ontotext.refine.cli.utils.PrintUtils.error;
import static com.ontotext.refine.cli.utils.PrintUtils.info;

import com.fasterxml.jackson.databind.JsonNode;
import com.ontotext.refine.cli.Process;
import com.ontotext.refine.client.RefineClient;
import com.ontotext.refine.client.command.RefineCommands;
import com.ontotext.refine.client.command.operations.GetOperationsResponse;
import com.ontotext.refine.client.command.project.configurations.GetProjectConfigurationsResponse;
import com.ontotext.refine.client.exceptions.RefineException;
import picocli.CommandLine.Command;
import picocli.CommandLine.ExitCode;
import picocli.CommandLine.Option;
import picocli.CommandLine.Parameters;

/**
 * Defines the extract project configuration process and all of the required arguments for it.
 *
 * @author Antoniy Kunchev
 */
@Command(
    name = "extract",
    description = "Extracts specific project configuration in JSON format.",
    separator = " ",
    sortOptions = false,
    headerHeading = "Usage:%n",
    synopsisHeading = "%n",
    descriptionHeading = "%nDescription:%n",
    parameterListHeading = "%nParameters:%n",
    optionListHeading = "%nOptions:%n",
    mixinStandardHelpOptions = true)
public class ExtractConfigurations extends Process {

  @Parameters(
      index = "0",
      paramLabel = "PROJECT",
      description = "The project whose configurations to extract.")
  private String project;

  @Option(
      names = {"-m", "--mode"},
      description = "Controls which project configuration to be extracted."
          + " The default is '${DEFAULT-VALUE}'."
          + " The allowed values are: ${COMPLETION-CANDIDATES}.",
      completionCandidates = AllowedExtractModes.class,
      defaultValue = "operations")
  private ExtractMode mode;

  @Override
  public Integer call() throws Exception {
    try (RefineClient client = getClient()) {

      String projectId = resolveProject(project);
      info(getConfiguration(projectId, client));

      return ExitCode.OK;
    } catch (RefineException re) {
      error(re.getMessage());
    }
    return ExitCode.SOFTWARE;
  }

  private JsonNode getConfiguration(String projectId, RefineClient client) throws RefineException {
    switch (mode) {
      case IMPORT_OPTIONS:
        return getImportOptions(projectId, client);
      case FULL:
        return getProjectConfigurations(projectId, client).getContent();
      case OPERATIONS:
      default:
        return getOperationsHistory(projectId, client).getContent();
    }
  }

  private JsonNode getImportOptions(String projectId, RefineClient client) throws RefineException {
    return getProjectConfigurations(projectId, client)
        .getImportOptions()
        .orElseThrow(() -> new RefineException(
            "Failed to load the import options for project: %s", project));
  }

  private GetProjectConfigurationsResponse getProjectConfigurations(
      String projectId, RefineClient client)
      throws RefineException {
    return RefineCommands
        .getProjectConfigurations()
        .setProject(projectId)
        .build()
        .execute(client);
  }

  private GetOperationsResponse getOperationsHistory(String projectId, RefineClient client)
      throws RefineException {
    return RefineCommands
        .getOperations()
        .setProject(projectId)
        .build()
        .execute(client);
  }
}
