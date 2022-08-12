package com.ontotext.refine.cli;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.ontotext.refine.cli.test.support.ExpectedSystemExit;
import org.junit.jupiter.api.Test;
import picocli.CommandLine.ExitCode;

/**
 * Created by Pavel Mihaylov on 21/09/2021.
 */
class MainTest extends BaseProcessTest {

  @Override
  protected Class<?> command() {
    return null;
  }

  @Override
  protected void shouldFailOnMissingRefineUrl() {
    // not applicable to main command
  }
  
  @Override
  protected void shouldMatchCommandHelpInDocumentation() {
    // not applicable to main command
  }

  @Test
  @ExpectedSystemExit(ExitCode.USAGE)
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
  @ExpectedSystemExit(ExitCode.OK)
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
  @ExpectedSystemExit(ExitCode.OK)
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
