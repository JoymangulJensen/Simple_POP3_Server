import javax.net.ssl.SSLServerSocket;
import javax.net.ssl.SSLServerSocketFactory;
import javax.net.ssl.SSLSocket;
import java.io.IOException;
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
     *
     * @throws IOException in case of exception
     */
    private static void launch() throws IOException {
        SSLServerSocket secureSocket = null;
        SSLServerSocketFactory factory = (SSLServerSocketFactory) SSLServerSocketFactory.getDefault();
        secureSocket = (SSLServerSocket) factory.createServerSocket(SERVER_PORT);
        secureSocket.setEnabledCipherSuites(factory.getSupportedCipherSuites());

        UserParser.parse();

        System.out.println("Server Running on port " + secureSocket.getLocalPort());

        boolean stop = false;
        while (connexions.size() < NB_CONNEXION_MAX && !stop) {
            SSLSocket socket = null;
            socket = (SSLSocket) secureSocket.accept();
            Connexion connexion = new Connexion(socket);
            connexions.add(connexion);
            System.out.println("New Connexion n°" + connexions.size() + " " + socket);
            new Thread(connexion).start();

        }

        if (stop) {
            secureSocket.close();
        }
    }
}
