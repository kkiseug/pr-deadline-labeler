package domain;

import java.util.List;

public interface PullRequestRepository {

    List<PullRequest> getOpenPullRequest();
}
