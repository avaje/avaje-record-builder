module io.avaje.recordbuilder.core {

  requires java.compiler;
  requires static io.avaje.prism;
  requires static org.jspecify;

  provides javax.annotation.processing.Processor with io.avaje.recordbuilder.internal.RecordProcessor;
}
