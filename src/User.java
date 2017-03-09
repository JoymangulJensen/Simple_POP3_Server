import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;

/**
 * Created by p1509413 on 06/03/2017.
 */
public class User {

    private String username = "default";
    private String password = "default";
    private MailBox mailBox;

    public User(String username, String password) {
        this.username = username;
        this.password = password;
        this.mailBox = null;
        try {
            Files.createDirectories(Paths.get(MailBox.MAILBOX_DIRECTORY +this.username));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public MailBox getMailBox() {
        return mailBox;
    }

    public void setMailBox(MailBox mailBox) {
        this.mailBox = mailBox;
    }

    @Override
    public String toString() {
        return "User{" +
                "username='" + username + '\'' +
                ", password='" + password + '\'' +
                '}';
    }
}
