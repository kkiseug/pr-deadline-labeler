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

    public LabelingService(LabelAttacher labelAttacher, PullRequestRepository pullRequestRepository,
                           LabelPolicy labelPolicy) {
        this.labelAttacher = labelAttacher;
        this.pullRequestRepository = pullRequestRepository;
        this.labelPolicy = labelPolicy;
    }

    public void attachLabels(LocalDate today) {
        pullRequestRepository.getOpenPullRequest()
            .forEach(pr -> pr.labelAttach(labelAttacher, labelPolicy, today));
    }

    public void attachLabel(Long prNum, LocalDate baseDate, LocalDate today) {
        List<String> labels = pullRequestRepository.getCurrentLabels(prNum);
        PullRequest pullRequest = new PullRequest(prNum, baseDate, labels);

        pullRequest.labelAttach(labelAttacher, labelPolicy, today);
    }
}
