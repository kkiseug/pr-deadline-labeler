package domain;

import java.time.LocalDate;

public class PullRequest {

    private final Long prNumber;
    private final LocalDate createdAt;

    public PullRequest(Long prNumber, LocalDate createdAt) {
        this.prNumber = prNumber;
        this.createdAt = createdAt;
    }

    public void labelAttach(LabelAttacher labelAttacher, LabelPolicy labelPolicy, LocalDate today) {
        String label = labelPolicy.resolve(createdAt, today);
        labelAttacher.attach(prNumber, label);
    }
}
