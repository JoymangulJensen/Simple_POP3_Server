import com.sun.javaws.exceptions.InvalidArgumentException;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Gaetan on 05/03/2017.
 * Server Connexion
 */
class Connexion implements Runnable {

    private static final int NB_TRY = 3;
    private InputStream is;
    private OutputStream os;
    private MailBoxProcessor mailBoxProcessor = new MailBoxProcessor();

    private int nbTryConnexions = 0;

    private List<Message> messagesSent = new ArrayList<>();

    private boolean stop = false;

    Connexion(Socket socket) throws IOException {
        Socket clientSocket = socket;
        is = clientSocket.getInputStream();
        os = clientSocket.getOutputStream();
    }

    @Override
    public void run() {
        init();
        while (!stop) {
            stop = process();
        }
        stop();
    }

    private void init() {
        send(new Message(Command.OK, "Server Running"));
    }

    /**
     * Process the receptions of commands
     * @return boolean indicating if the program should stop or not
     */
    private boolean process() {
        Message message;
        switch ((message = receive()).getCommand()) {
            case APOP:
                List<String> args = message.getArgs();
                if (args.size() == 2) {
                    String username = args.get(0);
                    String password = args.get(1);
                    try {
                        mailBoxProcessor.authentication(username, password);
                    } catch (InvalidArgumentException e) {
                        if (nbTryConnexions ++ > NB_TRY) {
                            System.out.println("Number of tries exceeded");
                            this.stop();
                        }
                    }
                }
                break;
            case DELE:
                break;
            case RETR:
                break;
            case QUIT:
                send(new Message(Command.OK));
                return true;
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
        return false;
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
        } catch (IOException e) {
            System.out.printf("Could not read from input stream");
            e.printStackTrace();
        }
        return new Message(Command.EXCEPTION);
    }

    /**
     * End
     */
    private void stop() {
        try {
            is.close();
            os.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
