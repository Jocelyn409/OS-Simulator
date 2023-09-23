@SuppressWarnings("all")
public class KernelandProcess {
    private Thread thread;
    private boolean started;
    private int PID;
    private static int nextPID;
    private long sleepUntil;
    private Priority.Level level;
    private int processTimeoutCount;

    public KernelandProcess(UserlandProcess up, int PID, Priority.Level level) {
        thread = new Thread(up);
        this.PID = PID;
        nextPID = PID + 1;
        started = false;
        this.level = level;
        processTimeoutCount = 0;
    }

    // Suspend thread only if thread has already started.
    public void stop() {
        if(started) {
            thread.suspend();
        }
    }

    // Return true if thread has started and isAlive().
    public boolean isDone() {
        return started && !(thread.isAlive());
    }

    // Resume thread if already started; otherwise start it.
    public void run() {
        if(started) {
            thread.resume();
        }
        else {
            thread.start();
            started = true;
        }
    }

    // Check if the process needs to be demoted. processTimeoutCount is incremented,
    // then if it's 5, demote the process, and reset the count.
    public void checkProcessDemotion() {
        processTimeoutCount++;
        if(processTimeoutCount == 5 && (level != Priority.Level.Background)) {
            switch (level) {
                case RealTime -> level = Priority.Level.Interactive;
                case Interactive -> level = Priority.Level.Background;
            }
            processTimeoutCount = 0;
        }
    }

    public void resetProcessTimeoutCount() {
        processTimeoutCount = 0;
    }

    public void incrementRunsToTimeout() {
        processTimeoutCount++;
    }

    public int getPID() {
        return PID;
    }

    public void setPID(int PID) {
        this.PID = PID;
    }

    public long getSleepUntil() {
        return sleepUntil;
    }

    public void setSleepUntil(long sleepUntil) {
        this.sleepUntil = sleepUntil;
    }

    public Priority.Level getLevel() {
        return level;
    }

    public void setLevel(Priority.Level level) {
        this.level = level;
    }

    public int getProcessTimeoutCount() {
        return processTimeoutCount;
    }
}
