package io.avaje.recordbuilder.internal;

import java.io.IOException;
import java.io.UncheckedIOException;
import java.io.Writer;

/** Helper that wraps a writer with some useful methods to append content. */
public class Append implements AutoCloseable {

  private final Writer writer;

  public Append(Writer writer) {
    this.writer = writer;
  }

  public Append append(String content) {
    try {
      writer.append(content);
      return this;
    } catch (IOException e) {
      throw new UncheckedIOException(e);
    }
  }

  @Override
  public void close() {
    try {
      writer.flush();
      writer.close();
    } catch (IOException e) {
      throw new UncheckedIOException(e);
    }
  }

  public Append eol() {
    try {
      writer.append("\n");
      return this;
    } catch (IOException e) {
      throw new RuntimeException(e);
    }
  }

  /** Append content with formatted arguments. */
  public Append append(String format, Object... args) {
    return append(String.format(format, args));
  }
}
