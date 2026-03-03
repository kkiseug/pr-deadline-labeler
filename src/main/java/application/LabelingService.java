package application;

import domain.LabelAttacher;
import domain.LabelPolicy;
import domain.PullRequest;
import domain.PullRequestRepository;
import java.time.LocalDate;
import java.util.List;

public class LabelingService {

    private final LabelAttacher labelAttacher;
    private final PullRequestRepository pullRequestRepository;
    private final LabelPolicy labelPolicy;

    public LabelingService(LabelAttacher labelAttacher,
                           PullRequestRepository pullRequestRepository,
                           LabelPolicy labelPolicy
    ) {
        this.labelAttacher = labelAttacher;
        this.pullRequestRepository = pullRequestRepository;
        this.labelPolicy = labelPolicy;
    }

    public void attachLabels(boolean skipDraft, LocalDate today) {
        pullRequestRepository.getOpenPullRequest()
            .forEach(pr -> pr.labelAttach(labelAttacher, labelPolicy, skipDraft, today));
    }

    public void attachLabel(Long prNum, LocalDate baseDate, boolean skipDraft, LocalDate today) {
        PullRequest pullRequest = pullRequestRepository.findByPRNumber(prNum).withBaseDate(baseDate);
        pullRequest.labelAttach(labelAttacher, labelPolicy, skipDraft, today);
    }
}
