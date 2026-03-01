package infrastructure;

import domain.LabelAttacher;
import java.io.IOException;
import java.net.URI;
import java.net.URLEncoder;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpRequest.BodyPublishers;
import java.net.http.HttpResponse;
import java.net.http.HttpResponse.BodyHandlers;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Random;

public class GithubLabelAttacher implements LabelAttacher {

    private static final String BASE_URL = "https://api.github.com/repos/%s/issues/%d/labels";
    private static final String BASE_LABEL_URL = "https://api.github.com/repos/%s/labels";

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
        ensureLabelExists(label);
        attachNewDnLabels(prNumber, label);
    }

    private void ensureLabelExists(String label) {
        String labelEncode = URLEncoder.encode(label, StandardCharsets.UTF_8);
        HttpRequest request = HttpRequest.newBuilder(
                URI.create(String.format(BASE_LABEL_URL + "/" + labelEncode, repo))
            )
            .header("Authorization", "Bearer " + token)
            .header("Content-Type", "application/json")
            .GET()
            .build();

        try {
            HttpResponse<String> response = httpClient.send(request, BodyHandlers.ofString());
            if (response.statusCode() == 200) return;
            createDnLabel(label);
        } catch (IOException | InterruptedException e) {
            throw new RuntimeException("라벨 생성 실패", e);
        }
    }

    private void createDnLabel(String label) {
        HttpRequest request = HttpRequest.newBuilder(
                URI.create(String.format(BASE_LABEL_URL, repo))
            )
            .header("Authorization", "Bearer " + token)
            .header("Content-Type", "application/json")
            .POST(BodyPublishers.ofString("{\"name\": \"" + label + "\", \"color\": \"" + randomColor() + "\"}"))
            .build();

        try {
            httpClient.send(request, BodyHandlers.ofString());
        } catch (IOException | InterruptedException e) {
            throw new RuntimeException("라벨 생성 실패", e);
        }
    }

    private String randomColor() {
        Random random = new Random();
        return String.format("%06X", random.nextInt(0xFFFFFF + 1));
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
            httpClient.send(request, BodyHandlers.ofString());
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
