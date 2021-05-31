package music.event;

public class NoteLabelDragEndEvent {
    private int id;
    private int x;

    public NoteLabelDragEndEvent(int id, int x) {
        super();
        this.x = x;
        this.id = id;
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

}
