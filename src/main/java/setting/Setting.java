package setting;

public class Setting {

    /**
     * 0 개수를 세기 힘든 사람을 위한 상수
     */
    public static int ZERO = 0;
    public static int ONE_HUNDRED = 100; // 백
    public static int ONE_THOUSAND = 1000; // 천
    public static int TEN_THOUSAND = 10000; // 만
    public static int ONE_HUNDRED_THOUSAND = 100000; // 십만
    public static int ONE_MILLION = 1000000; // 백만: 10초 소요
    public static int TEN_MILLION = 10000000; // 천만: 60초~5분 소요 // 2개 이상의 테이블에 대해 설정하면 자원 부족으로 다 안들어감!! // 왜 오래걸리는지 모르겠음.. 갑자기..

    /**
     * 각 테이블마다 몇 개의 데이터를 집어넣을 건지!
     * ZERO로 세팅하면 아예 실행되지 않음!
     */

    /**
     * 각 테이블에 값을 넣기 전 사전에 필요한 데이터들
     * member, refreshToken, book은 필요한 데이터 없음
     * block은 member 데이터 필요
     * discussion은 member, book 데이터 필요
     * comment는 member, discussion 데이터 필요
     * reply는 member, comment 데이터 필요
     *
     * memberReport는 member 데이터 필요
     * discussionReport, discussionLike는 discussion, member 데이터 필요
     * commentReport, commentLike는 comment, member 데이터 필요
     * replyReport, replyLike는 report, member 데이터 필요
     */
    public static int MEMBER_COUNT = ONE_HUNDRED_THOUSAND;
    public static int BLOCK_COUNT = ZERO; // 최댓값: MEMBER_COUNT * (MEMBER_COUNT-1)
    public static int MEMBER_REPORT_COUNT = ZERO; // 최댓값: MEMBER_COUNT * (MEMBER_COUNT-1)
    public static int REFRESH_TOKEN_COUNT = ZERO;

    public static int BOOK_COUNT = ONE_HUNDRED_THOUSAND;

    public static int DISCUSSION_COUNT = ONE_HUNDRED_THOUSAND;
    public static int DISCUSSION_LIKE_COUNT = ONE_HUNDRED_THOUSAND; // 최댓값: MEMBER_COUNT (정책상)
    public static int DISCUSSION_REPORT_COUNT = ZERO; // 최댓값: MEMBER_COUNT * DISCUSSION_COUNT (정책상, 본인이 쓴 글 고려 x)

    public static int COMMENT_COUNT = ONE_HUNDRED_THOUSAND;
    public static int COMMENT_LIKE_COUNT = ZERO; // 최댓값: MEMBER_COUNT (정책상)
    public static int COMMENT_REPORT_COUNT = ZERO; // 최댓값: MEMBER_COUNT * COMMENT_COUNT (정책상, 본인이 쓴 글 고려 x)

    public static int REPLY_COUNT = ONE_HUNDRED_THOUSAND;
    public static int REPLY_LIKE_COUNT = ZERO; // 최댓값: MEMBER_COUNT (정책상)
    public static int REPLY_REPORT_COUNT = ZERO; // 최댓값: MEMBER_COUNT * REPLY_COUNT (정책상, 본인이 쓴 글 고려 x)

    /**
     * 현재 테이블에 있는 데이터 수!! 값을 골고루 넣어주기 위함 + 제대로 안적으면 에러남!! 까먹지 막고 꼭 쓰기!!
     * SELECT count(*) FROM MEMBER;
     * SELECT count(*) FROM BOOK;
     * SELECT count(*) FROM DISCUSSION;
     * SELECT count(*) FROM COMMENT;
     * SELECT count(*) FROM REPLY;
     */
    public static int CURRENT_MEMBER_COUNT = ZERO;
    public static int CURRENT_BOOK_COUNT = ZERO;
    public static int CURRENT_DISCUSSION_COUNT = ZERO;
    public static int CURRENT_COMMENT_COUNT = ZERO;
    public static int CURRENT_REPLY_COUNT = ZERO;

    /**
     * flag 세팅. 데이터 삭제 여부, 추가 여부
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
    public static final String DATABASE = "todoktodok_test2"; // MySQL DATABASE 이름
    public static final String OPTION = "?useSSL=false&serverTimezone=UTC&allowPublicKeyRetrieval=true"; // 옵션 (안건드는게좋음)
    public static final String USERNAME = "root"; //  MySQL 서버 아이디
    public static final String PASSWORD = "kansiro0314"; // MySQL 서버 비밀번호

    /**
     * 잘 들어갔는지 확인!
     SELECT count(*) FROM MEMBER;
     SELECT count(*) FROM BLOCK;
     SELECT count(*) FROM MEMBER_REPORT;
     SELECT count(*) FROM REFRESH_TOKEN;

     SELECT count(*) FROM BOOK;

     SELECT count(*) FROM DISCUSSION;
     SELECT count(*) FROM DISCUSSION_LIKE;
     SELECT count(*) FROM DISCUSSION_REPORT;

     SELECT count(*) FROM COMMENT;
     SELECT count(*) FROM COMMENT_LIKE;
     SELECT count(*) FROM COMMENT_REPORT;

     SELECT count(*) FROM REPLY;
     SELECT count(*) FROM REPLY_LIKE;
     SELECT count(*) FROM REPLY_REPORT;
     */
}
