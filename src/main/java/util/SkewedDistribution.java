package util;

import java.util.concurrent.ThreadLocalRandom;

public class SkewedDistribution {
    private final int memberCount;
    private final int bookCount;

    public SkewedDistribution(int memberCount, int bookCount) {
        this.memberCount = memberCount;
        this.bookCount = bookCount;
    }

    public long skewedMemberId() {
        // 상위 20% 멤버가 70% 활동을 차지
        if (ThreadLocalRandom.current().nextDouble() < 0.7) {
            return ThreadLocalRandom.current().nextLong(1, (long)(memberCount * 0.2) + 1);
        }
        return ThreadLocalRandom.current().nextLong(1, memberCount + 1);
    }

    public long skewedBookId() {
        // 상위 30% 책이 60% 토론을 차지
        if (ThreadLocalRandom.current().nextDouble() < 0.6) {
            return ThreadLocalRandom.current().nextLong(1, (long)(bookCount * 0.3) + 1);
        }
        return ThreadLocalRandom.current().nextLong(1, bookCount + 1);
    }
}
