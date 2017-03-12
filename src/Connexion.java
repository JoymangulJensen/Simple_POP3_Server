import com.sun.javaws.exceptions.InvalidArgumentException;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;
import java.net.SocketException;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Gaetan on 05/03/2017.
 * Server Connexion
 */
class Connexion implements Runnable {

    private static final int NB_TRY = 3;
    private Socket clientSocket;
    private InputStream is;
    private OutputStream os;
    private MailBoxProcessor mailBoxProcessor = new MailBoxProcessor();

    private int nbTryConnexions = 0;

    private List<Message> messagesSent = new ArrayList<>();

    private boolean stop = false;
    private User user = null;

    Connexion(Socket socket) throws IOException {
        clientSocket = socket;
        is = clientSocket.getInputStream();
        os = clientSocket.getOutputStream();
    }

    @Override
    public void run() {
        init();
        while (!stop) {
            process();
        }
    }

    private void init() {
        send(new Message(Command.OK, "Server Running"));
    }

    /**
     * Process the receptions of commands
     *
     * @return boolean indicating if the program should stop or not
     */
    private void process() {
        Message message;
        switch ((message = receive()).getCommand()) {
            case APOP:
                Message result = this.apop(message);
                if (result.getCommand() == Command.EXCEPTION) {
                    this.stop(result.getArgComplet());
                } else {
                    send(result);
                }
                break;
            case USER:
                this.user(message);
                break;
            case DELE:
                this.dele(message);
                break;
            case RETR:
                this.retr(message);
                break;
            case STAT:
                this.stat();
                break;
            case LIST:
                this.list(message);
                break;
            case RSET:
                this.rset();
                break;
            case QUIT:
                this.quit();
                break;
            case EXCEPTION:
                // Send again the last message
                send(messagesSent.get(messagesSent.size() - 1));
                break;
            case DEFAULT:
                System.out.println("default");
                break;
            default:
                // Command not known
                send(new Message(Command.ERROR, "Invalid command"));
        }
    }

    private void user(Message message) {
        Message messageReturn = new Message(Command.ERROR, "Invalid Credentials");
        List<String> args = message.getArgs();
        if (args.size() == 1) {
            String username = args.get(0);
            try {
                if (this.mailBoxProcessor.usernameExists(username)) {
                    this.user = new User(username, null);
                    this.send(new Message(Command.OK, username + " is a real hoopy frood"));
                    do {
                        Message passwordmsg = this.receive();
                        passwordmsg = this.receive(); // We have two receive() because putty send a blank after each command
                        this.pass(passwordmsg);
                    } while (nbTryConnexions < NB_TRY);
                }
            } catch (InvalidArgumentException e) {
                System.err.println(e);
                this.send(new Message(Command.ERROR, e.getRealMessage()));
                if (++nbTryConnexions > NB_TRY) {
                    String error = "Number of tries exceeded";
                    this.user = null;
                    this.send(new Message(Command.EXCEPTION, error));
                }
            }
        }
    }

    private void pass(Message message) {
        List<String> args = message.getArgs();
        if (args.size() == 1 && message.getCommand() == Command.PASS) {
            String password = args.get(0);
            try {
                this.user = mailBoxProcessor.authentication(this.user.getUsername(), password);
                this.user.setMailBox(new MailBox(this.user));
                this.send(new Message(Command.OK));
                nbTryConnexions = 3;
            } catch (InvalidArgumentException e) {
                System.out.println(e);
                this.send(new Message(Command.ERROR, e.toString()));
                if (++nbTryConnexions > NB_TRY) {
                    String error = "Number of tries exceeded";
                    this.user = null;
                    this.send(new Message(Command.EXCEPTION, error));
                }
            }
        } else {
            nbTryConnexions = 3;
            this.user = null;
            this.send(new Message(Command.EXCEPTION, "Cannot use command"));
        }
    }

    private Message apop(Message message) {
        Message messageReturn = new Message(Command.ERROR, "Invalid Credentials");
        List<String> args = message.getArgs();
        if (args.size() == 2) {
            String username = args.get(0);
            String password = args.get(1);
            try {
                this.user = mailBoxProcessor.authentication(username, password);
                this.user.setMailBox(new MailBox(this.user));
                messageReturn = new Message(Command.OK);
            } catch (InvalidArgumentException e) {
                System.out.println(e);
                if (++nbTryConnexions > NB_TRY) {
                    String error = "Number of tries exceeded";
                    messageReturn = new Message(Command.EXCEPTION, error);
                }
            }
        }
        return messageReturn;
    }

    private void list(Message message) {
        if (this.isAuthorise()) {
            if (message.getArgs().size() == 0) {
                List<Mail> mails = this.user.getMailBox().getMails();
                String mailboxSize = String.valueOf(this.user.getMailBox().getMailBoxSize());
                String nbMail = String.valueOf(this.user.getMailBox().getNbMail());
                this.send(new Message("+OK " + nbMail + " messages (" + mailboxSize + " octets)"));
                for (int i = 0; i < mails.size(); i++) {
                    this.send(new Message(String.valueOf(i + 1) + " " + String.valueOf(mails.get(i).getSize())));
                }
            } else {
                int mailIndex = Integer.parseInt(message.getArgs().get(0));
                try {
                    int mailSize = this.user.getMailBox().getMailBoxSize(mailIndex);
                    this.send(new Message(String.valueOf(mailIndex) + " " + String.valueOf(mailSize)));
                } catch (IndexOutOfBoundsException e) {
                    String nbMail = String.valueOf(this.user.getMailBox().getNbMail());
                    this.send(new Message(Command.ERROR, "no such message, only " + nbMail + " messages in maildrop"));
                }
            }
        }

    }

    private void stat() {
        if (this.isAuthorise()) {
            String mailboxSize = String.valueOf(this.user.getMailBox().getMailBoxSize());
            String nbMail = String.valueOf(this.user.getMailBox().getNbMail());
            this.send(new Message(Command.OK, nbMail + " " + mailboxSize));
        }
    }

    private void rset() {
        if (this.isAuthorise()) {
            this.user.getMailBox().reset();
            String mailboxSize = String.valueOf(this.user.getMailBox().getMailBoxSize());
            String nbMail = String.valueOf(this.user.getMailBox().getNbMail());
            this.send(new Message(Command.OK, nbMail + " messages (" + mailboxSize + " octets)"));
        }
    }

    private void retr(Message message) {
        Message error = new Message(Command.ERROR, "Error");
        if (this.isAuthorise()) {
            int mailIndex = Integer.parseInt(message.getArgs().get(0));
            if (message.getArgs().size() > 0) {
                try {
                    Mail mail = this.user.getMailBox().getMail(mailIndex);
                    this.send(new Message(Command.OK, mail.getSize() + " octets"));
                    this.send(new Message(mail.getContent()));
                    this.send(new Message("."));
                } catch (IndexOutOfBoundsException e) {
                    String nbMail = String.valueOf(this.user.getMailBox().getNbMail());
                    this.send(new Message(Command.ERROR, "no such message, only " + nbMail + " messages in maildrop"));
                }
            } else {
                this.send(error);
            }
        }
    }

    private void dele(Message message) {
        Message error = new Message("Error");
        if (this.isAuthorise()) {
            if (message.getArgs().size() > 0) {
                int mailIndex = Integer.parseInt(message.getArgs().get(0));
                try {
                    this.user.getMailBox().delete(mailIndex);
                    this.send(new Message(Command.OK, "message 1 deleted"));
                } catch (IndexOutOfBoundsException e) {
                    String nbMail = String.valueOf(this.user.getMailBox().getNbMail());
                    this.send(new Message(Command.ERROR, "no such message, only " + nbMail + " messages in maildrop"));
                }
            } else {
                this.send(error);
            }
        }

    }

    private void quit() {
        if (this.isAuthorise()) {
            if (this.mailBoxProcessor.updateMailBox(this.user.getMailBox())) {
                this.send(new Message(Command.OK));
                this.stop("QUIT");
            } else {
                this.send(new Message(Command.ERROR, "Cannot update mailbox"));
            }
        }
        this.stop("QUIT");
    }

    private boolean isAuthorise() {
        if (this.user == null) {
            this.send(new Message(Command.ERROR, "Authentication needed"));
            return false;
        }
        return true;
    }

    /**
     * Send a message over TCP
     *
     * @param message Message to sent
     */
    private void send(Message message) {
        try {
            os.write(message.getBytes());
            messagesSent.add(message);
            System.out.println("Message Sent : " + message);
        } catch (SocketException se) {
            this.stop("Socket Error");
        } catch (IOException e) {
            System.out.println("Could not write " + message);
            e.printStackTrace();
        }
    }

    /**
     * Receive a message over TCP
     *
     * @return the message received
     */
    private Message receive() {
        byte b[] = new byte[Message.BUFFER_MAX_SIZE];
        try {
            if (is.read(b) != -1) {
                Message message = new Message(b);
                System.out.println("Message received : " + message);
                return message;
            }
        } catch (SocketException se) {
            this.stop("Socket Error");
        } catch (IOException e) {
            System.out.printf("Could not read from input stream");
            e.printStackTrace();
        }
        return new Message(Command.EXCEPTION);
    }

    /**
     * End
     */
    private void stop(String statut) {
        if (!this.stop) {
            this.stop = true;
            try {
                is.close();
                os.close();
                this.clientSocket.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
            System.out.println(statut + " - Connexion finished");
        }
    }
}
