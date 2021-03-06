package server.game;

import base.events.Event;
import base.reactor.Reactor;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;



public class Timer implements Runnable {
    public static final ExecutorService TIMER_THREAD_POOL = Executors.newSingleThreadExecutor();
    private int roundTime = 60*1000;
    private Game game;
    private Reactor reactor;
    private long initTime;
    private long currentTime;
    private long beginTime;

    public Timer(Game game) {
        this.game = game;
        reactor = game.getReactor();
    }
    public void setRoundTime(int roundTime) {
        this.roundTime = roundTime;
    }

    public long getRoundTime() {
        return roundTime;
    }

    public long getLastingTimeFromInit() {
        return currentTime - initTime;
    }

    public long getLastingTimeFromRound() {
        return currentTime - beginTime;
    }

    public void start() {
        TIMER_THREAD_POOL.submit(this);
    }
    @Override
    public void run() {
        currentTime = initTime = System.currentTimeMillis();

        reactor.submitEvent(new Event());
        while (game.isRunning()) {
            beginTime = System.currentTimeMillis();
            reactor.submitEvent(new Event());
            while (game.isRunning() && currentTime - beginTime < roundTime) {
                currentTime = System.currentTimeMillis();
                try {
                    Thread.sleep(100);
                } catch (InterruptedException e) {
                    game.stopGame();
                }
            }
            reactor.submitEvent(new Event());
        }
        reactor.submitEvent(new Event());

    }
}
