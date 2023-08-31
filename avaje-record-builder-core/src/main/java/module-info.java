module io.avaje.record.core {

  requires java.compiler;
  requires static io.avaje.prism;

  provides javax.annotation.processing.Processor with io.avaje.recordbuilder.internal.RecordProcessor;
}
