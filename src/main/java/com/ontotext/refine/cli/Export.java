package com.ontotext.refine.cli;

import com.ontotext.refine.client.RefineClient;
import com.ontotext.refine.client.command.ExportRowsResponse;
import com.ontotext.refine.client.command.RefineCommands;
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
      description = "The output format of the export (csv or json).")
  private String format;

  @Override
  public Integer call() throws Exception {
    if (!isFormatSupported()) {
      System.err.println(String.format("The format: '%s' is currently not supported.", format));
      return ExitCode.USAGE;
    }

    File result = null;
    try (RefineClient client = getClient()) {

      ExportRowsResponse response = RefineCommands
          .exportRows()
          .project(project)
          .format(format)
          // TODO not sure if there are more options for the engine
          .engine("{\"mode\":\"row-based\"}")
          .token(getToken())
          .build()
          .execute(client);

      result = response.getFile();

      System.out.println(FileUtils.readFileToString(result, StandardCharsets.UTF_8));
      return ExitCode.OK;
    } catch (RefineException re) {
      System.err.println(re.getMessage());
    } finally {
      FileUtils.deleteQuietly(result);
    }
    return ExitCode.SOFTWARE;
  }

  private boolean isFormatSupported() {
    return "csv".equalsIgnoreCase(format) || "json".equalsIgnoreCase(format);
  }
}
