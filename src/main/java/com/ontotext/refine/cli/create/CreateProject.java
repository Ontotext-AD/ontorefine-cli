package com.ontotext.refine.cli.create;

import static com.ontotext.refine.cli.project.configurations.ProjectConfigurationsParser.Configuration.IMPORT_OPTIONS;
import static com.ontotext.refine.cli.project.configurations.ProjectConfigurationsParser.get;
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
          + " (csv).") // tsv, , json, txt, xls, xlsx, ods
  private File file;

  @Option(
      names = {"-n", "--name"},
      description = "A name for the Refine project. If not provided, the file name will be used.")
  private String name;

  @Option(
      names = {"-f", "--format"},
      description = "The format of the provided file. The default format is '${DEFAULT-VALUE}'."
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

  @Override
  public Integer call() throws Exception {
    if (FileValidator.doesNotExists(file, "FILE")) {
      return ExitCode.USAGE;
    }

    try (RefineClient client = getClient()) {

      Builder command = RefineCommands
          .createProject()
          .file(file)
          .format(format.toUploadFormat())
          .name(StringUtils.defaultIfBlank(name, file.getName()))
          .token(getToken());

      if (configurations != null) {
        get(configurations, IMPORT_OPTIONS).ifPresent(opts -> command.options(opts::asText));
      }

      CreateProjectResponse response = command.build().execute(client);

      info("Successfully created project with identifier: %s", response.getProjectId());
      return ExitCode.OK;
    } catch (RefineException re) {
      error(re.getMessage());
    }
    return ExitCode.SOFTWARE;
  }
}
