package com.ontotext.refine.cli;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.ontotext.refine.cli.test.support.ExpectedSystemExit;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.PrintStream;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.function.Consumer;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringUtils;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import picocli.CommandLine;
import picocli.CommandLine.ExitCode;

/**
 * Provides common logic that could be used throughout the CLI tests.
 *
 * @author Antoniy Kunchev
 */
public abstract class BaseProcessTest {

  private ByteArrayOutputStream outputOs;
  private String consoleOutputAsStr;

  private ByteArrayOutputStream errorsOs;
  private String errorsAsStr;

  protected final PrintStream consoleOut = System.out;
  protected final PrintStream consoleErr = System.err;

  protected static final String PROJECT_ID = "1812661014997";

  protected final Consumer<String[]> commandExecutor() {
    return cmdArgs -> {
      List<String> args = new ArrayList<>(cmdArgs.length + 1);
      Class<?> commandClass = command();
      if (commandClass != null) {
        args.add(commandClass.getAnnotation(CommandLine.Command.class).name());
      }
      args.addAll(Arrays.asList(cmdArgs));
      Main.main(args.toArray(String[]::new));
    };
  }

  /**
   * Provides the class for the commands. Used for base tests defined in the current class.
   * The command name will be extracted from the @Command annotation and passed to Main::main.
   *
   * @return the command class
   */
  protected abstract Class<?> command();

  @BeforeEach
  void init() {
    outputOs = new ByteArrayOutputStream();
    System.setOut(new PrintStream(outputOs));

    errorsOs = new ByteArrayOutputStream();
    System.setErr(new PrintStream(errorsOs));
  }

  @AfterEach
  void resetToOriginal() {
    System.setOut(consoleOut);
    System.setErr(consoleErr);
  }

  @Test
  @ExpectedSystemExit(ExitCode.USAGE)
  protected void shouldFailOnMissingRefineUrl() {
    try {
      commandExecutor().accept(new String[0]);
    } finally {
      assertTrue(assertMissingArgError().contains("'--url <url>'"));
    }
  }

  @Test
  @ExpectedSystemExit(ExitCode.OK)
  protected void shouldMatchCommandHelpInDocumentation() {
    try {
      commandExecutor().accept(args("-h"));
    } finally {
      assertEquals(loadAndNormalizeCommandHelp(), consoleOutput());
    }
  }

  private String loadAndNormalizeCommandHelp() {
    String command = command().getAnnotation(CommandLine.Command.class).name();
    try {
      File doc = new File("commands/" + command + ".md");
      assertTrue(doc.exists(), "Expected the file " + doc.getAbsolutePath() + " to exist");
      return trimToCommandHelp(IOUtils.toString(doc.toURI(), StandardCharsets.UTF_8));
    } catch (IOException ioe) {
      throw new AssertionError("Failed to load the documentation for command: " + command, ioe);
    }
  }

  private String trimToCommandHelp(String value) {
    return StringUtils.substringBetween(value, "```bash", "```").stripLeading();
  }

  /**
   * Asserts that the first line of the console error output is for missing arguments and then
   * return it.
   *
   * @return the first line of the console error output
   */
  protected String assertMissingArgError() {
    String[] terms = {"options", "option", "parameters", "parameter", "arguments", "argument"};

    String firstLine = consoleErrors().split(System.lineSeparator())[0];
    boolean condition =
        firstLine.contains("Missing required") && StringUtils.containsAny(firstLine, terms);
    assertTrue(condition, "Expected to find missing arg line but it was <" + firstLine + ">");
    return firstLine;
  }

  /** Varargs converter. */
  protected String[] args(String... args) {
    return args;
  }

  /**
   * Provides the console output as {@link String}.
   *
   * @return the console output
   */
  protected final String consoleOutput() {
    if (consoleOutputAsStr == null) {
      consoleOutputAsStr = new String(outputOs.toByteArray(), StandardCharsets.UTF_8);
    }
    return consoleOutputAsStr;
  }

  /**
   * Provides the console errors output as {@link String}.
   *
   * @return the console errors output
   */
  protected final String consoleErrors() {
    if (errorsAsStr == null) {
      errorsAsStr = new String(errorsOs.toByteArray(), StandardCharsets.UTF_8);
    }
    return errorsAsStr;
  }
}
