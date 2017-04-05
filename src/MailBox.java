import java.util.ArrayList;
import java.util.List;

/**
 * Created by p1509413 on 06/03/2017.
 */
public class MailBox {
    public final static String MAILBOX_DIRECTORY = new StringBuilder()
            .append(System.getProperty("user.home"))
            .append("/IPC/mailboxes/").toString();

    private List<Mail> mails = new ArrayList<>();
    private List<Mail> mailsToDelete = new ArrayList<>();
    private User user;

    public List<Mail> getMails() {
        return mails;
    }

    public List<Mail> getMailsToDelete() {
        return mailsToDelete;
    }

    public User getUser() {
        return user;
    }

    public MailBox(User user) {
        this.user = user;
        this.buildMails();
    }

    private void buildMails() {
        this.mails = MailParser.parse(user);
    }

    /**
     *
     * @param index
     * @throws IndexOutOfBoundsException
     */
    public void delete(int index) throws IndexOutOfBoundsException{
        index--;
        this.mailsToDelete.add(this.mails.get(index));
        this.mails.remove(index);
    }

    public void reset() {
        for (Mail mail : mailsToDelete) {
            this.mails.add(mail);
        }
        this.mailsToDelete = new ArrayList<>();
    }

    /**
     * Get the mailbox size in bytes
     * @return
     */
    public int getMailBoxSize() {
        int size = 0;
        for (Mail mail :
                this.mails) {
            size += mail.getSize();
        }
        return size;
    }

    /**
     * Get the size(bytes) of a specific mail
     * @param index index of the mail for getting its size
     * @return the size of the mail
     * @throws IndexOutOfBoundsException exception when index is out of range of mailbox
     */
    public int getMailBoxSize(int index) throws IndexOutOfBoundsException {
        Mail mail = this.getMail(index);
        return mail.getSize();
    }

    /**
     * @return Number of mail not deleted in the mailbox
     */
    public int getNbMail() {
        int size = 0;
        for (Mail mail :
                this.mails) {
            size++;
        }
        return size;
    }

    /**
     * Get q specific mail according to an index
     * @param index int : index of mail to retrieve
     * @return Mail : the mail that corresponds to the index
     * @throws IndexOutOfBoundsException : Exception when index is invalid
     */
    public Mail getMail(int index)throws IndexOutOfBoundsException {
        return this.mails.get(--index);  // --index is here because the client mail index starts with 1
    }

    @Override
    public String toString() {
        String result = "MailBox{\n mails=";
        for (Mail mail : mails) {
            result += mail.toString() + "\n";
        }
        result += "}}";
        return result;
    }
}
