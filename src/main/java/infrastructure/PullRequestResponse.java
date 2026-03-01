package infrastructure;

import com.google.gson.annotations.SerializedName;
import java.util.List;

public record PullRequestResponse(
    @SerializedName("number")
    Long prNumber,
    @SerializedName("created_at")
    String createdAt,
    @SerializedName("labels")
    List<LabelResponse> labels
) {

    public record LabelResponse(
        @SerializedName("name")
        String name
    ) {

        @Override
        public String name() {
            return name;
        }
    }

    @Override
    public Long prNumber() {
        return prNumber;
    }

    @Override
    public String createdAt() {
        return createdAt;
    }

    @Override
    public List<LabelResponse> labels() {
        return labels;
    }
}
