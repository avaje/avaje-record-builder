package io.avaje.spi.test;

import static org.assertj.core.api.Assertions.assertThat;

import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.Collections;
import java.util.Set;

import javax.tools.JavaFileObject;
import javax.tools.JavaFileObject.Kind;
import javax.tools.StandardLocation;
import javax.tools.ToolProvider;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;

import io.avaje.recordbuilder.internal.RecordProcessor;

class ServiceProcessorTest {

  @AfterEach
  void deleteGeneratedFiles() throws IOException {
    Paths.get("io.avaje.recordbuilder.test.SPIInterface").toAbsolutePath().toFile().delete();
    Paths.get(" io.avaje.recordbuilder.test.SPIInterface$NestedSPIInterface").toAbsolutePath().toFile().delete();
  }

  @Test
  void runAnnotationProcessor() throws Exception {
    final var source = Paths.get("src/main/java/io/").toAbsolutePath().toString();

    final Iterable files = getSourceFiles(source);

    final var compiler = ToolProvider.getSystemJavaCompiler();

    final var task =
        compiler.getTask(new PrintWriter(System.out), null, null, Arrays.asList(), null, files);
    task.setProcessors(Arrays.asList(new RecordProcessor()));

    assertThat(task.call()).isTrue();

    assertThat(Paths.get("io.avaje.recordbuilder.test.SPIInterface").toAbsolutePath().toFile().exists()).isTrue();
    assertThat(
            Paths.get("io.avaje.recordbuilder.test.SPIInterface$NestedSPIInterface")
                .toAbsolutePath()
                .toFile()
                .exists())
        .isTrue();
  }

  private Iterable<JavaFileObject> getSourceFiles(String source) throws Exception {
    final var compiler = ToolProvider.getSystemJavaCompiler();
    final var files = compiler.getStandardFileManager(null, null, null);

    files.setLocation(StandardLocation.SOURCE_PATH, Arrays.asList(new File(source)));

    final Set<Kind> fileKinds = Collections.singleton(Kind.SOURCE);
    return files.list(StandardLocation.SOURCE_PATH, "", fileKinds, true);
  }
}
