package com.ontotext.refine.cli.create;

import java.util.Arrays;
import java.util.Iterator;

/**
 * Provides the allowed input data formats and completion candidates for the format argument in
 * create command.
 *
 * @author Antoniy Kunchev
 */
public class AllowedInputDataFormats implements Iterable<String> {

  @Override
  public Iterator<String> iterator() {
    return Arrays.stream(InputDataFormat.values())
        .map(value -> value.toString().toLowerCase())
        .iterator();
  }
}
