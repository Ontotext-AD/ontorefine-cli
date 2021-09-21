package com.ontotext.refine.cli;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;
import picocli.CommandLine;

/**
 * Created by Pavel Mihaylov on 21/09/2021.
 */
class MainTest extends BaseProcessTest {
  @Override
  protected Class<?> command() {
    return null;
  }

  @Override
  // Test not applicable to main command
  protected void shouldFailOnMissingRefineUrl() {
  }

  @Test
  @ExpectSystemExit(CommandLine.ExitCode.USAGE)
  void shouldShowMissingCommandUsage() {
    try {
      commandExecutor().accept(args());
    } finally {
      consoleOut.println(consoleErrors());
      assertTrue(consoleErrors().contains("Missing command."));
      assertTrue(consoleErrors().contains("Usage: ontorefine-cli [-hV] [COMMAND]"));
      assertTrue(consoleOutput().isEmpty());
    }
  }

  @Test
  @ExpectSystemExit(CommandLine.ExitCode.OK)
  void shouldShowUsageOnHyphenH() {
    try {
      commandExecutor().accept(args("-h"));
    } finally {
      consoleOut.println(consoleOutput());
      assertFalse(consoleOutput().contains("Missing command."));
      assertTrue(consoleOutput().contains("Usage: ontorefine-cli [-hV] [COMMAND]"));
      assertTrue(consoleErrors().isEmpty());
    }
  }

  @Test
  @ExpectSystemExit(CommandLine.ExitCode.OK)
  void shouldShowUsageOnHelp() {
    try {
      commandExecutor().accept(args("-h"));
    } finally {
      consoleOut.println(consoleOutput());
      assertFalse(consoleOutput().contains("Missing command."));
      assertTrue(consoleOutput().contains("Usage: ontorefine-cli [-hV] [COMMAND]"));
      assertTrue(consoleErrors().isEmpty());
    }
  }
}
