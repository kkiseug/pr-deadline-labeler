package domain;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;

public class LabelPolicy {

    public String resolve(LocalDate baseDate, LocalDate today) {
        long between = ChronoUnit.DAYS.between(baseDate, today);

        if (between == 0) return "D-2";
        if (between == 1) return "D-1";
        if (between == 2) return "D-Day";
        return "OVER-DUE";
    }
}
