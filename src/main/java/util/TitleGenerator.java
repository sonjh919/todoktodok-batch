package util;

import java.util.*;
import java.util.concurrent.ThreadLocalRandom;

public class TitleGenerator {
    private static final String[] EN_SHORT = {"AI","DB","OS","ML","GC","UX","API","IoT","ETL","ORM","NLP","SQL"};
    private static final String[] KO_SHORT = {"입문","실전","요약","핵심","비법","가이드","정리","분석","설계","테스트","검색","코드","데이터"};
    private static final String[] KO_ADJ = {"빠른","느린","작은","큰","안정적인","확장형","분산형","실용적인","현대적인","기초","고급"};
    private static final String[] KO_NOUN = {"데이터","시스템","네트워크","알고리즘","데이터베이스","트랜잭션","캐시","검색","파이프라인","ERD","운영","테스트","설계","성능","지표"};
    private static final String[] GENRES = {"Fantasy","Sci-Fi","History","Economy","Coding","Startup","Design","Cloud","Security","Analytics"};

    private final List<String> popularTitles;

    public TitleGenerator() {
        this.popularTitles = buildPopularTitles(120);
    }

    private List<String> buildPopularTitles(int count) {
        List<String> titles = new ArrayList<>();
        Random rand = new Random(42); // 시드 고정으로 일관된 인기 타이틀 생성

        for (int i = 0; i < count; i++) {
            String title = switch (rand.nextInt(4)) {
                case 0 -> randChoice(KO_ADJ, rand) + " " + randChoice(KO_NOUN, rand) + " 101";
                case 1 -> randChoice(EN_SHORT, rand) + " " + randChoice(new String[]{"101","2.0","Best"}, rand);
                case 2 -> randChoice(KO_NOUN, rand) + ": " + randChoice(KO_SHORT, rand);
                case 3 -> randChoice(EN_SHORT, rand) + " x " + randChoice(KO_NOUN, rand);
                default -> "기본 제목";
            };
            titles.add(title);
        }
        return titles;
    }

    public String makeTitle(Long seed, double reusePopularity) {
        // ThreadLocalRandom 사용으로 멀티스레드 환경에서 안전
        Random rnd = (seed != null) ? new Random(seed) : ThreadLocalRandom.current();

        // 인기 타이틀 재사용
        if (rnd.nextDouble() < reusePopularity) {
            return popularTitles.get(rnd.nextInt(popularTitles.size()));
        }

        // 랜덤 요소들 (emojiOpt 제거)
        String yearOrNum = String.valueOf(rnd.nextInt(2025 - 1998 + 1) + 1998);
        String shortToken = randChoice(EN_SHORT, rnd);

        // 다양한 템플릿 중 랜덤 선택 (이모지 제거된 버전)
        String title = switch (rnd.nextInt(10)) {
            case 0 -> randChoice(KO_ADJ, rnd) + " " + randChoice(KO_NOUN, rnd) + " " + yearOrNum;
            case 1 -> randChoice(KO_NOUN, rnd) + " " + shortToken;
            case 2 -> randChoice(EN_SHORT, rnd) + " " + randChoice(KO_NOUN, rnd) + " 가이드";
            case 3 -> "Q&A: " + randChoice(KO_NOUN, rnd) + "와 " + randChoice(EN_SHORT, rnd);
            case 4 -> randChoice(GENRES, rnd) + " for " + randChoice(EN_SHORT, rnd);
            case 5 -> randChoice(KO_NOUN, rnd) + " 체크리스트 #" + (rnd.nextInt(50) + 1);
            case 6 -> randChoice(EN_SHORT, rnd) + " vs " + randChoice(EN_SHORT, rnd);
            case 7 -> randChoice(KO_NOUN, rnd) + " · " + randChoice(KO_SHORT, rnd) + " · " + shortToken;
            case 8 -> randChoice(KO_NOUN, rnd) + " — " + randChoice(new String[]{"핵심정리","실무팁","리팩터링","모범사례"}, rnd);
            case 9 -> shortToken + ": " + randChoice(new String[]{"case study","benchmark","anti-pattern","best practices"}, rnd);
            default -> randChoice(KO_NOUN, rnd) + " 기초";
        };

        // 너무 짧으면 보강
        if (title.length() < 6) {
            title += " " + randChoice(new String[]{"가이드", "101", "Note"}, rnd);
        }

        return title;
    }

    private String randChoice(String[] array, Random rand) {
        return array[rand.nextInt(array.length)];
    }
}
