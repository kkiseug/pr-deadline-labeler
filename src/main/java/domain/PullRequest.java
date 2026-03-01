package domain;

import java.time.LocalDate;
import java.util.List;

public class PullRequest {

    private final Long prNumber;
    private final LocalDate createdAt;
    private final List<String> currentLabels;

    public PullRequest(Long prNumber, LocalDate createdAt, List<String> currentLabels) {
        this.prNumber = prNumber;
        this.createdAt = createdAt;
        this.currentLabels = currentLabels;
    }

    public void labelAttach(LabelAttacher labelAttacher, LabelPolicy labelPolicy, LocalDate today) {
        if (isOverDue()) return;

        String label = labelPolicy.resolve(createdAt, today);
        labelAttacher.attach(prNumber, currentLabels, label);
    }

    private boolean isOverDue() {
        return currentLabels.stream()
            .anyMatch(label -> label.equals("OVER-DUE"));
    }
}
