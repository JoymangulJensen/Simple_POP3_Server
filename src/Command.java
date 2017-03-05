/**
 * Created by Gaetan on 05/03/2017.
 * List of commands
 */
public enum Command {

    OK("OK"),
    APOP("APOP"),
    DELE("DELE"),
    RETR("RETR"),
    QUIT("QUIT"),
    DEFAULT(""),
    EXCEPTION("EXCEPTION"),
    ERROR("ERROR");

    private final String text;

    public String getText() {return text;}

    Command(String text) {
        this.text = text;
    }

    public static String[] names() {
        Command[] commands = values();
        String[] names = new String[commands.length];
        for (int i = 0; i < commands.length; i++) {
            names[i] = commands[i].text;
        }
        return names;
    }
}
