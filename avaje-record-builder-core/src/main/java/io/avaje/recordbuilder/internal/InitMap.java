package io.avaje.recordbuilder.internal;

import java.util.HashMap;
import java.util.Map;

public class InitMap {
  private static final Map<String, String> defaultsMap = new HashMap<>();

  static {
    // TODO add the rest of the collections
    final var util = "java.util.";
    final var initDiamond = "new java.util.%s<>()";
    // optional
    put(util + "Optional", util + "Optional.empty()");
    put(util + "OptionalInt", util + "OptionalInt.empty()");
    put(util + "OptionalDouble", util + "OptionalDouble.empty()");
    put(util + "OptionalLong", util + "OptionalLong.empty()");

    put(util + "Collection", initDiamond.formatted("ArrayList"));
    put(util + "SequencedCollection", initDiamond.formatted("ArrayList"));
    // list
    put(util + "List", initDiamond.formatted("ArrayList"));
    put(util + "ArrayList", initDiamond.formatted("ArrayList"));
    put(util + "LinkedList", initDiamond.formatted("LinkedList"));
    // set
    put(util + "Set", initDiamond.formatted("HashSet"));
    put(util + "SequencedSet", initDiamond.formatted("LinkedHashSet"));
    put(util + "HashSet", initDiamond.formatted("HashSet"));
    put(util + "TreeSet", initDiamond.formatted("TreeSet"));
    put(util + "SortedSet", initDiamond.formatted("TreeSet"));
    put(util + "NavigableSet", initDiamond.formatted("TreeSet"));
    put(util + "LinkedHashSet", initDiamond.formatted("LinkedHashSet"));
    // map
    put(util + "Map", initDiamond.formatted("HashMap"));
    put(util + "SequencedMap", initDiamond.formatted("LinkedHashMap"));
    put(util + "HashMap", initDiamond.formatted("HashMap"));
    put(util + "LinkedHashMap", initDiamond.formatted("LinkedHashMap"));
    put(util + "TreeMap", initDiamond.formatted("TreeMap"));
    put(util + "SortedMap", initDiamond.formatted("TreeMap"));
    put(util + "NavigableMap", initDiamond.formatted("TreeMap"));

    // queue
    put(util + "Queue", initDiamond.formatted("LinkedList"));

    // deque

    put(util + "Deque", initDiamond.formatted("ArrayDeque"));
    put(util + "ArrayDeque", initDiamond.formatted("ArrayDeque"));

  }

  static void put(String key, String value) {

    defaultsMap.put(key, value);
  }

  static String get(String key) {
    return defaultsMap.get(key);
  }

  public static void putAll(Map<String, String> map) {
    defaultsMap.putAll(map);
  }
}
