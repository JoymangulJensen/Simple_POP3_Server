import com.sun.javaws.exceptions.InvalidArgumentException;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * Created by p1509413 on 06/03/2017.
 */
public class MailBoxProcessor {
    private List<User> users = new ArrayList<>();
    private String timestamp;
    private User user;

    MailBoxProcessor() {
        this.buildUsers();
    }

    User authentication(String username, String encodedPassword, String timestamp) throws InvalidArgumentException {
        User user = this.findUserByUsername(username);
        this.timestamp = timestamp;
        String userPassword = getEncodedPassword(user.getPassword());
        if (Objects.equals(userPassword, encodedPassword)) {
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

    public String getEncodedPassword(String password)
    {
        MessageDigest messageDigest;
        try {
            String fullPhrase = this.timestamp + password;
            messageDigest = MessageDigest.getInstance("MD5");
            messageDigest.update(fullPhrase.getBytes());
            byte[] messageDigestMD5 = messageDigest.digest();
            StringBuilder stringBuffer = new StringBuilder();
            for (byte bytes : messageDigestMD5) {
                stringBuffer.append(String.format("%02x", bytes & 0xff));
            }
            return stringBuffer.toString();
        } catch (NoSuchAlgorithmException exception) {
            exception.printStackTrace();
        }
        return null;
    }

    /**
     * Delete all mails that were marked as deleted
     * @param mailbox the mailbox to update
     * @return Noolean whether or not update was successful
     */
    public boolean updateMailBox(MailBox mailbox) {
        System.out.println("here");
        System.out.println(mailbox.getMails());
        for (Mail mail: mailbox.getMails()) {
            try {
                if (mail.isToDelete())
                    Files.delete(Paths.get(mail.getFileName()));
            } catch (IOException x) {
                System.err.println(x);
                return false;
            }
        }
        return true;
    }

    private void buildUsers() {
        this.users = UserParser.parse();
    }

    public List<User> getUsers() {
        return users;
    }

}
