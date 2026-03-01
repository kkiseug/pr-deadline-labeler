package domain;

import java.util.List;

public class FakeLabelAttacher implements LabelAttacher {

    private String label;

    @Override
    public void attach(Long prNumber, List<String> currentLabels, String label) {
        this.label = label;
    }

    public String getAttachedLabel() {
        return label;
    }
}
