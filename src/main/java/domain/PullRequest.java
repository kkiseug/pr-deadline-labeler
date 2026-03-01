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
        String currentDnLabel = getCurrentDnLabel();

        labelAttacher.attach(prNumber, currentLabels, label);
        System.out.printf("성공적으로 #%d 라벨을 업데이트 했습니다. (%s -> %s)%n", prNumber, currentDnLabel, label);
    }

    private String getCurrentDnLabel() {
        return currentLabels.stream()
            .filter(currentLabel -> currentLabel.startsWith("D-") || currentLabel.equals("OVER-DUE"))
            .findFirst()
            .orElse("없음");
    }

    private boolean isOverDue() {
        return currentLabels.stream()
            .anyMatch(label -> label.equals("OVER-DUE"));
    }
}
