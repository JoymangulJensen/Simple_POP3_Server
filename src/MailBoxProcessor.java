import com.sun.javaws.exceptions.InvalidArgumentException;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by p1509413 on 06/03/2017.
 */
public class MailBoxProcessor {
    public final static String MAILBOX_DIRECTORY = "mailboxes/";

    private List<User> users = new ArrayList<>();

    public MailBoxProcessor() {
        this.buildUsers();
    }

    public MailBox authentication(String username, String password) throws InvalidArgumentException {
        User user = this.findUserByUsername(username);
        if (user.getPassword() == password) return user.getMailBox();
        throw new InvalidArgumentException(new String[]{"Password invalid"});
    }

    private User findUserByUsername(String username) throws InvalidArgumentException {
        for (User user : users) {
            if (user.getUsername() == username) return user;
        }
        throw new InvalidArgumentException(new String[]{"User not found"});
    }

    private void buildUsers() {
        this.users = UserParser.parse();
    }

    public List<User> getUsers() {return users;}

}
