package fuck.manthe.nmsl.util;

import org.jetbrains.annotations.NotNull;
import org.springframework.stereotype.Component;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Date;

@Component
public class FormatUtil {
    @NotNull
    public String formatVapeTime(@NotNull LocalDateTime date) {
        Instant instant = date.toInstant(ZoneOffset.UTC);
        ZonedDateTime zonedDateTime = instant.atZone(ZoneOffset.UTC);
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss.SSSXXX");
        return zonedDateTime.format(formatter);
    }

    @NotNull
    public String formatVapeTime(@NotNull Date date) {
        Instant instant = date.toInstant();
        ZonedDateTime zonedDateTime = instant.atZone(ZoneOffset.UTC);
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss.SSSXXX");
        return zonedDateTime.format(formatter);
    }

    @NotNull
    public String formatVapeTime(long timestamp) {
        return this.formatVapeTime(new Date(timestamp));
    }
}
