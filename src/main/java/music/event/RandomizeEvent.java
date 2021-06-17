package music.event;

public class RandomizeEvent {
    int seed;

    public int getSeed() {
        return seed;
    }

    public void setSeed(int seed) {
        this.seed = seed;
    }

    public RandomizeEvent(int seed) {
        super();
        this.seed = seed;
    }



}
