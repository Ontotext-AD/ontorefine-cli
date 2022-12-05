package com.ontotext.refine.cli.utils;

import com.fasterxml.jackson.databind.JsonNode;
import com.ontotext.refine.client.exceptions.RefineException;
import com.ontotext.refine.client.util.JsonParser;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;

/**
 * Contains convenient methods for processing of JSON data.
 *
 * @author Antoniy Kunchev
 */
public class JsonUtils {

  private JsonUtils() {
    throw new IllegalStateException("Utility class should not be instantiated.");
  }

  /**
   * Parses the input file to a {@link JsonNode}.
   *
   * @param file to parse
   * @return JSON node corresponding to the content of the input file
   * @throws RefineException when there is an error during the parsing or the file does not contain
   *         JSON
   */
  public static JsonNode toJson(File file) throws RefineException {
    try (FileInputStream fis = new FileInputStream(file)) {
      return JsonParser.JSON_PARSER.parseJson(fis);
    } catch (IOException ioe) {
      throw new RefineException("Failed to parse JSON file: '" + file.getName() + "'.", ioe);
    }
  }
}
