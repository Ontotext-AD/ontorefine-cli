package com.ontotext.refine.cli.utils;

import static com.ontotext.refine.cli.project.configurations.ProjectConfigurationsParser.get;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.JsonNodeFactory;
import com.ontotext.refine.cli.export.rdf.RdfResultFormats;
import com.ontotext.refine.cli.project.configurations.ProjectConfigurationsParser;
import com.ontotext.refine.cli.project.configurations.ProjectConfigurationsParser.Configuration;
import com.ontotext.refine.client.RefineClient;
import com.ontotext.refine.client.command.RefineCommands;
import com.ontotext.refine.client.command.models.GetProjectModelsResponse;
import com.ontotext.refine.client.exceptions.RefineException;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import org.apache.commons.io.FileUtils;

/**
 * Utility containing logic related to export of refine project data in RDF format.
 *
 * @author Antoniy Kunchev
 */
public class RdfExportUtils {

  private RdfExportUtils() {
    throw new IllegalStateException("Utility class should not be instantiated.");
  }

  /**
   * Exports the data of given refine project using the provided mapping. The mapping can be either
   * JSON containing operation for the mapping, direct mapping or SPARQL CONSTRUCT query.
   *
   * @param project the identifier of the project which data should be exported
   * @param mappingFile an file that contains the mapping that should be used in the transformation
   * @param resultFormat the RDF format of the result that should be returned
   * @param using provides information which type of mapping to be used
   * @param client to be used for export request
   * @return stream containing the exported data
   * @throws IOException when error occurs during the export execution
   */
  public static InputStream export(
      String project,
      File mappingFile,
      RdfResultFormats resultFormat,
      Using using,
      RefineClient client)
      throws IOException {
    switch (using) {
      case SPARQL:
        return sparqlExport(project, mappingFile, resultFormat, client);
      case MAPPING:
        return mappingExport(project, mappingFile, resultFormat, client);
      default:
        throw new UnsupportedOperationException(
            "The '" + using + "' method for data RDFization is not supported.");
    }
  }

  private static InputStream sparqlExport(
      String project,
      File mappingFile,
      RdfResultFormats format,
      RefineClient client) throws IOException {
    return RefineCommands
        .exportAsRdf()
        .setProject(project)
        .setQuery(readFile(mappingFile))
        .setFormat(format.getFormat())
        .build()
        .execute(client)
        .getResultStream();
  }

  private static String readFile(File mapping) throws RefineException {
    try {
      return FileUtils.readFileToString(mapping, StandardCharsets.UTF_8);
    } catch (IOException ioe) {
      throw new RefineException("Failed to read the mapping from file: '%s'.", mapping.getName());
    }
  }

  private static InputStream mappingExport(
      String project,
      File mappingFile,
      RdfResultFormats format,
      RefineClient client)
      throws IOException {
    return RefineCommands
        .exportRdf()
        .setProject(project)
        .setMapping(getRdfMapping(mappingFile, project, client))
        .setFormat(format.getFormat())
        .build()
        .execute(client)
        .getResultStream();
  }

  private static String getRdfMapping(File mappingFile, String project, RefineClient client)
      throws IOException {
    if (mappingFile != null) {
      JsonNode json = JsonUtils.toJson(mappingFile);
      if (ProjectConfigurationsParser.isProjectConfiguration(json)) {
        return get(json, Configuration.OPERATIONS)
            .orElseGet(JsonNodeFactory.instance::objectNode)
            .toString();
      }

      return readFile(mappingFile);
    }

    // if there is no mapping file, try to extract it from the model
    GetProjectModelsResponse response =
        RefineCommands.getProjectModels().setProject(project).build().execute(client);

    // tiny hack that allows for the logic in the client library to handle the rest
    return JsonNodeFactory.instance.objectNode()
        .set("overlayModels", response.getOverlayModels())
        .toString();
  }

  /**
   * Provides the methods for RDFization of the data for the export.
   *
   * @author Antoniy Kunchev
   */
  public enum Using {
    SPARQL, MAPPING
  }
}
