package com.ontotext.refine.cli;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.io.File;
import java.net.URL;
import org.junit.jupiter.api.Test;

/**
 * Simple unit test for {@link VersionProvider}.
 *
 * @author Antoniy Kunchev
 */
class VersionProviderTest {

  private VersionProvider provider = new VersionProvider();

  @Test
  void shouldPrintUnknownCliVersion() throws Exception {
    // renames the actual properties file so that the provider fails to load it
    URL metaDir = getClass().getResource("/META-INF/maven/com.ontotext/ontorefine-cli/");
    File properties = new File(metaDir.getPath(), "pom.properties");
    File unknownProps = new File(metaDir.getPath(), "unknown-pom.properties");
    properties.renameTo(unknownProps);

    try {
      String expected = "OntoRefine-CLI Unknown version";
      String actual = provider.getVersion()[0];
      assertEquals(expected, actual);
    } finally {
      // reverts the original file name
      unknownProps.renameTo(properties);
    }
  }

  @Test
  void shouldPrintCliVersion() throws Exception {
    String expected = "OntoRefine-CLI v9999-test";
    String actual = provider.getVersion()[0];
    assertEquals(expected, actual);
  }
}
