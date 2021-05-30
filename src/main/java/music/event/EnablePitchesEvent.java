package music.event;

public class EnablePitchesEvent {

    private boolean enable;

    public boolean isEnable() {
        return enable;
    }

    public void setEnable(boolean enable) {
        this.enable = enable;
    }

    public EnablePitchesEvent(boolean enable) {
        super();
        this.enable = enable;
    }



}
