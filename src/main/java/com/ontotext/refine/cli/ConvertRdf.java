package com.ontotext.refine.cli;

import com.fasterxml.jackson.databind.JsonNode;
import com.ontotext.refine.client.RefineClient;
import com.ontotext.refine.client.command.RefineCommands;
import com.ontotext.refine.client.command.models.GetProjectModelsResponse;
import com.ontotext.refine.client.command.rdf.ExportRdfResponse;
import com.ontotext.refine.client.command.rdf.ResultFormat;
import com.ontotext.refine.client.exceptions.RefineException;
import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.Iterator;
import org.apache.commons.io.FileUtils;
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
    description = "Converts the data of a project to RDF format.",
    separator = " ")
class ConvertRdf extends Process {

  @Parameters(
      index = "0",
      paramLabel = "PROJECT",
      description = "The project whose data to convert.")
  private String project;

  @Option(
      names = {"-m", "--mapping"},
      description = "The mapping that will be used for the RDF conversion. The file should contain"
          + " JSON configuration. If not provided the process will try to retrieve it from the"
          + " project configurations, if it is defined there.")
  private File mapping;

  @Option(
      names = {"-f", "--format"},
      description = "Controls the format of the result. The default format is '${DEFAULT-VALUE}'."
          + " The allowed values are: ${COMPLETION-CANDIDATES}",
      completionCandidates = AllowedFormatValues.class,
      defaultValue = "turtle")
  private ResultFormat format;

  @Override
  public Integer call() {
    try (RefineClient client = getClient()) {

      ExportRdfResponse response = RefineCommands
          .exportRdf()
          .setProject(project)
          .setMapping(getRdfMapping(client))
          .setFormat(format)
          .build()
          .execute(client);

      System.out.println(response.getResult());
      return ExitCode.OK;
    } catch (RefineException re) {
      System.err.println(re.getMessage());
    } catch (Exception exc) {
      String error = String.format(
          "Failed to execute RDF conversion for project: '%s' due to %s", project,
          exc.getMessage());
      System.err.println(error);
    }
    return ExitCode.SOFTWARE;
  }

  private String getRdfMapping(RefineClient client) throws RefineException {
    if (mapping != null) {
      return readFile();
    }

    GetProjectModelsResponse response =
        RefineCommands.getProjectModels().setProject(project).build().execute(client);

    JsonNode rdfMapping = extractMapping(response);

    if (rdfMapping == null) {
      throw new RefineException("Failed to retrieve the mapping for project: '%s'", project);
    }

    return rdfMapping.toString();
  }

  private JsonNode extractMapping(GetProjectModelsResponse response) {
    JsonNode overlayModels = response.getOverlayModels();
    if (overlayModels == null) {
      return null;
    }

    // there are 2 wrapping object for the mapping...
    JsonNode mappingDef = overlayModels.findValue("mappingDefinition");
    return mappingDef != null ? mappingDef.findValue("mappingDefinition") : null;
  }

  private String readFile() throws RefineException {
    try {
      return FileUtils.readFileToString(mapping, StandardCharsets.UTF_8);
    } catch (IOException ioe) {
      throw new RefineException(
          "Failed to read the mapping from the file: '%s' for project: '%s'",
          mapping.getName(),
          project);
    }
  }

  private static class AllowedFormatValues implements Iterable<String> {

    @Override
    public Iterator<String> iterator() {
      return Arrays.stream(ResultFormat.values())
          .map(value -> value.toString().toLowerCase())
          .iterator();
    }
  }
}
