package com.ontotext.refine.cli;

import static com.ontotext.refine.cli.utils.PrintUtils.error;
import static com.ontotext.refine.cli.utils.PrintUtils.info;

import com.ontotext.refine.cli.utils.ExportUtils;
import com.ontotext.refine.client.RefineClient;
import com.ontotext.refine.client.command.RefineCommands;
import com.ontotext.refine.client.command.export.Engines;
import com.ontotext.refine.client.command.export.ExportRowsResponse;
import com.ontotext.refine.client.exceptions.RefineException;
import java.io.File;
import java.nio.charset.StandardCharsets;
import org.apache.commons.io.FileUtils;
import picocli.CommandLine.Command;
import picocli.CommandLine.ExitCode;
import picocli.CommandLine.Parameters;

/**
 * Defines the export process for different formats and all of the required arguments for it.
 *
 * @author Antoniy Kunchev
 */
@Command(
    name = "export",
    description = "Exports the data of a project in CSV or JSON format.",
    separator = " ")
class Export extends Process {

  @Parameters(
      index = "0",
      paramLabel = "PROJECT",
      description = "The identifier of the project to export.")
  private String project;

  @Parameters(
      index = "1",
      paramLabel = "FORMAT",
      description = "The output format of the export (only csv at the moment).",
      defaultValue = "csv")
  private String format;

  @Override
  public Integer call() throws Exception {
    if (!"csv".equalsIgnoreCase(format)) {
      error("The format: '%s' is currently not supported.", format);
      return ExitCode.USAGE;
    }

    File result = null;
    try (RefineClient client = getClient()) {
      ExportUtils.awaitProcessesCompletion(project, client);

      ExportRowsResponse response = RefineCommands
          .exportRows()
          .setProject(project)
          .setFormat(format)
          .setEngine(Engines.ROW_BASED)
          .setToken(getToken())
          .build()
          .execute(client);

      result = response.getFile();

      info(FileUtils.readFileToString(result, StandardCharsets.UTF_8));
      return ExitCode.OK;
    } catch (RefineException re) {
      error(re.getMessage());
    } finally {
      FileUtils.deleteQuietly(result);
    }
    return ExitCode.SOFTWARE;
  }
}
