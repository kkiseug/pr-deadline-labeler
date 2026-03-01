import application.LabelingService;
import domain.LabelAttacher;
import domain.LabelPolicy;
import domain.PullRequestRepository;
import infrastructure.GithubLabelAttacher;
import infrastructure.GithubPullRequestRepository;
import java.net.http.HttpClient;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

public class Main {

    public static void main(String[] args) {
        String token = System.getenv("INPUT_GITHUB-TOKEN");
        String repo = System.getenv("GITHUB_REPOSITORY");
        String baseDate = System.getenv("INPUT_BASE-DATE");
        String prNumber = System.getenv("INPUT_PR-NUMBER");

        HttpClient httpClient = HttpClient.newHttpClient();
        LabelAttacher labelAttacher = new GithubLabelAttacher(httpClient, token, repo);
        PullRequestRepository pullRequestRepository = new GithubPullRequestRepository(httpClient, token, repo);
        LabelPolicy labelPolicy = new LabelPolicy();
        LabelingService labelingService = new LabelingService(labelAttacher, pullRequestRepository, labelPolicy);

        LocalDate now = LocalDate.now();
        if (prNumber == null) {
            labelingService.attachLabels(now);
        } else {
            LocalDate realBaseDate = LocalDate.parse(baseDate, DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss'Z'"));

            long prNum = Long.parseLong(prNumber);
            labelingService.attachLabel(prNum, realBaseDate, now);
        }
    }
}
