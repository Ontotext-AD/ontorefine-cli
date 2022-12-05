package com.ontotext.refine.cli.extract;

import java.util.Arrays;
import java.util.Iterator;

/**
 * Provides the allowed extract mode values and completion candidates for the mode argument in
 * extract command.
 *
 * @author Antoniy Kunchev
 */
class AllowedExtractModes implements Iterable<String> {

  @Override
  public Iterator<String> iterator() {
    return Arrays.stream(ExtractMode.values())
        .map(ExtractMode::toString)
        .iterator();
  }
}
