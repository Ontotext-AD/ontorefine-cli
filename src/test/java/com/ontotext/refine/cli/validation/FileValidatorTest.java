package com.ontotext.refine.cli.validation;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.io.File;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

/**
 * Test for {@link FileValidator}.
 *
 * @author Antoniy Kunchev
 */
class FileValidatorTest {

  @Test
  void existsCase() {
    File file = mock(File.class);
    when(file.exists()).thenReturn(Boolean.TRUE);
    assertTrue(FileValidator.exists(file, "-test-arg", true));
  }

  @ParameterizedTest
  @ValueSource(strings = {"null", "/no"})
  void notExistCases(String value) {
    assertFalse(FileValidator.exists(new File(value), "-test-arg", false));
  }

  @Test
  void nullCases() {
    assertFalse(FileValidator.exists(null, "-test-arg", false));
  }
}
