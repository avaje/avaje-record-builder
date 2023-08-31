package io.avaje.recordbuilder.internal;

import static java.util.stream.Collectors.toSet;

import java.io.BufferedReader;
import java.io.IOException;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.regex.Pattern;

public final class ModuleReader {

  private static final Pattern regex = Pattern.compile("provides\\s+(.*?)\\s+with");

  /** Keeps Track of found services by SPI and implementation set */
  private final Map<String, Set<String>> foundServices = new HashMap<>();

  private final Map<String, Set<String>> missingServicesMap = new HashMap<>();

  private boolean staticWarning;
  private boolean inProvides = false;

  private static boolean coreWarning;

  public ModuleReader(Map<String, Set<String>> services) {
    services.forEach(this::add);
  }

  private void add(String k, Set<String> v) {
    missingServicesMap.put(
        ProcessorUtils.shortType(k).replace("$", "."),
        v.stream().map(ProcessorUtils::shortType).collect(toSet()));
  }

  public void read(BufferedReader reader) throws IOException {
    String line;
    while ((line = reader.readLine()) != null) {
      // retrieve service from provides statement
      readLine(line);
    }
  }

  void readLine(String line) {
    String service = null;
    if (line.contains("provides")) {
      inProvides = true;
      final var matcher = regex.matcher(line);
      if (matcher.find()) {
        service = ProcessorUtils.shortType(matcher.group(1)).replace("$", ".");
      }
    }

    // if not part of a provides statement skip
    if (!inProvides || line.isBlank()) {
      if (!staticWarning && line.contains("io.avaje.recordbuilder") && !line.contains("static")) {
        staticWarning = true;
      }
      if (line.contains("io.avaje.recordbuilder.core")) {
        coreWarning = true;
      }
      return;
    }

    processLine(line, service);

    //  provides statement has ended
    if (line.contains(";")) {
      inProvides = false;
    }
  }

  /** as service implementations are discovered, remove from missing strings map */
  private void processLine(String line, String service) {
    final Set<String> missingServiceImpls = missingServicesMap.get(service);
    final Set<String> foundServiceImpls =
        foundServices.computeIfAbsent(service, k -> new HashSet<>());
    if (!foundServiceImpls.containsAll(missingServiceImpls)) {
      parseServices(line, missingServiceImpls, foundServiceImpls);
    }
    missingServiceImpls.removeAll(foundServiceImpls);
  }

  /**
   * as service implementations are discovered, add to found strings set for a given service
   *
   * @param input the line to check
   * @param missingServiceImpls the services we're looking for
   * @param foundServiceImpls where we'll store the results if we have a match
   */
  private static void parseServices(
      String input, Set<String> missingServiceImpls, Set<String> foundServiceImpls) {

    for (final var impl : missingServiceImpls) {
      if (input.contains(impl)) {
        foundServiceImpls.add(impl);
      }
    }
  }

  public boolean staticWarning() {
    return staticWarning;
  }

  public boolean coreWarning() {
    return coreWarning;
  }

  public Map<String, Set<String>> missing() {
    return missingServicesMap;
  }
}
