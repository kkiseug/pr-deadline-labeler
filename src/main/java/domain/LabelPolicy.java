package domain;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;

public class LabelPolicy {

    private final Long deadLine;

    public LabelPolicy(Long deadLine) {
        this.deadLine = deadLine;
    }

    public String resolve(LocalDate baseDate, LocalDate today) {
        Long between = ChronoUnit.DAYS.between(baseDate, today);
        Long daysLeft = deadLine - between;

        if (daysLeft.equals(0L)) return "D-Day";
        if (daysLeft.compareTo(0L) < 0) return "OVER-DUE";
        return "D-" + daysLeft;
    }
}
