package com.ontotext.refine.cli.transform;

import static com.ontotext.refine.cli.operations.OperationsUtil.getOperations;
import static com.ontotext.refine.cli.project.configurations.ProjectConfigurationsParser.Configuration.IMPORT_OPTIONS;
import static com.ontotext.refine.cli.project.configurations.ProjectConfigurationsParser.get;
import static com.ontotext.refine.cli.utils.ExportUtils.awaitProcessesCompletion;
import static com.ontotext.refine.cli.utils.PrintUtils.error;
import static com.ontotext.refine.cli.utils.PrintUtils.print;
import static com.ontotext.refine.cli.utils.RdfExportUtils.export;
import static com.ontotext.refine.cli.validation.FileValidator.doesNotExists;
import static java.lang.String.format;

import com.ontotext.refine.cli.Process;
import com.ontotext.refine.cli.create.AllowedInputDataFormats;
import com.ontotext.refine.cli.create.InputDataFormat;
import com.ontotext.refine.cli.export.rdf.AllowedRdfResultFormat;
import com.ontotext.refine.cli.export.rdf.RdfResultFormats;
import com.ontotext.refine.cli.utils.RdfExportUtils.Using;
import com.ontotext.refine.client.JsonOperation;
import com.ontotext.refine.client.RefineClient;
import com.ontotext.refine.client.ResponseCode;
import com.ontotext.refine.client.command.RefineCommands;
import com.ontotext.refine.client.command.create.CreateProjectCommand.Builder;
import com.ontotext.refine.client.command.operations.ApplyOperationsResponse;
import com.ontotext.refine.client.exceptions.RefineException;
import java.io.File;
import java.io.IOException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import org.apache.commons.io.IOUtils;
import picocli.CommandLine.Command;
import picocli.CommandLine.ExitCode;
import picocli.CommandLine.Option;
import picocli.CommandLine.Parameters;

/**
 * Defines a transformation operation and all of the parameters that it have.<br>
 * Currently the operation will handle transformation from CSV to RDF using either mapping defined
 * as JSON or SPARQL CONSTRUCT query.<br>
 * The idea for the future releases is to have this operation convert almost everything to
 * everything (XML to CSV, JSON to XML, etc.). Basically the conventions that the Ontotext Refine
 * tool can support via project creation and export functionalities.
 *
 * @author Antoniy Kunchev
 */
@Command(
    name = "transform",
    description = "Transforms given dataset into different data format.",
    separator = " ",
    sortOptions = false,
    headerHeading = "Usage:%n",
    synopsisHeading = "%n",
    descriptionHeading = "%nDescription:%n",
    parameterListHeading = "%nParameters:%n",
    optionListHeading = "%nOptions:%n",
    mixinStandardHelpOptions = true)
public class Transform extends Process {

  @Parameters(
      index = "0",
      paramLabel = "FILE",
      description = "The file containing the data that should be transformed."
          + " It should be a full name with one of the supported extensions:"
          + " (csv).")
  private File file;

  @Option(
      names = {"-f", "--format"},
      description = "The format of the provided file. The default format is '${DEFAULT-VALUE}'."
          + " The allowed values are: ${COMPLETION-CANDIDATES}",
      completionCandidates = AllowedInputDataFormats.class,
      defaultValue = "csv")
  private InputDataFormat format;

  @Option(
      names = {"-c", "--configurations"},
      description = "A file with the configurations that should be used for project creation."
          + " Ideally it should contain the import options and the operations history of the"
          + " project, but it is also allowed for one one of the configurations to be present."
          + " The mapping for the RDFization of the dataset is stored as operation to the history."
          + " The file should contain JSON document.")
  private File configurations;

  @Option(
      names = {"-q", "--sparql"},
      description = "A file containing SPARQL CONSTRUCT query to be used for RDFization of the"
          + " provided dataset.")
  private File sparql;

  @Option(
      names = {"-r", "--result"},
      description = "Controls the output format of the result. The default format is"
          + " '${DEFAULT-VALUE}'. The allowed values are: ${COMPLETION-CANDIDATES}",
      completionCandidates = AllowedRdfResultFormat.class,
      defaultValue = "turtle")
  private RdfResultFormats result;

  @Option(
      names = "--no-clean",
      negatable = true,
      description = "Controls the cleaning of the project after the operation execution."
          + " When enabled the clean up will be executed regardless of the success of"
          + " the transformation. By default the cleaning is enabled.")
  private boolean clean = true;

  @Override
  public Integer call() throws Exception {
    if (doesNotExists(file, "FILE")) {
      return ExitCode.USAGE;
    }

    if (doesNotExists(sparql) && doesNotExists(configurations)) {
      error(
          "Expected at least one of '--configurations' or '--sparql' parameters to be available.");
      return ExitCode.USAGE;
    }

    String project = null;
    RefineClient client = getClient();
    try {
      project = createProject(client);

      if (!applyOperations(project, client)) {
        return ExitCode.SOFTWARE;
      }

      awaitProcessesCompletion(project, client);

      if (sparql != null) {
        print(export(project, sparql, result, Using.SPARQL, client));
      } else {
        print(export(project, configurations, result, Using.MAPPING, client));
      }

      return ExitCode.OK;
    } catch (RefineException re) {
      error(re.getMessage());
    } catch (Exception exc) {
      error(
          "Unexpected error occurred during transformation of the dataset '%s'. Details: %s",
          file.getName(),
          exc.getMessage());
    } finally {
      cleanup(project, client);
      IOUtils.closeQuietly(client);
    }
    return ExitCode.SOFTWARE;
  }

  private String createProject(RefineClient client) throws IOException {
    String currentDate = DateTimeFormatter.ISO_LOCAL_DATE.format(LocalDate.now());
    Builder command = RefineCommands
        .createProject().file(file)
        .format(format.toUploadFormat())
        .name(format("cli-transform-%s-%s", file.getName(), currentDate))
        .token(getToken());

    if (configurations != null) {
      get(configurations, IMPORT_OPTIONS).ifPresent(opts -> command.options(opts::asText));
    }

    return command.build().execute(client).getProjectId();
  }

  private boolean applyOperations(String project, RefineClient client) {
    // we should not try to apply the operations, if there aren't any
    if (doesNotExists(configurations)) {
      return true;
    }

    try {
      ApplyOperationsResponse response = RefineCommands
          .applyOperations()
          .project(project)
          .token(getToken())
          .operations(JsonOperation.from(getOperations(configurations)))
          .build()
          .execute(client);

      if (ResponseCode.ERROR.equals(response.getCode())) {
        error(
            "Failed to apply transformation to dataset '%s' due to: %s",
            file.getName(),
            response.getMessage());
        return false;
      }
      return true;
    } catch (Exception exc) { // NOSONAR
      error(
          "Failed to apply operations to dataset '%s'. Check if the provided JSON is correct.",
          file.getName());
      return false;
    }
  }

  private void cleanup(String project, RefineClient client) throws IOException {
    if (project == null || !clean) {
      return;
    }

    RefineCommands.deleteProject().project(project).token(getToken()).build().execute(client);
  }
}
