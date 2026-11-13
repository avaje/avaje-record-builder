package io.avaje.recordbuilder.staged;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.junit.jupiter.api.Test;

import io.avaje.recordbuilder.test.Address;

class StagedTest {

  private final Address address = new Address("1", "2", "city", "country");

  @Test
  void allStagesInOrder() {
    StagedBuilder.NameStage first = StagedBuilder.builder();
    Staged staged =
        first
            .name("fred")
            .age(42)
            .nickname(null)
            .motto(Optional.of("go"))
            .addTags("a")
            .addTags("b")
            .endTags()
            .address(address)
            .build();

    assertThat(staged.name()).isEqualTo("fred");
    assertThat(staged.age()).isEqualTo(42);
    assertThat(staged.nickname()).isNull();
    assertThat(staged.motto()).contains("go");
    assertThat(staged.address()).isSameAs(address);
    assertThat(staged.tags()).containsExactly("x", "a", "b");
  }

  @Test
  void collectionStageSetter() {
    Staged staged =
        StagedBuilder.builder()
            .name("fred")
            .age(42)
            .nickname("freddy")
            .motto(Optional.empty())
            .tags(List.of("y"))
            .address(address)
            .build();

    assertThat(staged.tags()).containsExactly("y");
  }

  @Test
  void collectionStageEndKeepsDefault() {
    Staged staged =
        StagedBuilder.builder()
            .name("fred")
            .age(42)
            .nickname(null)
            .motto(Optional.empty())
            .endTags()
            .address(address)
            .build();

    assertThat(staged.tags()).containsExactly("x");
  }

  @Test
  void everyComponentIsAStage() {
    var stages =
        Arrays.stream(StagedBuilder.class.getDeclaredClasses())
            .filter(Class::isInterface)
            .map(Class::getSimpleName)
            .toList();
    assertThat(stages)
        .containsExactlyInAnyOrder(
            "NameStage", "AgeStage", "NicknameStage", "MottoStage", "TagsStage", "AddressStage");
  }

  @Test
  void builderFromIsUnstaged() {
    Staged original =
        StagedBuilder.builder()
            .name("fred")
            .age(42)
            .nickname(null)
            .motto(Optional.empty())
            .endTags()
            .address(address)
            .build();
    StagedBuilder from = StagedBuilder.builder(original);
    assertThat(from.nickname("freddy").build().name()).isEqualTo("fred");
  }

  @Test
  void generic() {
    StagedPair<String, Integer> pair =
        StagedPairBuilder.<String, Integer>builder().first("a").second(2).label("x").build();
    assertThat(pair).isEqualTo(new StagedPair<>("a", 2, "x"));
  }

  @Test
  void lastStageIsCollection() {
    StagedCollections built =
        StagedCollectionsBuilder.builder()
            .addTags("a")
            .endTags()
            .putCounts("b", 1)
            .putCounts("c", 2)
            .endCounts()
            .build();
    assertThat(built.tags()).containsExactly("a");
    assertThat(built.counts()).isEqualTo(Map.of("b", 1, "c", 2));
  }
}
