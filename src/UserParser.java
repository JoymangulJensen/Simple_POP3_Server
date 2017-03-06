import java.util.ArrayList;
import java.util.List;

/**
 * Created by p1509413 on 06/03/2017.
 */
public class UserParser {

    private final static String USER_FILE_PATH = "./users.txt";

    public static List<User> parse() {
        // TODO parse users
        List<User> users = new ArrayList<>();
        users.add(new User("user1", "pass"));
        users.add(new User("user2", "pass"));
        return users;
    }

}
