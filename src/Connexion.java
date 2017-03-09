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
            case DELE:
                this.send(this.dele(message));
                break;
            case RETR:
                this.retr(message);
                break;
            case STAT:
                Message messageStat = this.stat();
                send(messageStat);
                break;
            case LIST:
                this.list(message);
                break;
            case RSET:
                this.send(this.rset(message));
                break;
            case QUIT:
                send(new Message(Command.OK));
                this.stop("QUIT");
            case EXCEPTION:
                // Send again the last message
                send(messagesSent.get(messagesSent.size()-1));
                break;
            case DEFAULT:
                System.out.println("default"); break;
            default:
                // Command not known
                send(new Message(Command.ERROR, "Invalid command"));
        }
    }

    private Message stat() {
        if(this.checkUser())
            return new Message(Command.ERROR, "Authentication needed");
        String mailboxSize = String.valueOf(this.user.getMailBox().getMailBoxSize());
        String nbMail = String.valueOf(this.user.getMailBox().getNbMail());
        return new Message("OK " + nbMail + " " +mailboxSize);
    }

    private Message list(Message message) {
        // TODO
        return null;
    }

    private Message rset(Message message) {
        Message error = new Message("Error");
        if (!checkUser()) {return error;}
        if (message.getArgs().size() == 0) {
            this.user.getMailBox().reset();
            return new Message("OK");
        }
        return error;
    }

    private void retr(Message message) {
        if (!checkUser()) {return;}

    }

    private Message dele(Message message) {
        Message error = new Message("Error");
        if (!checkUser()) {return error;}
        if (message.getArgs().size() == 1) {
            int idMessage = Integer.parseInt(message.getArgs().get(0));
            if (this.user.getMailBox().delete(idMessage)) {
                return new Message("OK message n°" + idMessage + " deleted!");
            }
        }
        return error;
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

    private boolean checkUser() {
        return this.user == null;
    }

    /**
     * Send a message over TCP
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
     * @return the message received
     */
    private Message receive() {
        byte b[] = new byte[Message.BUFFER_MAX_SIZE];
        try {
            if(is.read(b) != -1) {
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
