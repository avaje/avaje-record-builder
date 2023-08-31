[![Build](https://github.com/avaje/avaje-spi-service/actions/workflows/build.yml/badge.svg)](https://github.com/avaje/avaje-spi-service/actions/workflows/build.yml)
[![License](https://img.shields.io/badge/License-Apache%202.0-blue.svg)](https://github.com/avaje/avaje-spi-service/blob/master/LICENSE)
[![Maven Central : avaje-record-builder](https://maven-badges.herokuapp.com/maven-central/io.avaje/avaje-record-builder/badge.svg)](https://maven-badges.herokuapp.com/maven-central/io.avaje/avaje-record-builder)
[![Discord](https://img.shields.io/discord/1074074312421683250?color=%237289da&label=discord)](https://discord.gg/Qcqf9R27BR)
# avaje-record-builder
Uses Annotation processing to automatically adds `META-INF/services` entries for classes

## Usage
### 1. Add dependency:
```xml
<dependency>
  <groupId>io.avaje</groupId>
  <artifactId>avaje-record-builder</artifactId>
  <version>${record.version}</version>
  <optional>true</optional>
  <scope>provided</scope>
</dependency>
```

When working with Java modules you need to add the annotation module as a static dependency.
```java
module my.module {
  requires static io.avaje.record;
}
```
### 2. Add `@RecordBuilder`

On classes that you'd like registered, put the `@ServiceProvider` annotation. As long as you only have one interface or one superclass, that type is assumed to be the spi interface. So given the example below:
```java
@RecordBuilder
public record ArmoredCore(String coreName, String model, int energyReserve, int ap) {) {}
```

The following class will be generated:
```
/**  Builder class for {@link ArmoredCore} */
public class ArmoredCoreBuilder {
  private String coreName;  // -- java.lang.String
  private String model;  // -- java.lang.String
  private int energyReserve;  // -- int
  private int ap;  // -- int

  private ArmoredCoreBuilder() {
  }

  private ArmoredCoreBuilder(String coreName, String model, int energyReserve, int ap) {
    this.coreName = coreName;
    this.model = model;
    this.energyReserve = energyReserve;
    this.ap = ap;
  }

  /**
   * Return a new builder with all fields set to default Java values
   */
  public static ArmoredCoreBuilder builder() {
      return new ArmoredCoreBuilder();
  }

  /**
   * Return a new builder with all fields set to the values taken from the given record instance
   */
  public static ArmoredCoreBuilder builder(ArmoredCore from) {
      return new ArmoredCoreBuilder(from.coreName(), from.model(), from.energyReserve(), from.ap());
  }

  /**
   * Return a new ArmoredCore instance with all fields set to the current values in this builder
   */
  public ArmoredCore build() {
      return new ArmoredCore(coreName, model, energyReserve, ap);
  }
  /**
   * Set a new value for the {@code coreName} record component in the builder
   */
  public ArmoredCoreBuilder coreName(String coreName) {
      this.coreName = coreName;
      return this;
  }
  /**
   * Set a new value for the {@code model} record component in the builder
   */
  public ArmoredCoreBuilder model(String model) {
      this.model = model;
      return this;
  }
  /**
   * Set a new value for the {@code energyReserve} record component in the builder
   */
  public ArmoredCoreBuilder energyReserve(int energyReserve) {
      this.energyReserv = energyReserv;
      return this;
  }
  /**
   * Set a new value for the {@code ap} record component in the builder
   */
  public ArmoredCoreBuilder ap(int ap) {
      this.ap = ap;
      return this;
  }
}
```

