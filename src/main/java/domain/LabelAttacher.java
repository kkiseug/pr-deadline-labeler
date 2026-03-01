package domain;

import java.util.List;

public interface LabelAttacher {

    void attach(Long prNumber, List<String> currentLabels, String label);
}
