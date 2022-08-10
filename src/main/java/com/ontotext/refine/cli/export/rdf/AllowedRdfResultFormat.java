package com.ontotext.refine.cli.export.rdf;

import com.ontotext.refine.client.command.rdf.ResultFormat;
import java.util.Arrays;
import java.util.Iterator;
import java.util.Set;

/**
 * Provides the values for the allowed RDF result formats and the completion candidates for the RDF
 * export commands.
 *
 * @author Antoniy Kunchev
 */
public class AllowedRdfResultFormat implements Iterable<String> {

  private static final Set<ResultFormat> UNSUPPORTED = Set.of(ResultFormat.HDT, ResultFormat.RDFA);

  @Override
  public Iterator<String> iterator() {
    return Arrays.stream(ResultFormat.values())
        .filter(value -> !UNSUPPORTED.contains(value))
        .map(value -> value.toString().toLowerCase())
        .iterator();
  }
}