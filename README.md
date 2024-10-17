[![Build](https://github.com/avaje/avaje-spi-service/actions/workflows/build.yml/badge.svg)](https://github.com/avaje/avaje-spi-service/actions/workflows/build.yml)
[![License](https://img.shields.io/badge/License-Apache%202.0-blue.svg)](https://github.com/avaje/avaje-spi-service/blob/master/LICENSE)
[![Maven Central](https://img.shields.io/maven-central/v/io.avaje/avaje-record-builder.svg?label=Maven%20Central)](https://mvnrepository.com/artifact/io.avaje/avaje-record-builder)
[![Discord](https://img.shields.io/discord/1074074312421683250?color=%237289da&label=discord)](https://discord.gg/Qcqf9R27BR)
# avaje-record-builder
Uses Annotation processing to generate builders for records.

## Distinguishing features
- By default, Collection/Optional Types will not be null (an empty collection/optional will be provided)
- We can choose the default value of a record component in the generated builder
- Support for generating Checker/NullAway compliant builders for static null analysis.
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

Add the annotation module as a static dependency when working with Java modules.
```java
module my.module {
  requires static io.avaje.recordbuilder;
}
```
### 2. Add `@RecordBuilder`
```java
@RecordBuilder
public record ArmoredCore(
    @DefaultValue("\"Steel Haze\"") String coreName,
    String model,
    int energyReserve, 
    int ap) {}
```

The following builder class will be generated for the above:
<details>
    <summary>Generated Class, (click to expand)</summary>
<pre content="java">
/**  Builder class for {@link ArmoredCore} */
public class ArmoredCoreBuilder {
  private String coreName = "Steel Haze";
  private String model;
  private int energyReserve;
  private int ap;

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
      this.energyReserve = energyReserve;
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
  </pre>
</details>

## Default Values
Using `@DefaultValue` we can directly write the code to set the default value in the generated builder. This allows us to directly write a value or use static methods to set the default builder state.
```java
@RecordBuilder
public record Defaults(
    @DefaultValue("List.of(1,2,3)") List<Integer> list,
    @DefaultValue("24") int num,
    @DefaultValue("\"string val\"") String str,
    @DefaultValue("CustomClass.createDefault()") CustomClass custom) {}
```

This will generate:
```java
public class DefaultsBuilder {
  private List<Integer> list = List.of(1,2,3);
  private int num = 24;
  private String str = "string val";
  private CustomClass custom = CustomClass.createDefault();

...the rest of the builder
}
```
