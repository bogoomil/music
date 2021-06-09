package music.event;

public class NoteLabelDragEvent {
    private int id;
    private int x;

    public NoteLabelDragEvent(int id, int x) {
        super();
        this.id = id;
        this.x = x;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getX() {
        return x;
    }

    public void setX(int x) {
        this.x = x;
    }

    //



}
