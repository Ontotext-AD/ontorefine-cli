package com.ontotext.refine.cli.export.rdf;

import com.ontotext.refine.client.command.rdf.ResultFormat;

/**
 * Contains values for the result format of the export RDF commands. Basically it is a proxy for the
 * {@link ResultFormat} values with additional filtering of the formats.
 *
 * @author Antoniy Kunchev
 */
public enum RdfResultFormats {

  RDFXML(ResultFormat.RDFXML),

  NTRIPLES(ResultFormat.NTRIPLES),

  TURTLE(ResultFormat.TURTLE),

  TURTLESTAR(ResultFormat.TURTLESTAR),

  TRIX(ResultFormat.TRIX),

  TRIG(ResultFormat.TRIG),

  TRIGSTAR(ResultFormat.TRIGSTAR),

  BINARY(ResultFormat.BINARY),

  NQUADS(ResultFormat.NQUADS),

  JSONLD(ResultFormat.JSONLD),

  RDFJSON(ResultFormat.RDFJSON);

  private final ResultFormat format;

  private RdfResultFormats(ResultFormat format) {
    this.format = format;
  }

  public ResultFormat getFormat() {
    return format;
  }
}
