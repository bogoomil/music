package music.event;

public class NoteDragStartEvent {
    private int id;

    public NoteDragStartEvent(int id) {
        super();
        this.id = id;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }


}
