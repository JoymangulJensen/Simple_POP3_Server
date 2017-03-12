import com.sun.javaws.exceptions.InvalidArgumentException;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * Created by p1509413 on 06/03/2017.
 */
public class MailBoxProcessor {
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
        for (User user : users) {
            if (Objects.equals(user.getUsername(), username)) return user;
        }
        throw new InvalidArgumentException(new String[]{"User not found"});
    }

    public Boolean usernameExists(String username) throws InvalidArgumentException {
        for (User user : users) {
            if (Objects.equals(user.getUsername(), username)) return true;
        }
        throw new InvalidArgumentException(new String[]{"User not found"});
    }

    /**
     * Delete all mails that were marked as deleted
     * @param mailbox the mailbox to update
     * @return Noolean whether or not update was successful
     */
    public Boolean updateMailBox(MailBox mailbox) {
        for (Mail mail: mailbox.getMailsToDelete()) {
            try {
                Files.delete(Paths.get(mail.getFileName()));
            } catch (IOException x) {
                System.err.println(x);
                return Boolean.FALSE;
            }
        }
        return Boolean.TRUE;
    }

    private void buildUsers() {
        this.users = UserParser.parse();
    }

    public List<User> getUsers() {
        return users;
    }

}
