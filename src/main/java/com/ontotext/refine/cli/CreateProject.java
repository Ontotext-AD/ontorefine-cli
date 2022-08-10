package com.ontotext.refine.cli;

import com.ontotext.refine.cli.validation.FileValidator;
import com.ontotext.refine.client.RefineClient;
import com.ontotext.refine.client.UploadFormat;
import com.ontotext.refine.client.command.RefineCommands;
import com.ontotext.refine.client.command.create.CreateProjectResponse;
import com.ontotext.refine.client.exceptions.RefineException;
import java.io.File;
import java.util.Arrays;
import java.util.Iterator;
import org.apache.commons.lang3.StringUtils;
import picocli.CommandLine.Command;
import picocli.CommandLine.ExitCode;
import picocli.CommandLine.Option;
import picocli.CommandLine.Parameters;

/**
 * Defines the create project process and all of the required arguments for it.
 *
 * @author Antoniy Kunchev
 */
@Command(
    name = "create",
    description = "Creates a new project from a file.",
    separator = " ")
class CreateProject extends Process {

  @Parameters(
      index = "0",
      paramLabel = "FILE",
      description = "The file that will be used to create the project."
          + " It should be a full name with one of the supported extensions"
          + " (csv).") // tsv, xml, json, txt, xls, xlsx, ods
  private File file;

  @Option(
      names = {"-n", "--name"},
      description = "The name of the OntoRefine project to create. If not provided,"
          + " the file name will be used.")
  private String name;

  @Option(
      names = {"-f", "--format"},
      description = "The format of the provided file. The default format is '${DEFAULT-VALUE}'."
          + " The allowed values are: ${COMPLETION-CANDIDATES}",
      completionCandidates = AllowedInputDataFormats.class,
      defaultValue = "csv")
  private InputDataFormat format;

  // TODO we need additional argument for the options for the project
  // the options are metadata for the project and the file. For some files it is required to provide
  // an initial parsing point to the refine tool. Example JSON, XML, etc.

  @Override
  public Integer call() throws Exception {
    if (FileValidator.doesNotExists(file, "FILE")) {
      return ExitCode.USAGE;
    }

    try (RefineClient client = getClient()) {

      CreateProjectResponse response = RefineCommands
          .createProject()
          .file(file)
          .format(format.toUploadFormat())
          .name(StringUtils.defaultIfBlank(name, file.getName()))
          .token(getToken())
          .build()
          .execute(client);

      String message = String.format(
          "The project with id '%s' was created successfully.", response.getProjectId());
      System.out.println(message);
      return ExitCode.OK;
    } catch (RefineException re) {
      System.err.println(re.getMessage());
    }
    return ExitCode.SOFTWARE;
  }

  /**
   * Provides human friendly representation of the values for the input data formats.
   *
   * @author Antoniy Kunchev
   */
  private enum InputDataFormat {

    CSV(UploadFormat.SEPARATOR_BASED);

    // TODO: more for the next releases

    private final UploadFormat uploadFormat;

    private InputDataFormat(UploadFormat uploadFormat) {
      this.uploadFormat = uploadFormat;
    }

    private UploadFormat toUploadFormat() {
      return uploadFormat;
    }
  }

  /**
   * Provides the allowed input data formats and completion candidates for the format argument.
   *
   * @author Antoniy Kunchev
   */
  private static class AllowedInputDataFormats implements Iterable<String> {

    @Override
    public Iterator<String> iterator() {
      return Arrays.stream(InputDataFormat.values())
          .map(value -> value.toString().toLowerCase())
          .iterator();
    }
  }
}
