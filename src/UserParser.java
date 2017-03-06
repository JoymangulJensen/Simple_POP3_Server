import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

/**
 * Created by p1509413 on 06/03/2017.
 */
public class UserParser {

    private final static String USER_FILE_PATH = "users.txt";

    public static List<User> parse() {
        String stringAllfile = null;
        List<User> users = new ArrayList<>();
        try {
            File file = new File(USER_FILE_PATH);
            Scanner scanner = new Scanner(file);
            while (scanner.hasNextLine()) {
                String[] userDetails = scanner.nextLine().split(" ");
                users.add(new User(userDetails[0], userDetails[1]));
            }
            scanner.close();

        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        return users;
    }

}
