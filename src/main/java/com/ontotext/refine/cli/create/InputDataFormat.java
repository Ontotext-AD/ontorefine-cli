package com.ontotext.refine.cli.create;

import com.ontotext.refine.client.UploadFormat;

/**
 * Provides human friendly representation of the values for the input data formats of the create
 * command.
 *
 * @author Antoniy Kunchev
 */
public enum InputDataFormat {

  CSV(UploadFormat.SEPARATOR_BASED),

  TSV(UploadFormat.SEPARATOR_BASED),

  EXCEL(UploadFormat.EXCEL),

  JSON(UploadFormat.JSON),

  XML(UploadFormat.XML);

  // TODO: more for the next releases

  private final UploadFormat uploadFormat;

  private InputDataFormat(UploadFormat uploadFormat) {
    this.uploadFormat = uploadFormat;
  }

  /**
   * Retrieves the {@link UploadFormat} value from the current format.
   *
   * @return upload format
   */
  public UploadFormat toUploadFormat() {
    return uploadFormat;
  }
}
