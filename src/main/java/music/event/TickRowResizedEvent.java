package music.event;

public class TickRowResizedEvent {
    private int width;

    public TickRowResizedEvent(int width) {
        super();
        this.width = width;
    }

    public int getWidth() {
        return width;
    }

    public void setWidth(int width) {
        this.width = width;
    }

}
