package music.event.tracks;

public class PlayTrackEvent {
    int channel;
    int instrument;
    int tempo;

    public PlayTrackEvent(int channel, int instrument, int tempo) {
        super();
        this.channel = channel;
        this.instrument = instrument;
        this.tempo = tempo;
    }

    public int getChannel() {
        return channel;
    }

    public void setChannel(int channel) {
        this.channel = channel;
    }

    public int getInstrument() {
        return instrument;
    }

    public void setInstrument(int instrument) {
        this.instrument = instrument;
    }

    public int getTempo() {
        return tempo;
    }

    public void setTempo(int tempo) {
        this.tempo = tempo;
    }





}
