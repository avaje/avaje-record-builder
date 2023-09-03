package io.avaje.recordbuilder.internal;

import java.util.Arrays;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.StringJoiner;

import javax.lang.model.type.TypeMirror;

interface UType {

  /** Create the UType from the given TypeMirror. */
  static UType parse(TypeMirror returnType) {

    return parse(returnType.toString());
  }

  /** Create the UType from the given String. */
  static UType parse(String rawType) {
    final var type = ProcessorUtils.trimAnnotations(rawType);
    final int pos = type.indexOf('<');
    if (pos == -1) {
      return new UType.Basic(type);
    }
    return new UType.Generic(type);
  }

  /** Return the import types. */
  Set<String> importTypes();

  /** Return the short name. */
  String shortType();

  /** Return the main type (outer most type). */
  String mainType();

  /** Return the first generic parameter. */
  default String param0() {
    return null;
  }

  /** Return the second generic parameter. */
  default String param1() {
    return null;
  }

  /** Return the raw generic parameter if this UType is a Collection. */
  default UType paramRaw() {
    return null;
  }

  /** Return the raw type. */
  String full();

  default boolean isGeneric() {
    return false;
  }

  default String genericParams() {
    return "";
  }

  /** Simple non-generic type. */
  class Basic implements UType {
    final String rawType;

    Basic(String rawType) {
      this.rawType = rawType;
    }

    @Override
    public String full() {
      return rawType;
    }

    @Override
    public Set<String> importTypes() {
      return rawType.startsWith("java.lang.") && rawType.indexOf('.') > -1
          ? Set.of()
          : Collections.singleton(rawType.replace("[]", ""));
    }

    @Override
    public String shortType() {
      return ProcessorUtils.shortType(rawType);
    }

    @Override
    public String mainType() {
      return rawType;
    }

    @Override
    public String toString() {
      return rawType;
    }
  }

  /** Generic type. */
  class Generic implements UType {
    final String rawType;
    final List<String> allTypes;
    final String shortRawType;

    Generic(String rawTypeInput) {
      this.rawType = rawTypeInput.replace(" ", ""); // trim whitespace
      this.allTypes = Arrays.asList(rawType.split("[<|>|,]"));
      this.shortRawType = shortRawType(rawType, allTypes);
    }

    private String shortRawType(String rawType, List<String> allTypes) {
      final Map<String, String> typeMap = new LinkedHashMap<>();
      for (final String val : allTypes) {
        typeMap.put(val, ProcessorUtils.shortType(val));
      }
      String shortRaw = rawType;
      for (final Map.Entry<String, String> entry : typeMap.entrySet()) {
        shortRaw = shortRaw.replace(entry.getKey(), entry.getValue());
      }
      return shortRaw;
    }

    @Override
    public String full() {
      return rawType;
    }

    @Override
    public String toString() {
      return rawType;
    }

    @Override
    public Set<String> importTypes() {
      final Set<String> set = new LinkedHashSet<>();
      for (final String type : allTypes) {
        if (!type.startsWith("java.lang.") && type.indexOf('.') > -1) {
          if (type.startsWith("java")) {
            set.add(type.replace("[]", "").replace("?extends", ""));
          } else {
            set.add(innerTypesImport(type).replace("[]", "").replace("?extends", ""));
          }
        }
      }
      set.remove("?");
      return set;
    }

    public String innerTypesImport(String type) {
      final var parts = type.split("\\.");
      var result = "";
      var foundUpper = false;

      for (var i = 0; i < parts.length; i++) {
        if (!Character.isUpperCase(parts[i].charAt(0))) {
          result += parts[i] + ".";
        } else if (!foundUpper) {
          foundUpper = true;
          result += parts[i] + (i == parts.length - 1 ? "" : ".");
        } else {
          break;
        }
      }

      if (result.endsWith(".")) {
        result = result.substring(0, result.length() - 1);
      }
      return result;
    }

    @Override
    public boolean isGeneric() {
      return true;
    }

    @Override
    public String genericParams() {
      final StringJoiner joiner = new StringJoiner(",");
      for (final String type : allTypes) {
        if (type.indexOf('.') == -1) {
          joiner.add(type);
        }
      }
      final String commaDelim = joiner.toString();
      return commaDelim.isEmpty() ? "" : "<" + commaDelim + "> ";
    }

    @Override
    public String shortType() {
      return shortRawType;
    }

    @Override
    public String mainType() {
      return allTypes.isEmpty() ? null : allTypes.get(0);
    }

    @Override
    public String param0() {
      return allTypes.size() < 2 ? null : allTypes.get(1);
    }

    @Override
    public String param1() {
      return allTypes.size() < 3 ? null : allTypes.get(2);
    }
  }
}
