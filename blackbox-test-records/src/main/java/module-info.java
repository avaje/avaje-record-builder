import org.jspecify.annotations.NullMarked;

@NullMarked
module io.avaje.spi.blackbox {
  requires static io.avaje.recordbuilder;
  requires io.avaje.validation.contraints;
  requires java.compiler;
  requires org.jspecify;
}
