import java.util.HashSet;

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

    public static HashSet<String> getEnums() {

        HashSet<String> values = new HashSet<>();
        for (Command c : Command.values()) {
            values.add(c.name());
        }
        return values;
    }
}
