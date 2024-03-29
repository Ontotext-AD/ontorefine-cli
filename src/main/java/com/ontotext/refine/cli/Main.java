package com.ontotext.refine.cli;

import static com.ontotext.refine.cli.utils.PrintUtils.error;

import com.ontotext.refine.cli.create.CreateProject;
import com.ontotext.refine.cli.export.rdf.ExportRdf;
import com.ontotext.refine.cli.extract.ExtractConfigurations;
import com.ontotext.refine.cli.operations.ApplyOperations;
import com.ontotext.refine.cli.project.aliases.UpdateAliases;
import com.ontotext.refine.cli.transform.Transform;
import picocli.CommandLine;

/**
 * The main entry-point for the ontorefine-cli command. The remaining commands are defined as
 * sub-commands such that the whole appears as a single application. The order of the commands is
 * significant and represents the likely workflow, grouped by functionality.
 */
@CommandLine.Command(
    name = "ontorefine-cli",
    subcommands = {
        CreateProject.class,
        DeleteProject.class,
        Export.class,
        ExtractConfigurations.class,
        ApplyOperations.class,
        RegisterReconService.class,
        ExportRdf.class,
        Transform.class,
        RetrieveVersion.class,
        UpdateAliases.class,
        CommandLine.HelpCommand.class},
    mixinStandardHelpOptions = true,
    separator = " ",
    versionProvider = VersionProvider.class)
class Main implements Runnable {

  @Override
  public void run() {
    // won't be called, this command has sub-commands
  }

  /**
   * Executes the specified command.
   *
   * @param args for the command
   */
  public static void main(String... args) {
    final CommandLine cmd = new CommandLine(new Main()).setCaseInsensitiveEnumValuesAllowed(true);

    if (args.length > 0) {
      System.exit(cmd.execute(args));
    } else {
      // No arguments, complain and show usage
      error("Missing command.");
      cmd.usage(System.err);
      System.exit(CommandLine.ExitCode.USAGE);
    }
  }
}
