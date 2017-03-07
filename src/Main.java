import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;

class Main {

    private static final int NB_CONNEXION_MAX = 10;

    private static final int SERVER_PORT = 1096;

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

        UserParser.parse();

        System.out.println("Server Running on port " + serverSocket.getLocalPort());

        boolean stop = false;
        while (connexions.size() < NB_CONNEXION_MAX && !stop) {
            Socket socket = serverSocket.accept();
            Connexion connexion = new Connexion(socket);
            connexions.add(connexion);
            System.out.println("New Connexion n°" + connexions.size() + " " + socket);
            new Thread(connexion).start();
        }

        if (stop) {
            serverSocket.close();
        }
    }
}
