import application.LabelingService;
import domain.LabelAttacher;
import domain.LabelPolicy;
import domain.PullRequestRepository;
import infrastructure.GithubLabelAttacher;
import infrastructure.GithubPullRequestRepository;
import java.net.http.HttpClient;
import java.time.LocalDate;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;

public class Main {

    public static void main(String[] args) {
        try {
            String token = System.getenv("INPUT_GITHUB-TOKEN");
            String repo = System.getenv("GITHUB_REPOSITORY");
            String rawBaseDate = System.getenv("INPUT_BASE-DATE");
            String rawSkipDraft = System.getenv("INPUT_SKIP-DRAFT");
            String rawPrNumber = System.getenv("INPUT_PR-NUMBER");
            String rawDeadlineDays = System.getenv("INPUT_DEADLINE-DAYS");

            HttpClient httpClient = HttpClient.newHttpClient();
            LabelAttacher labelAttacher = new GithubLabelAttacher(httpClient, token, repo);
            PullRequestRepository pullRequestRepository = new GithubPullRequestRepository(httpClient, token, repo);
            LabelPolicy labelPolicy = new LabelPolicy(Long.parseLong(rawDeadlineDays));
            LabelingService labelingService = new LabelingService(labelAttacher, pullRequestRepository, labelPolicy);

            LocalDate now = LocalDate.now(ZoneId.of("Asia/Seoul"));
            boolean skipDraft = Boolean.parseBoolean(rawSkipDraft);
            if (rawPrNumber == null || rawPrNumber.isEmpty()) {
                labelingService.attachLabels(skipDraft, now);
            } else {
                LocalDate baseDate = LocalDate.parse(
                    rawBaseDate,
                    DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss'Z'")
                );

                long prNumber = Long.parseLong(rawPrNumber);
                labelingService.attachLabel(prNumber, baseDate, skipDraft, now);
            }
        } catch (RuntimeException e) {
            System.out.println("::error::" + e.getMessage());
            if (e.getCause() != null) {
                System.out.println("::error::caused by: " + e.getCause());
            }
            System.exit(1);
        }
    }
}
