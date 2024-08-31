import io.jstach.jstache.JStacheConfig;
import io.jstach.jstache.JStacheType;

@JStacheConfig(type = JStacheType.STACHE)
module io.avaje.recordbuilder.core {

  requires java.compiler;
  requires static io.avaje.prism;
  requires static org.jspecify;
  requires static io.jstach.jstache;

  provides javax.annotation.processing.Processor with io.avaje.recordbuilder.internal.RecordProcessor;
}
