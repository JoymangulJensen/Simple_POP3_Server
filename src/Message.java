/**
 * Created by Gaetan on 05/03/2017.
 * Class Message representing a command message
 */
class Message {

    private Command command = Command.DEFAULT;

    Command getCommand() {return command;}

    private String arg = "";

    public String getArg() {return arg;}

    static final int BUFFER_MAX_SIZE = 1000;

    public Message() {}

    Message(byte[] message) {
        this(new String(message));
    }

    public Message(String fullMesage) {
        this.build(fullMesage);
    }

    Message(Command command, String arg) {
        this(command);
        this.arg = arg;
    }

    Message(Command command) {
        this.command = command;
    }

    private void build(String fullMessage) {
        String[] words = fullMessage.split(" ");
        command = this.findCommand(words[0]);
        arg = fullMessage.replaceFirst(command.getText(), "").trim();
    }

    private Command findCommand(String word) {
        for (String command : Command.names()) {
            if (word.equals(command)) {
                return Command.valueOf(command);
            }
        }
        return Command.DEFAULT;
    }

    @Override
    public String toString() {
        return command.getText() + " " + arg;
    }

    byte[] getBytes() {
        return this.toString().getBytes();
    }
}
