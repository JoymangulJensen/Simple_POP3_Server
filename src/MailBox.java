import java.util.ArrayList;
import java.util.List;

/**
 * Created by p1509413 on 06/03/2017.
 */
public class MailBox {

    private List<Mail> mails = new ArrayList<>();

    private String folderName;

    public MailBox(String username) {
        this.folderName = username;
        this.buildMails();
    }

    private void buildMails() {
        this.mails = MailParser.parse(this.folderName);
    }

}
