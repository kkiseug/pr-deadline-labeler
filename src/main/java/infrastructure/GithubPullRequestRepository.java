package infrastructure;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import domain.PullRequest;
import domain.PullRequestRepository;
import infrastructure.PullRequestResponse.LabelResponse;
import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.net.http.HttpResponse.BodyHandlers;
import java.nio.charset.StandardCharsets;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;

public class GithubPullRequestRepository implements PullRequestRepository {

    private static final String BASE_URL = "https://api.github.com/repos/%s/pulls?state=open";
    private static final String BASE_LABEL_URL = "https://api.github.com/repos/%s/issues/%d/labels";

    private final HttpClient httpClient;
    private final String token;
    private final String repo;

    public GithubPullRequestRepository(HttpClient httpClient, String token, String repo) {
        this.httpClient = httpClient;
        this.token = token;
        this.repo = repo;
    }

    @Override
    public List<PullRequest> getOpenPullRequest() {
        HttpRequest request = HttpRequest.newBuilder(
                URI.create(String.format(BASE_URL, repo))
            )
            .header("Authorization", "Bearer " + token)
            .header("Accept", "application/vnd.github+json")
            .GET()
            .build();

        try {
            HttpResponse<String> response = httpClient.send(request, BodyHandlers.ofString(StandardCharsets.UTF_8));

            Gson gson = new Gson();
            List<PullRequestResponse> dto = gson.fromJson(
                response.body(),
                new TypeToken<List<PullRequestResponse>>() {
                }.getType()
            );

            return dto.stream()
                .map(prResponse -> new PullRequest(
                        prResponse.prNumber(),
                        LocalDate.parse(prResponse.createdAt(), DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss'Z'")),
                        prResponse.labels().stream()
                            .map(LabelResponse::name)
                            .toList()
                    )
                )
                .toList();
        } catch (IOException | InterruptedException e) {
            throw new RuntimeException("PR 목록 불러오기 실패", e);
        }
    }

    @Override
    public List<String> getCurrentLabels(Long prNumber) {
        HttpRequest request = HttpRequest.newBuilder(
                URI.create(String.format(BASE_LABEL_URL, repo, prNumber))
            )
            .header("Authorization", "Bearer " + token)
            .header("Accept", "application/vnd.github+json")
            .GET()
            .build();

        try {
            HttpResponse<String> response = httpClient.send(request, BodyHandlers.ofString(StandardCharsets.UTF_8));

            Gson gson = new Gson();
            List<LabelResponse> dto = gson.fromJson(
                response.body(),
                new TypeToken<List<LabelResponse>>() {
                }.getType()
            );

            return dto.stream()
                .map(LabelResponse::name)
                .toList();
        } catch (IOException | InterruptedException e) {
            throw new RuntimeException("PR 라벨 목록 불러오기 실패", e);
        }
    }
}
