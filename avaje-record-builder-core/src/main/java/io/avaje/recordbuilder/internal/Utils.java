package io.avaje.recordbuilder.internal;

public class Utils {
  private Utils() {}

  static String extractTypeWithNest(String fullType) {
    int p = fullType.lastIndexOf('.');
    if (p == -1 || fullType.startsWith("java")) {
      return fullType;
    } else {
      final StringBuilder result = new StringBuilder();
      var foundClass = false;
      var firstClass = true;
      for (final String part : fullType.split("\\.")) {
        if (Character.isUpperCase(part.charAt(0))) {
          foundClass = true;
        }
        result.append(foundClass && !firstClass ? "/" : ".").append(part);
        if (foundClass) {
          firstClass = false;
        }
      }
      if (result.charAt(0) == '.') {
        result.deleteCharAt(0);
      }
      var fullResult = result.toString();

      p = fullResult.lastIndexOf('/');
      if (p == -1) {
        return fullResult;
      }
      return fullResult.substring(0, p);
    }
  }
}
