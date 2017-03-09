/**
 * Created by p1509413 on 06/03/2017.
 */
public class Mail {

    private String content;
    private boolean toDelete = false;
    private int size;

    public Mail(String content, int size) {
        this.content = content;
        this.size = size;
    }

    public String getContent() {
        return content;
    }

    public boolean isToDelete() {
        return toDelete;
    }

    public void setToDelete(boolean toDelete) {
        this.toDelete = toDelete;
    }

    public int getSize() {
        return size;
    }

    public void setSize(int size) {
        this.size = size;
    }

    @Override
    public String toString() {
        return "Mail{" +
                "content='" + content + '\'' +
                ", size=" + size +
                '}';
    }
}
