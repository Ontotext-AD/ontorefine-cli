package com.ontotext.refine.cli;

import com.ontotext.refine.client.RefineClient;
import com.ontotext.refine.client.command.RefineCommands;
import com.ontotext.refine.client.command.version.GetVersionResponse;
import com.ontotext.refine.client.exceptions.RefineException;
import picocli.CommandLine.Command;
import picocli.CommandLine.ExitCode;


/**
 * Defines the version retrieval process.
 *
 * @author Antoniy Kunchev
 */
@Command(
    name = "version",
    description = "Retrieves the version of the OntoRefine instance.",
    separator = " ",
    hidden = true)
class RetrieveVersion extends Process {

  @Override
  public Integer call() throws Exception {
    try (RefineClient client = getClient()) {
      GetVersionResponse response = RefineCommands.getVersion().build().execute(client);

      System.out.println("Name: " + response.getFullName());
      System.out.println("Full version: " + response.getFullVersion());
      System.out.println("Version: " + response.getVersion());
      System.out.println("Revision: " + response.getRevision());
      return ExitCode.OK;
    } catch (RefineException re) {
      System.err.println(re.getMessage());
    }
    return ExitCode.SOFTWARE;
  }
}
