package util;

import java.util.*;
import java.util.concurrent.ThreadLocalRandom;

public class DiscussionContentGenerator {
    // 시작 문장 템플릿
    private static final String[] OPENINGS = {
            "최근에", "저는", "우리 팀에서", "이전 프로젝트에서", "오늘", "개발 중에", "테스트 중에", "문서를 보다가", "사용해보니", "설계를 고민하며"
    };
    // 주요 키워드/구문
    private static final String[] TOPICS = {
            "성능 최적화", "메모리 누수", "스레드 안전성", "데드락", "트랜잭션 관리", "캐시 전략", "REST API 설계",
            "데이터베이스 인덱스", "JWT 인증", "부하 테스트", "마이크로서비스 아키텍처", "롤백 이슈", "로깅 전략"
    };
    // 연결어·부연 설명
    private static final String[] CONNECTORS = {
            "때문에", "문제로", "중에", "관해서", "관련하여", "이슈가", "효과적으로", "간단하게", "복잡하게", "실제로"
    };
    // 마무리 구문
    private static final String[] CLOSINGS = {
            "어떻게 생각하시나요?", "의견 부탁드립니다.", "조언 부탁드립니다.", "경험 공유해주세요.", "알려주세요.", "피드백 환영합니다."
    };

    private final List<String> popularContents;

    public DiscussionContentGenerator() {
        this.popularContents = buildPopularContents(100);
    }

    private List<String> buildPopularContents(int count) {
        List<String> list = new ArrayList<>();
        Random rand = new Random(321);
        for (int i = 0; i < count; i++) {
            String text = OPENINGS[rand.nextInt(OPENINGS.length)] + " "
                    + TOPICS[rand.nextInt(TOPICS.length)] + " "
                    + CONNECTORS[rand.nextInt(CONNECTORS.length)] + " "
                    + TOPICS[rand.nextInt(TOPICS.length)] + " "
                    + CLOSINGS[rand.nextInt(CLOSINGS.length)];
            list.add(text);
        }
        return list;
    }

    /**
     * 토론 본문(content) 생성
     * @param seed              (옵션) 재현성을 위한 시드
     * @param reusePopularity   인기 콘텐츠 재사용 확률 (0~1)
     * @return                  10~100자 길이의 자연어 문장
     */
    public String makeContent(Long seed, double reusePopularity) {
        Random rnd = (seed != null) ? new Random(seed) : ThreadLocalRandom.current();

        // 인기 콘텐츠 재사용
        if (rnd.nextDouble() < reusePopularity) {
            return popularContents.get(rnd.nextInt(popularContents.size()));
        }

        // 랜덤 생성
        String opening = OPENINGS[rnd.nextInt(OPENINGS.length)];
        String topic1 = TOPICS[rnd.nextInt(TOPICS.length)];
        String connector = CONNECTORS[rnd.nextInt(CONNECTORS.length)];
        String topic2 = TOPICS[rnd.nextInt(TOPICS.length)];
        String closing = CLOSINGS[rnd.nextInt(CLOSINGS.length)];

        String content = String.join(" ", opening, topic1, connector, topic2, closing);

        // 길이 보정: 10자 미만이면 키워드 추가, 100자 초과 시 잘라내기
        if (content.length() < 10) {
            content += " " + TOPICS[rnd.nextInt(TOPICS.length)];
        }
        if (content.length() > 100) {
            content = content.substring(0, 100);
        }
        return content;
    }
}
