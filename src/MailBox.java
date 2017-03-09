import java.util.ArrayList;
import java.util.List;

/**
 * Created by p1509413 on 06/03/2017.
 */
public class MailBox {
    public final static String MAILBOX_DIRECTORY = "mailboxes/";
    private List<Mail> mails = new ArrayList<>();
    private User user;


    public MailBox(User user) {
        this.user = user;
        this.buildMails();
    }

    private void buildMails() {
        this.mails = MailParser.parse(user);
    }

    public boolean delete(int idMessage) {
        if (idMessage-1 >= mails.size()) {
            return false;
        }
        mails.get(idMessage-1).setToDelete(true);
        return true;
    }

    public void reset() {
        for (Mail mail :mails) {
            mail.setToDelete(false);
        }
    }

    public int getMailBoxSize() {
        int size=0;
        for (Mail mail:
             this.mails) {
            size += mail.getSize();
        }
        return size;
    }

    public int getMailBoxSize(int index) throws ArrayIndexOutOfBoundsException{
        return this.mails.get(++index).getSize();
    }

    public int getNbMail() {
        return this.mails.size();
    }

    @Override
    public String toString() {
        String result = "MailBox{\n mails=";
        for (Mail mail: mails) {
            result += mail.toString() + "\n";
        }
        result += "}}";
        return result;
    }
}
