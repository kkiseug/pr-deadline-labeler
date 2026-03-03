package domain;

import static org.assertj.core.api.Assertions.assertThat;

import java.time.LocalDate;
import java.util.List;
import org.junit.jupiter.api.Test;

class PullRequestTest {

    @Test
    void OVER_DUE_라벨이_있으면_라벨을_업데이트하지_않는다() {
        PullRequest pr = new PullRequest(1L, LocalDate.of(2026, 3, 1), List.of("OVER-DUE"), false);
        FakeLabelAttacher fakeAttacher = new FakeLabelAttacher();
        LabelPolicy labelPolicy = new LabelPolicy(5L);

        pr.labelAttach(fakeAttacher, labelPolicy, false, LocalDate.of(2026, 3, 10));

        assertThat(fakeAttacher.getAttachedLabel()).isNull();
    }

    @Test
    void draft_pr이면서_skip_draft가_true면_라벨을_업데이트_하지_않는다() {
        PullRequest pr = new PullRequest(1L, LocalDate.of(2026, 3, 1), List.of(), true);
        FakeLabelAttacher fakeAttacher = new FakeLabelAttacher();
        LabelPolicy labelPolicy = new LabelPolicy(5L);

        pr.labelAttach(fakeAttacher, labelPolicy, true, LocalDate.of(2026, 3, 10));

        assertThat(fakeAttacher.getAttachedLabel()).isNull();
    }

    @Test
    void draft_pr이면서_skip_draft가_false면_라벨을_업데이트_한다() {
        PullRequest pr = new PullRequest(1L, LocalDate.of(2026, 3, 1), List.of(), true);
        FakeLabelAttacher fakeAttacher = new FakeLabelAttacher();
        LabelPolicy labelPolicy = new LabelPolicy(5L);

        pr.labelAttach(fakeAttacher, labelPolicy, false, LocalDate.of(2026, 3, 10));

        assertThat(fakeAttacher.getAttachedLabel()).isNotNull();
    }
}
