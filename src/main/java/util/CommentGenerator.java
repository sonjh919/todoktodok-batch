package util;

import java.util.*;
import java.util.concurrent.ThreadLocalRandom;

public class CommentGenerator {
    // 자주 쓰이는 시작 구문
    private static final String[] PREFIXES = {
            "좋아요!", "정말 좋은 글이네요.", "와, 대단해요!", "잘 읽었습니다.", "공감합니다.",
            "이 부분 추가 설명 부탁드려요.", "궁금한 점이 있습니다.", "감사합니다.", "흥미롭네요.", "재미있습니다."
    };
    // 자주 쓰이는 중간 키워드
    private static final String[] KEYWORDS = {
            "알고리즘", "성능", "설계", "최적화", "예제", "코드", "테스트", "디자인", "데이터", "트랜잭션"
    };
    // 자주 쓰이는 마무리 구문
    private static final String[] SUFFIXES = {
            "😊", "👍", "👌", "😉", "🔥"
    };

    private final List<String> popularComments;

    public CommentGenerator() {
        this.popularComments = buildPopularComments(100);
    }

    // 인기 댓글 미리 생성
    private List<String> buildPopularComments(int count) {
        List<String> list = new ArrayList<>();
        Random rand = new Random(123);
        for (int i = 0; i < count; i++) {
            String prefix = PREFIXES[rand.nextInt(PREFIXES.length)];
            String keyword = KEYWORDS[rand.nextInt(KEYWORDS.length)];
            String suffix = SUFFIXES[rand.nextInt(SUFFIXES.length)];
            list.add(prefix + " " + keyword + " 👍 " + suffix);
        }
        return list;
    }

    /**
     * 댓글 생성
     * @param seed               (옵션) 재현성을 위한 시드
     * @param reusePopularity    인기 댓글 재사용 확률 (0~1)
     */
    public String makeComment(Long seed, double reusePopularity) {
        Random rnd = (seed != null) ? new Random(seed) : ThreadLocalRandom.current();

        // 인기 댓글 재사용
        if (rnd.nextDouble() < reusePopularity) {
            return popularComments.get(rnd.nextInt(popularComments.size()));
        }

        // 랜덤 조합
        String prefix = PREFIXES[rnd.nextInt(PREFIXES.length)];
        String keyword = KEYWORDS[rnd.nextInt(KEYWORDS.length)];
        String suffix = SUFFIXES[rnd.nextInt(SUFFIXES.length)];

        // 문장 길이 변화를 위한 추가 단어
        int extraCount = rnd.nextInt(3); // 0~2개 추가
        StringBuilder extra = new StringBuilder();
        for (int i = 0; i < extraCount; i++) {
            extra.append(" ").append(KEYWORDS[rnd.nextInt(KEYWORDS.length)]);
        }

        return prefix + " " + keyword + extra + " " + suffix;
    }
}
