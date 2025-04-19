package id.co.bankbsi.rizqtracker.util;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Random;

public class ReferenceNumberGenerator {
    private static final String TRANSFER_PREFIX = "TRX-TF-";
    private static final String TOPUP_PREFIX = "TRX-TU-";
    private static final DateTimeFormatter DATE_FORMAT = DateTimeFormatter.ofPattern("yyyyMMdd");
    private static final Random RANDOM = new Random();

    public static String generate(String transactionType) {
        String prefix = "transfer".equalsIgnoreCase(transactionType) ? TRANSFER_PREFIX : TOPUP_PREFIX;
        String datePart = LocalDateTime.now().format(DATE_FORMAT);
        String randomPart = String.format("%06d", RANDOM.nextInt(1000000));

        return prefix + datePart + randomPart;
    }
}
