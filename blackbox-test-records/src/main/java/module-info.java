import io.avaje.recordbuilder.DefaultInit;
import io.avaje.recordbuilder.test.DamageType;

@DefaultInit.Global(type = DamageType.class, value="DamageType.ENERGY")
module io.avaje.spi.blackbox {
  requires io.avaje.record;
  requires java.compiler;
}
