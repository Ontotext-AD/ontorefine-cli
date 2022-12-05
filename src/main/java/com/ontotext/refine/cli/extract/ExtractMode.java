package com.ontotext.refine.cli.extract;

/**
 * Provides the options for the mode argument in extract configurations.
 *
 * @author Antoniy Kunchev
 */
public enum ExtractMode {

  /**
   * Extract only operations history.
   */
  OPERATIONS("operations"),

  /**
   * Extract only the import options.
   */
  IMPORT_OPTIONS("import-options"),

  /**
   * Extract the full set of configurations as single JSON document.
   */
  FULL("full");

  private final String mode;

  private ExtractMode(String mode) {
    this.mode = mode;
  }

  @Override
  public String toString() {
    return mode;
  }
}
