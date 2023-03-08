package it.rialtlas.healthmonitor.model;

public class MeasurementFunction {
    private final Runnable functor;
    private final int measureName;
    private final int foreseenSeconds;
    private final String actionKey;
    private int elapsedTime;

    protected MeasurementFunction(int measureName, int foreseenSeconds, String actionKey, Runnable functor) {
        this.measureName = measureName;
        this.foreseenSeconds = foreseenSeconds;
        this.actionKey = actionKey;
        this.functor = functor;
    }
    public int elapsedTime(int time) {
        this.elapsedTime += time;

        return (this.elapsedTime>=foreseenSeconds ? foreseenSeconds/2 : 0);
    }

    public void resetTime() { this.elapsedTime = 0; }

    public int measure() {
        return this.measureName;
    }
    public String actionKey() {
        return this.actionKey;
    }

    public int foreseenSeconds() {
        return this.foreseenSeconds;
    }

    public void perform() {
        this.functor.run();
    }
}
