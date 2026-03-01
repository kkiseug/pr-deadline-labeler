package infrastructure;

import domain.LabelAttacher;
import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpRequest.BodyPublishers;
import java.net.http.HttpResponse;
import java.net.http.HttpResponse.BodyHandlers;
import java.util.List;

public class GithubLabelAttacher implements LabelAttacher {

    private static final String BASE_URL = "https://api.github.com/repos/%s/issues/%d/labels";

    private final HttpClient httpClient;
    private final String token;
    private final String repo;

    public GithubLabelAttacher(HttpClient httpClient, String token, String repo) {
        this.httpClient = httpClient;
        this.token = token;
        this.repo = repo;
    }

    @Override
    public void attach(Long prNumber, List<String> currentLabels, String label) {
        removeCurrentDnLabels(prNumber, currentLabels);
        attachNewDnLabels(prNumber, label);
    }

    private void attachNewDnLabels(Long prNumber, String label) {
        HttpRequest request = HttpRequest.newBuilder(
                URI.create(String.format(BASE_URL, repo, prNumber))
            )
            .header("Authorization", "Bearer " + token)
            .header("Content-Type", "application/json")
            .POST(BodyPublishers.ofString("{\"labels\": [\"" + label + "\"]}"))
            .build();

        try {
            HttpResponse<String> response = httpClient.send(request, BodyHandlers.ofString());
            System.out.println("라벨 부착 응답 코드: " + response.statusCode());
            System.out.println("라벨 부착 응답 바디: " + response.body());
        } catch (IOException | InterruptedException e) {
            throw new RuntimeException("라벨 부착 실패", e);
        }
    }

    private void removeCurrentDnLabels(Long prNumber, List<String> currentLabels) {
        String dLabel = currentLabels.stream()
            .filter(cLabel -> cLabel.startsWith("D-") || cLabel.equals("OVER-DUE"))
            .findFirst()
            .orElse(null);

        if (dLabel != null) {
            HttpRequest request = HttpRequest.newBuilder(
                    URI.create(String.format(BASE_URL + "/%s", repo, prNumber, dLabel))
                )
                .header("Authorization", "Bearer " + token)
                .header("Accept", "application/vnd.github+json")
                .DELETE()
                .build();

            try {
                httpClient.send(request, HttpResponse.BodyHandlers.ofString());
            } catch (IOException | InterruptedException e) {
                throw new RuntimeException("라벨 제거 실패", e);
            }
        }
    }
}
