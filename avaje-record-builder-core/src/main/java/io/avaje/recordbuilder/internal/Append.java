package io.avaje.recordbuilder.internal;

import java.io.IOException;
import java.io.UncheckedIOException;
import java.io.Writer;

/** Helper that wraps a writer with some useful methods to append content. */
final class Append implements AutoCloseable {

  private final Writer writer;

  Append(Writer writer) {
    this.writer = writer;
  }

  Append append(String content) {
    try {
      writer.append(content);
      return this;
    } catch (final IOException e) {
      throw new UncheckedIOException(e);
    }
  }

  @Override
  public void close() {
    try {
      writer.flush();
      writer.close();
    } catch (final IOException e) {
      throw new UncheckedIOException(e);
    }
  }

  Append eol() {
    try {
      writer.append("\n");
      return this;
    } catch (final IOException e) {
      throw new RuntimeException(e);
    }
  }

  /** Append content with formatted arguments. */
  Append append(String format, Object... args) {
    return append(String.format(format, args));
  }
}
