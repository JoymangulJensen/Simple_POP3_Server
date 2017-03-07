import com.sun.javaws.exceptions.InvalidArgumentException;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * Created by p1509413 on 06/03/2017.
 */
public class MailBoxProcessor {
    public final static String MAILBOX_DIRECTORY = "mailboxes/";

    private List<User> users = new ArrayList<>();

    private User user;

    MailBoxProcessor() {
        this.buildUsers();
    }

    User authentication(String username, String password) throws InvalidArgumentException {
        User user = this.findUserByUsername(username);
        if (Objects.equals(user.getPassword(), password)) {
            this.user = user;
            return user;
        }
        throw new InvalidArgumentException(new String[]{"Password invalid"});
    }

    private User findUserByUsername(String username) throws InvalidArgumentException {
        for (User user : users) {if (Objects.equals(user.getUsername(), username)) return user;}
        throw new InvalidArgumentException(new String[]{"User not found"});
    }

    private void buildUsers() {
        this.users = UserParser.parse();
    }

    public List<User> getUsers() {return users;}

}
