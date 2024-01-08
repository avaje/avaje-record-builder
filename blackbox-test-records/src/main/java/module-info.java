import io.avaje.recordbuilder.RecordBuilder;

@RecordBuilder.GlobalConfig(enforceNullSafety = true)
module io.avaje.spi.blackbox {
  requires static io.avaje.recordbuilder;
  requires io.avaje.validation.contraints;
  requires java.compiler;
  requires org.jspecify;
}
