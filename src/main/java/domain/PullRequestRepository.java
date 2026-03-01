package domain;

import java.util.List;

public interface PullRequestRepository {

    List<PullRequest> getOpenPullRequest();

    List<String> getCurrentLabels(Long prNumber);
}
