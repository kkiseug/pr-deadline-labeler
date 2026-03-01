package domain;

import static org.assertj.core.api.Assertions.assertThat;

import java.time.LocalDate;
import org.junit.jupiter.api.Test;

public class LabelPolicyTest {

    @Test
    void 기준일_당일에는_D_마감일_라벨을_반환한다() {
        LocalDate baseDate = LocalDate.of(2026, 3, 1);
        LocalDate today = LocalDate.of(2026, 3, 1);
        Long deadLine = 5L;
        LabelPolicy labelPolicy = new LabelPolicy(deadLine);

        String label = labelPolicy.resolve(baseDate, today);

        assertThat(label).isEqualTo("D-" + deadLine);
    }

    @Test
    void 기준일로부터_하루가_지나면_D_마감일_1_라벨을_반환한다() {
        LocalDate baseDate = LocalDate.of(2026, 3, 1);
        LocalDate today = LocalDate.of(2026, 3, 2);
        Long deadLine = 5L;
        LabelPolicy labelPolicy = new LabelPolicy(deadLine);

        String label = labelPolicy.resolve(baseDate, today);

        assertThat(label).isEqualTo("D-4");
    }

    @Test
    void 마감일자가_되면_D_DAY_라벨을_반환한다() {
        LocalDate baseDate = LocalDate.of(2026, 3, 1);
        LocalDate today = LocalDate.of(2026, 3, 6);
        Long deadLine = 5L;
        LabelPolicy labelPolicy = new LabelPolicy(deadLine);

        String label = labelPolicy.resolve(baseDate, today);

        assertThat(label).isEqualTo("D-Day");
    }

    @Test
    void 기준일이_마감일보다_지나면_OVER_DUE_라벨을_반환한다() {
        LocalDate baseDate = LocalDate.of(2026, 3, 1);
        LocalDate today = LocalDate.of(2026, 3, 10);
        Long deadLine = 5L;
        LabelPolicy labelPolicy = new LabelPolicy(deadLine);

        String label = labelPolicy.resolve(baseDate, today);

        assertThat(label).isEqualTo("OVER-DUE");
    }
}
