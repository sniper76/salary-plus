package ag.act.module.digitaldocumentgenerator.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class DateFill {
    private String year;
    private String month;
    private String day;
    private String hour;

    public DateFill(java.time.ZonedDateTime dateTime) {
        this.year = String.valueOf(dateTime.getYear());
        this.month = String.valueOf(dateTime.getMonthValue());
        this.day = String.valueOf(dateTime.getDayOfMonth());
        this.hour = String.valueOf(dateTime.getHour());
    }

    public static DateFill createPreview() {
        return new DateFill("2023", "10", "30", "17");
    }
}
