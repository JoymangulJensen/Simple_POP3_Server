import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Created by Gaetan on 05/03/2017.
 * Class Message representing a command message
 */
class Message {

    private Command command = Command.DEFAULT;

    Command getCommand() {return command;}

    private String argComplet = "";

    private List<String> args = new ArrayList<>();

    public String getArgComplet() {return argComplet;}

    static final int BUFFER_MAX_SIZE = 1024;

    public Message() {}

    Message(byte[] message) {
        this(new String(message));
    }

    public Message(String fullMesage) {
        this.build(fullMesage);
    }

    Message(Command command, String arg) {
        this(command);
        this.argComplet = arg;
    }

    Message(Command command) {
        this.command = command;
    }

    private void build(String fullMessage) {
        String[] words = fullMessage.split(" ");
        command = this.findCommand(words[0].trim());
        argComplet = fullMessage.replaceFirst(command.getText(), "").trim();
    }

    private Command findCommand(String word) {
        for (String command : Command.getEnums()) {
            if (stringEquals(word, command)) {
                return Command.valueOf(command);
            }
        }
        return Command.DEFAULT;
    }

    @Override
    public String toString() {
        return command.getText() + " " + argComplet + "\r\n";
    }

    byte[] getBytes() {
        return this.toString().getBytes();
    }

    /**
     * Compare 2 strings char by char
     *
     * @param s1 first string
     * @param s2 second string
     * @return true if equals, false else
     */
    private boolean stringEquals(String s1, String s2) {

        if  (s1.length() != s2.length()) return false;
        for (int i = 0; i < s1.length(); i ++) {
            if (s1.charAt(i) != s2.charAt(i)) {
                return false;
            }
        }
        return true;
    }

    public List<String> getArgs() {
        if(this.argComplet.isEmpty())
            return new ArrayList<>();
        return Arrays.asList(argComplet.split(" "));
    }
}
