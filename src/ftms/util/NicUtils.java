package ftms.util;

import java.time.DateTimeException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Locale;

public final class NicUtils {
    private static final DateTimeFormatter DISPLAY_DATE =
            DateTimeFormatter.ofPattern("dd MMMM yyyy", Locale.ENGLISH);

    private NicUtils() {
    }

    public static String normalize(String nic) {
        return nic == null ? "" : nic.trim().toUpperCase(Locale.ROOT);
    }

    public static LocalDate getBirthDate(String nic) {
        String normalized = normalize(nic);
        final int year;
        final String dayDigits;

        if (normalized.matches("\\d{9}[VX]")) {
            year = 1900 + Integer.parseInt(normalized.substring(0, 2));
            dayDigits = normalized.substring(2, 5);
        } else if (normalized.matches("\\d{12}")) {
            year = Integer.parseInt(normalized.substring(0, 4));
            dayDigits = normalized.substring(4, 7);
        } else {
            throw new IllegalArgumentException(
                    "Enter a valid NIC: 9 digits followed by V/X, or 12 digits.");
        }

        int dayOfYear = Integer.parseInt(dayDigits);
        if (dayOfYear > 500) {
            dayOfYear -= 500;
        }
        try {
            if (dayOfYear < 1) {
                throw new DateTimeException("Invalid day of year");
            }
            LocalDate birthDate = LocalDate.ofYearDay(year, dayOfYear);
            if (birthDate.isAfter(LocalDate.now())) {
                throw new DateTimeException("Birth date is in the future");
            }
            return birthDate;
        } catch (DateTimeException exception) {
            throw new IllegalArgumentException(
                    "The date encoded in this NIC is not valid.", exception);
        }
    }

    public static String formatBirthDate(String nic) {
        return getBirthDate(nic).format(DISPLAY_DATE);
    }
}
