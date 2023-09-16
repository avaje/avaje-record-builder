module io.avaje.recordbuilder.core {

  requires java.compiler;
  requires static io.avaje.prism;

  provides javax.annotation.processing.Processor with io.avaje.recordbuilder.internal.RecordProcessor;
}
