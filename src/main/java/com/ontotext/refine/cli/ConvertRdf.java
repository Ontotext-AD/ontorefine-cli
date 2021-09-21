package com.ontotext.refine.cli;

import com.fasterxml.jackson.databind.JsonNode;
import com.ontotext.refine.client.RefineClient;
import com.ontotext.refine.client.command.RefineCommands;
import com.ontotext.refine.client.command.operations.GetOperationsResponse;
import com.ontotext.refine.client.command.rdf.ExportRdfResponse;
import com.ontotext.refine.client.exceptions.RefineException;
import picocli.CommandLine.Command;
import picocli.CommandLine.ExitCode;
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

  // TODO add mapping as parameter, if missing then try to retrieve one from the operations

  @Override
  public Integer call() {
    try (RefineClient client = getClient()) {

      ExportRdfResponse response = RefineCommands
          .exportRdf()
          .setProject(project)
          .setMapping(getRdfMapping(getClient()).toString())
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

  private JsonNode getRdfMapping(RefineClient client) throws RefineException {
    GetOperationsResponse response =
        RefineCommands.getOperations().setProject(project).build().execute(client);

    JsonNode mapping = response.getContent().findValue("mapping");

    if (mapping == null || mapping.isNull() || mapping.isMissingNode()) {
      throw new RefineException(
          String.format("Failed to retrieve the mapping for project: '%s'", project));
    }

    return mapping;
  }
}
