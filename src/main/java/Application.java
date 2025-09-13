import domain.MemberDao;
import database.ConnectMysql;

public class Application {
    public static void main(String[] args) {

        int cores = Runtime.getRuntime().availableProcessors();
        System.out.println("CPU cores: " + cores);

        // Member
        MemberDao memberDao = new MemberDao(new ConnectMysql());
        memberDao.deleteAllMembers();
        memberDao.addMemberBatchMultiThread(1000000, cores*2);

        // Discussion

        // Comment

        // Reply
    }
}
