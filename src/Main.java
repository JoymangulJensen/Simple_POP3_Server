import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;

class Main {

    private static final int NB_CONNEXION_MAX = 10;

    private static final int SERVER_PORT = 8080;

    private static List<Connexion> connexions = new ArrayList<>();

    public static void main(String[] args) {

        try {
            launch();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Launch the server
     * @throws IOException in case of exception
     */
    private static void launch() throws IOException {
        ServerSocket serverSocket = new ServerSocket(SERVER_PORT);

        System.out.println("Server Running");

        boolean stop = false;
        while (connexions.size() < NB_CONNEXION_MAX && !stop) {
            Socket s = serverSocket.accept();
            Connexion connexion = new Connexion(s);
            connexions.add(connexion);
            new Thread(connexion).start();
        }

        if (stop) {
            serverSocket.close();
        }
    }
}
