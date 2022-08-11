package com.ontotext.refine.cli.utils;

import com.ontotext.refine.client.RefineClient;
import com.ontotext.refine.client.command.RefineCommands;
import com.ontotext.refine.client.command.processes.GetProcessesCommand;
import com.ontotext.refine.client.command.processes.GetProcessesCommandResponse.ProjectProcess;
import com.ontotext.refine.client.exceptions.RefineException;
import java.time.Duration;
import java.util.Collection;

/**
 * Utility containing logic related to the refine project data export.
 *
 * @author Antoniy Kunchev
 */
public class ExportUtils {

  private ExportUtils() {
    throw new IllegalStateException("Utility class should not be instantiated.");
  }

  /**
   * Waits for all of the processes for specific project to be completed. The method pulls
   * information about any running processes for the specified project. This will repeat until all
   * processes are completed or some error occurs. The timeout parameter will be used as additional
   * protection mechanism to prevent infinite wait. <br>
   * The status refresh rate is 2 seconds and the timeout after which the waiting will be
   * interrupted is 1 hour.
   *
   * @param project which processes should be awaited
   * @param client to be used for status retrieval
   * @throws RefineException when an error occurs during the waiting process
   */
  public static void awaitProcessesCompletion(String project, RefineClient client)
      throws RefineException {
    awaitProcessesCompletion(project, Duration.ofSeconds(2), Duration.ofHours(1), client);
  }

  /**
   * Waits for all of the processes for specific project to be completed.The method pulls
   * information about any running processes for the specified project. This will repeat until all
   * processes are completed or some error occurs. The timeout parameter will be used as additional
   * protection mechanism to prevent infinite wait.
   *
   * @param project which processes should be awaited
   * @param refreshPeriod the period between the status printing
   * @param timeout the maximum period that should be awaited
   * @param client to be used for status retrieval
   * @throws RefineException when an error occurs during the waiting process
   */
  public static void awaitProcessesCompletion(
      String project, Duration refreshPeriod, Duration timeout, RefineClient client)
      throws RefineException {
    GetProcessesCommand command = RefineCommands.getProcesses().setProject(project).build();
    Collection<ProjectProcess> processes = command.execute(client).getProcesses();
    long interuptTime = System.currentTimeMillis() + timeout.toMillis();
    while (!processes.isEmpty() && interuptTime > System.currentTimeMillis()) {

      sleep(refreshPeriod);

      processes = command.execute(client).getProcesses();
    }
  }

  private static void sleep(Duration refreshPeriod) {
    try {
      Thread.sleep(refreshPeriod.toMillis());
    } catch (InterruptedException ie) { // NOSONAR
      Thread.currentThread().interrupt();
    }
  }
}
