package com.ontotext.refine.cli;

import java.io.InputStream;
import java.util.Properties;
import picocli.CommandLine;

/**
 * Retrieves the version of the client from the packaged pom.properties file,
 * or Unknown version if the file was not found.
 */
public class VersionProvider implements CommandLine.IVersionProvider {
  @Override
  public String[] getVersion() throws Exception {
    try (InputStream is = VersionProvider.class.getResourceAsStream(
        "/META-INF/maven/com.ontotext/ontorefine-cli/pom.properties")) {
      if (is != null) {
        Properties properties = new Properties();
        properties.load(is);
        String version = properties.getProperty("version");
        if (version != null) {
          return new String[] { "OntoRefine-CLI v" + version };
        }
      }
    }

    return new String[] { "OntoRefine-CLI Unknown version" };
  }
}
