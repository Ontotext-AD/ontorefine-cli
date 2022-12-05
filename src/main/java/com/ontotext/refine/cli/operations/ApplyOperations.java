package com.ontotext.refine.cli.operations;

import static com.ontotext.refine.cli.operations.OperationsUtil.getOperations;
import static com.ontotext.refine.cli.utils.PrintUtils.error;
import static com.ontotext.refine.cli.utils.PrintUtils.info;

import com.ontotext.refine.cli.Process;
import com.ontotext.refine.cli.validation.FileValidator;
import com.ontotext.refine.client.JsonOperation;
import com.ontotext.refine.client.RefineClient;
import com.ontotext.refine.client.ResponseCode;
import com.ontotext.refine.client.command.RefineCommands;
import com.ontotext.refine.client.command.operations.ApplyOperationsResponse;
import com.ontotext.refine.client.exceptions.RefineException;
import java.io.File;
import picocli.CommandLine.Command;
import picocli.CommandLine.ExitCode;
import picocli.CommandLine.Parameters;

/**
 * Defines a apply operations process and all of the required arguments for it.
 *
 * @author Antoniy Kunchev
 */
@Command(
    name = "apply",
    description = "Applies transformation operations to a project.",
    separator = " ",
    sortOptions = false,
    headerHeading = "Usage:%n",
    synopsisHeading = "%n",
    descriptionHeading = "%nDescription:%n",
    parameterListHeading = "%nParameters:%n",
    optionListHeading = "%nOptions:%n",
    mixinStandardHelpOptions = true)
public class ApplyOperations extends Process {

  @Parameters(
      index = "0",
      arity = "1",
      paramLabel = "OPERATIONS",
      description = "The file with the operations that should be applied to the project."
          + " The file should contain JSON.")
  private File operations;

  @Parameters(
      index = "1",
      paramLabel = "PROJECT",
      description = "The identifier of the project to which the transformation"
          + " operations will be applied.")
  private String project;

  @Override
  public Integer call() throws Exception {
    if (FileValidator.doesNotExists(operations, "OPERATIONS")) {
      return ExitCode.USAGE;
    }

    try (RefineClient client = getClient()) {

      ApplyOperationsResponse response = RefineCommands
          .applyOperations()
          .project(project)
          .token(getToken())
          .operations(JsonOperation.from(getOperations(operations)))
          .build()
          .execute(client);

      if (ResponseCode.ERROR.equals(response.getCode())) {
        error(
            "Failed to apply transformation to project '%s' due to: %s",
            project,
            response.getMessage());
        return ExitCode.SOFTWARE;
      }

      info("The transformations were successfully applied to project: %s", project);
      return ExitCode.OK;
    } catch (RefineException re) {
      error(re.getMessage());
    }
    return ExitCode.SOFTWARE;
  }
}
