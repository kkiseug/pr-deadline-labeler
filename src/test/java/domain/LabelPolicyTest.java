package domain;

import static org.assertj.core.api.Assertions.assertThat;

import java.time.LocalDate;
import org.junit.jupiter.api.Test;

public class LabelPolicyTest {

    @Test
    void 기준일_당일에는_D2_라벨을_반환한다() {
        LocalDate baseDate = LocalDate.of(2026, 3, 1);
        LocalDate today = LocalDate.of(2026, 3, 1);
        LabelPolicy labelPolicy = new LabelPolicy();

        String label = labelPolicy.resolve(baseDate, today);

        assertThat(label).isEqualTo("D-2");
    }

    @Test
    void 기준일로부터_하루가_지나면_D1_라벨을_반환한다() {
        LocalDate baseDate = LocalDate.of(2026, 3, 1);
        LocalDate today = LocalDate.of(2026, 3, 2);
        LabelPolicy labelPolicy = new LabelPolicy();

        String label = labelPolicy.resolve(baseDate, today);

        assertThat(label).isEqualTo("D-1");
    }

    @Test
    void 기준일로부터_이틀이_지나면_D_DAY_라벨을_반환한다() {
        LocalDate baseDate = LocalDate.of(2026, 3, 1);
        LocalDate today = LocalDate.of(2026, 3, 3);
        LabelPolicy labelPolicy = new LabelPolicy();

        String label = labelPolicy.resolve(baseDate, today);

        assertThat(label).isEqualTo("D-Day");
    }

    @Test
    void 기준일로부터_사흘_이상이_지나면_OVER_DUE_라벨을_반환한다() {
        LocalDate baseDate = LocalDate.of(2026, 3, 1);
        LocalDate today = LocalDate.of(2026, 3, 6);
        LabelPolicy labelPolicy = new LabelPolicy();

        String label = labelPolicy.resolve(baseDate, today);

        assertThat(label).isEqualTo("OVER-DUE");
    }
}
