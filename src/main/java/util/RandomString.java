package util;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.Random;

public class RandomString {
    private static final Random random = new Random();

    public static LocalDateTime generateRandomDateTime(LocalDateTime end) {
        LocalDateTime start = end.minusYears(1);
        long secondsBetween = Duration.between(end, start).getSeconds();
        long randomSeconds = (long) (random.nextDouble() * (secondsBetween + 1));
        return end.plusSeconds(randomSeconds);
    }

    public static String generateRandomHangulWithSpace(int length) {
        Random random = new Random();
        StringBuilder sb = new StringBuilder();

        int startHangul = 0xAC00; // 한글 시작 유니코드
        int endHangul = 0xD7A3;   // 한글 끝 유니코드
        int space = 0x0020;       // 스페이스 유니코드

        for (int i = 0; i < length; i++) {
            // 한글 혹은 스페이스 랜덤 선택
            boolean chooseSpace = random.nextBoolean();

            if (chooseSpace) {
                sb.append((char) space);
            } else {
                int codePoint = startHangul + random.nextInt(endHangul - startHangul + 1);
                sb.append((char) codePoint);
            }
        }

        return sb.toString();
    }
}
