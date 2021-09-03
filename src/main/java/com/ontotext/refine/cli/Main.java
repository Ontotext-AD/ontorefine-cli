package com.ontotext.refine.cli;

import picocli.CommandLine;

/**
 * The main entry-point for the ontorefine-cli command. The remaining commands
 * are defined as subcommands such that the whole appears as a single application.
 * The order of the commands is significant and represents the likely workflow,
 * grouped by functionality.
 */
@CommandLine.Command(name = "ontorefine-cli",
    subcommands = { CreateProject.class, DeleteProject.class, Export.class,
        ExtractOperations.class, ApplyOperations.class, Reconcile.class, RegisterReconService.class,
        ConvertRdf.class, RetrieveVersion.class, CommandLine.HelpCommand.class,},
    mixinStandardHelpOptions = true,
    separator = " ",
    versionProvider = VersionProvider.class)
class Main implements Runnable {

  @Override
  public void run() {
    // won't be called, this command has subcommands
  }

  public static void main(String... args) {
    final CommandLine cmd = new CommandLine(new Main());

    if (args.length > 0) {
      System.exit(cmd.execute(args));
    } else {
      // No arguments, complain and show usage
      System.err.println("Missing command.");
      cmd.usage(System.err);
      System.exit(CommandLine.ExitCode.USAGE);
    }
  }
}
