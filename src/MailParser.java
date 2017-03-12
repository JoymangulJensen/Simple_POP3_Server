import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;
import java.util.stream.Stream;

/**
 * Created by p1509413 on 06/03/2017.
 */
public class MailParser {

    public static List<Mail> parse(User user) {
        List<Mail> mails= new ArrayList<>();
        String folder = MailBox.MAILBOX_DIRECTORY + user.getUsername();
        // Read all files that are in the user mailbox folder
        try(Stream<Path> paths = Files.walk(Paths.get(folder))) {
            paths.forEach(filePath -> {
                if (Files.isRegularFile(filePath)) {
                    File file = new File(String.valueOf(filePath));
                    System.out.println(String.valueOf(filePath));
                    // Read each mail in the user folder
                    try {
                        Scanner scanner = new Scanner(file);
                        String mailContent = "";
                        while (scanner.hasNextLine()) {
                            mailContent += scanner.nextLine() + "\r\n";
                        }
                        Mail mail =  new Mail(mailContent, (int)file.length(), String.valueOf(filePath));
                        mails.add(mail);
                        scanner.close();
                    } catch (FileNotFoundException e) {
                        e.printStackTrace();
                    }
                }
            });
        } catch (IOException e) {
            e.printStackTrace();
        }
        return mails;
    }
}
