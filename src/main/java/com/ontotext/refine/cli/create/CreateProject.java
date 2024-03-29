package com.ontotext.refine.cli.create;

import static com.ontotext.refine.cli.project.aliases.ProjectAliasesUtils.assignAliases;
import static com.ontotext.refine.cli.project.aliases.ProjectAliasesUtils.extractAliases;
import static com.ontotext.refine.cli.utils.PrintUtils.error;
import static com.ontotext.refine.cli.utils.PrintUtils.info;

import com.ontotext.refine.cli.Process;
import com.ontotext.refine.cli.validation.FileValidator;
import com.ontotext.refine.client.RefineClient;
import com.ontotext.refine.client.command.RefineCommands;
import com.ontotext.refine.client.command.create.CreateProjectCommand.Builder;
import com.ontotext.refine.client.command.create.CreateProjectResponse;
import com.ontotext.refine.client.exceptions.RefineException;
import java.io.File;
import java.io.IOException;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringUtils;
import picocli.CommandLine.Command;
import picocli.CommandLine.ExitCode;
import picocli.CommandLine.Option;
import picocli.CommandLine.Parameters;

/**
 * Defines the create project process and all of the required arguments for it.
 *
 * @author Antoniy Kunchev
 */
@Command(
    name = "create",
    description = "Creates a new project from a file.",
    separator = " ",
    sortOptions = false,
    headerHeading = "Usage:%n",
    synopsisHeading = "%n",
    descriptionHeading = "%nDescription:%n",
    parameterListHeading = "%nParameters:%n",
    optionListHeading = "%nOptions:%n",
    mixinStandardHelpOptions = true)
public class CreateProject extends Process {

  @Parameters(
      index = "0",
      paramLabel = "FILE",
      description = "The file that will be used to create the project."
          + " It should be a full name with one of the supported extensions"
          + " (csv, tsv, json, xls, xlsx, etc.).")
  private File file;

  @Option(
      names = {"-n", "--name"},
      description = "A name for the Refine project. If not provided, the file name will be used.")
  private String name;

  @Option(
      names = {"-f", "--format"},
      description = "The format of the provided file. The default format is '${DEFAULT-VALUE}'."
          + " Except 'csv', all other formats are in experimental state."
          + " The allowed values are: ${COMPLETION-CANDIDATES}",
      completionCandidates = AllowedInputDataFormats.class,
      defaultValue = "csv")
  private InputDataFormat format;

  @Option(
      names = {"-c", "--configurations"},
      description = "File containing configurations for the importing process of the dataset."
          + " It includes information how to parse the input data so that it can be represented"
          + " in tabular form and additioanl options related to the project creation.")
  private File configurations;

  @Option(
      names = {"-a", "--aliases"},
      description = "Aliases for the project. The argument accepts multiple comma separated"
          + " values.",
      split = ",|, ",
      splitSynopsisLabel = ",")
  private String[] aliases;

  @Override
  public Integer call() throws Exception {
    if (FileValidator.doesNotExists(file, "FILE")) {
      return ExitCode.USAGE;
    }

    String projectId = null;
    RefineClient client = getClient();
    try {
      CreateProjectResponse response = createProject(client);

      projectId = response.getProjectId();

      String[] extractedAliases = extractAliases(aliases, configurations);
      boolean hasAssignedAliases = assignAliases(projectId, extractedAliases, client);

      info("Successfully created project with identifier: %s", projectId);

      if (hasAssignedAliases) {
        info("and aliases: %s", String.join(",", extractedAliases));
      }

      return ExitCode.OK;
    } catch (RefineException re) {
      handleError(projectId, client, re.getMessage());
    } finally {
      IOUtils.closeQuietly(client);
    }
    return ExitCode.SOFTWARE;
  }


  private CreateProjectResponse createProject(RefineClient client) throws IOException {
    Builder command = RefineCommands
        .createProject()
        .file(file)
        .format(format.toUploadFormat())
        .name(StringUtils.defaultIfBlank(name, file.getName()))
        .token(getToken());

    ImportOptionsProcessor.validateAndConsume(
        configurations, format, opts -> command.options(opts::toString));

    return command.build().execute(client);
  }


  private void handleError(String projectId, RefineClient client, String message)
      throws RefineException {

    // in order to simulate some kind of weird transaction,
    // if there is an error after the point of project creation, we need to clean it up
    if (projectId != null) {
      RefineCommands.deleteProject().project(projectId).token(getToken()).build().execute(client);
    }

    error(message);
  }
}
