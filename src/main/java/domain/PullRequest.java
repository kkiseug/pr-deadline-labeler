package domain;

import java.time.LocalDate;
import java.util.List;

public class PullRequest {

    private final Long prNumber;
    private final LocalDate createdAt;
    private final List<String> currentLabels;
    private final boolean draft;

    public PullRequest(Long prNumber, LocalDate createdAt, List<String> currentLabels, boolean draft) {
        this.prNumber = prNumber;
        this.createdAt = createdAt;
        this.currentLabels = currentLabels;
        this.draft = draft;
    }

    public void labelAttach(LabelAttacher labelAttacher, LabelPolicy labelPolicy, boolean skipDraft, LocalDate today) {
        if (isOverDue()) {
            System.out.printf("OVER-DUE PR #%d 제외되었습니다.%n", prNumber);
            return;
        }
        if (skipDraft && draft) {
            System.out.printf("draft PR #%d 제외되었습니다.%n", prNumber);
            return;
        }

        String label = labelPolicy.resolve(createdAt, today);
        String currentDnLabel = getCurrentDnLabel();

        labelAttacher.attach(prNumber, currentLabels, label);
        System.out.printf("성공적으로 #%d 라벨을 업데이트 했습니다. (%s -> %s)%n", prNumber, currentDnLabel, label);
    }

    public PullRequest withBaseDate(LocalDate baseDate) {
        return new PullRequest(prNumber, baseDate, currentLabels, draft);
    }

    private String getCurrentDnLabel() {
        return currentLabels.stream()
            .filter(currentLabel -> currentLabel.startsWith("D-"))
            .findFirst()
            .orElse("없음");
    }

    private boolean isOverDue() {
        return currentLabels.stream()
            .anyMatch(label -> label.equals("OVER-DUE"));
    }
}
