/**
 * Created by p1509413 on 06/03/2017.
 */
public class Mail {

    private String content;
    private int size;
    private String fileName;
    private boolean toDelete;

    public Mail(String content, int size, String fileName) {
        this.content = content;
        this.size = size;
        this.fileName = fileName;
    }

    public String getContent() {
        return content;
    }

    public int getSize() {
        return size;
    }

    public void setSize(int size) {
        this.size = size;
    }

    public String getFileName() {
        return fileName;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    @Override
    public String toString() {
        return "Mail{" +
//                "content='" + content + '\'' +
                ", size=" + size + "toDelete=" + toDelete +
                '}' + "\n\n";
    }

    public void setToDelete(boolean toDelete) {
        this.toDelete = toDelete;
    }

    public boolean isToDelete() {
        return toDelete;
    }
}
