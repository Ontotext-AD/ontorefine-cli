package com.ontotext.refine.cli.export.rdf;

import com.ontotext.refine.client.command.rdf.ResultFormat;
import java.util.Arrays;
import java.util.Iterator;

/**
 * Provides the values for the allowed RDF result formats and the completion candidates for the RDF
 * export commands.
 *
 * @author Antoniy Kunchev
 */
public class AllowedRdfResultFormat implements Iterable<String> {

  @Override
  public Iterator<String> iterator() {
    return Arrays.stream(ResultFormat.values())
        .map(value -> value.toString().toLowerCase())
        .iterator();
  }
}