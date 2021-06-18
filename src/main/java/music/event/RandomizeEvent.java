package music.event;

public class RandomizeEvent {
    int seed;
    int maxNoteLengthInTicks;

    public int getSeed() {
        return seed;
    }

    public void setSeed(int seed) {
        this.seed = seed;
    }

    public int getMaxNoteLengthInTicks() {
        return maxNoteLengthInTicks;
    }

    public void setMaxNoteLengthInTicks(int maxNoteLengthInTicks) {
        this.maxNoteLengthInTicks = maxNoteLengthInTicks;
    }

    public RandomizeEvent(int seed, int maxNoteLengthInTicks) {
        super();
        this.seed = seed;
        this.maxNoteLengthInTicks = maxNoteLengthInTicks;
    }




}
