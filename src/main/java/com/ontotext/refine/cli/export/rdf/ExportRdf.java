package com.ontotext.refine.cli.export.rdf;

import static com.ontotext.refine.cli.utils.ExportUtils.awaitProcessesCompletion;
import static com.ontotext.refine.cli.utils.PrintUtils.error;
import static com.ontotext.refine.cli.utils.PrintUtils.print;
import static com.ontotext.refine.cli.utils.RdfExportUtils.export;

import com.ontotext.refine.cli.Process;
import com.ontotext.refine.cli.utils.RdfExportUtils.Using;
import com.ontotext.refine.client.RefineClient;
import com.ontotext.refine.client.exceptions.RefineException;
import java.io.File;
import picocli.CommandLine.Command;
import picocli.CommandLine.ExitCode;
import picocli.CommandLine.Option;
import picocli.CommandLine.Parameters;

/**
 * Defines the convert RDF process and all of the required arguments for it.
 *
 * @author Antoniy Kunchev
 */
@Command(
    name = "rdf",
    description = "Exports the data of a project to RDF format.",
    separator = " ",
    sortOptions = false,
    headerHeading = "Usage:%n",
    synopsisHeading = "%n",
    descriptionHeading = "%nDescription:%n",
    parameterListHeading = "%nParameters:%n",
    optionListHeading = "%nOptions:%n",
    mixinStandardHelpOptions = true)
public class ExportRdf extends Process {

  @Parameters(
      index = "0",
      paramLabel = "PROJECT",
      description = "The project whose data to converted to RDF.")
  private String project;

  @Option(
      names = {"-q", "--sparql"},
      description = "A file containing SPARQL CONSTRUCT query to be used for RDF conversion.")
  private File sparql;

  @Option(
      names = {"-m", "--mapping"},
      description = "The mapping that will be used for the RDF conversion. The file should contain"
          + " JSON configuration. If not provided the process will try to retrieve it from the"
          + " project configurations.")
  private File mapping;

  @Option(
      names = {"-f", "--format"},
      description = "Controls the format of the result."
          + " The default format is '${DEFAULT-VALUE}'."
          + " The allowed values are: ${COMPLETION-CANDIDATES}",
      completionCandidates = AllowedRdfResultFormat.class,
      defaultValue = "turtle")
  private RdfResultFormats format;

  @Override
  public Integer call() {
    try (RefineClient client = getClient()) {

      String projectId = resolveProject(project);

      // wait for any unfinished process
      // TODO: flag this to give the user option to skip it
      awaitProcessesCompletion(projectId, client);

      if (sparql != null) {
        print(export(projectId, sparql, format, Using.SPARQL, client));
      } else {
        print(export(projectId, mapping, format, Using.MAPPING, client));
      }

      return ExitCode.OK;
    } catch (RefineException re) {
      error(re.getMessage());
    } catch (Exception exc) {
      error(
          "Failed to execute RDF conversion for project: '%s' due to %s",
          project,
          exc.getMessage());
    }
    return ExitCode.SOFTWARE;
  }
}
