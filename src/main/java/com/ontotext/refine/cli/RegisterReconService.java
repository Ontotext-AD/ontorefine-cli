package com.ontotext.refine.cli;

import com.ontotext.refine.client.RefineClient;
import com.ontotext.refine.client.ResponseCode;
import com.ontotext.refine.client.command.RefineCommands;
import com.ontotext.refine.client.command.reconcile.ReconServiceRegistrationCommand;
import com.ontotext.refine.client.command.reconcile.ReconServiceRegistrationCommandResponse;
import com.ontotext.refine.client.exceptions.RefineException;
import picocli.CommandLine.Command;
import picocli.CommandLine.ExitCode;
import picocli.CommandLine.Parameters;

/**
 * Defines a registration of additional reconciliation service process and all of the required
 * arguments for it.
 *
 * @author Antoniy Kunchev
 */
@Command(
    name = "register-service",
    description = "Registers an additional reconciliation service.",
    separator = " ")
class RegisterReconService extends Process {

  @Parameters(
      index = "0",
      arity = "1",
      paramLabel = "SERVICE",
      description = "The URL of the additional service that should be registered.")
  private String service;

  @Override
  public Integer call() throws Exception {
    try (RefineClient client = getClient()) {

      ReconServiceRegistrationCommand command = RefineCommands
          .registerReconciliationService()
          .setService(service)
          .setToken(getToken())
          .build();

      ReconServiceRegistrationCommandResponse response = command.execute(client);

      if (ResponseCode.ERROR.equals(response.getCode())) {
        System.err.printf(
            "Failed to register additional reconciliation service: '%s' due to: %s",
            service,
            response.getMessage());
        return ExitCode.SOFTWARE;
      }

      System.out.println("Successfully registered additional reconciliation service: " + service);
      return ExitCode.OK;
    } catch (RefineException re) {
      System.err.println(re.getMessage());
    }
    return ExitCode.SOFTWARE;
  }
}
