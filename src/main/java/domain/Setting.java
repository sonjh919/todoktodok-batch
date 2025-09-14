package domain;

public class Setting {

    /**
     * 0 개수를 세기 힘든 사람을 위한 상수
     */
    public static int ONE_HUNDRED = 100; // 백
    public static int ONE_THOUSAND = 1000; // 천
    public static int TEN_THOUSAND = 10000; // 만
    public static int ONE_HUNDRED_THOUSAND = 100000; // 십만
    public static int ONE_MILLION = 1000000; // 백만: 10초 소요
    public static int TEN_MILLION = 10000000; // 천만: 60초 소요 //

    /**
     * 각 테이블마다 몇 개의 데이터를 집어넣을 건지!
     *
     * discussion은 member, book 데이터 필요
     * comment는 member, discussion 데이터 필요
     * reply는 member, comment 데이터 필요
     */
    public static int MEMBER_COUNT = ONE_THOUSAND;
    public static int BOOK_COUNT = ONE_HUNDRED;
    public static int DISCUSSION_COUNT = ONE_MILLION;
    public static int COMMENT_COUNT = TEN_MILLION;
    public static int REPLY_COUNT = TEN_MILLION;

    /**
     * flag 세팅
     * 세팅할때마다 둘다 true로 켜두고 하는걸 추천..! (데이터 삭제 -> 새 데이터 삽입 순으로 진행)
     * 데이터 넣는게 for문으로 도는거라 delete없이 add만 하면 pk 꼬여서 오류남
     */
    public static boolean DELETE_FLAG = true; // true면 데이터 삭제 실행
    public static boolean ADD_FLAG = true; // true면 데이터 추가 실행

    /**
     * thread 세팅
     * <p>
     * thread 수는 (core 수)*2로 설정. core 수 궁금하면 Runtime.getRuntime().availableProcessors() 출력해보고 맞춰서 thread 바꿔도 돼!
     * 데이터 수가 THRESHOLD 이하면 싱글 스레드로 돌아가고, 그 이상이면 멀티 스레드로 돌아가게 세팅
     */
    public static int THREAD = 16;
    public static int THRESHOLD = TEN_THOUSAND;

    /**
     * mysql 연결 세팅
     */
    public static final String SERVER = "localhost:3306"; // MySQL 서버 주소
    public static final String DATABASE = "todoktodok_test"; // MySQL DATABASE 이름
    public static final String OPTION = "?useSSL=false&serverTimezone=UTC&allowPublicKeyRetrieval=true"; // 옵션 (안건드는게좋음)
    public static final String USERNAME = "root"; //  MySQL 서버 아이디
    public static final String PASSWORD = "kansiro0314"; // MySQL 서버 비밀번호

    /**
     * SQL
     * SELECT count(*) FROM MEMBER;
     * SELECT count(*) FROM BOOK;
     * SELECT count(*) FROM DISCUSSION;
     * SELECT count(*) FROM COMMENT;
     * SELECT count(*) FROM REPLY;
     */
}
