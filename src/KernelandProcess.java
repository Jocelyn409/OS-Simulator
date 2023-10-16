import java.util.LinkedList;

@SuppressWarnings("all")
public class KernelandProcess {
    private Thread thread;
    private boolean started;
    private int PID;
    private static int nextPID = 0;
    private long sleepUntil;
    private Priority.Level level;
    private int processTimeoutCount;
    private int[] indexArray;
    private String processName;
    private LinkedList<KernelMessage> messageQueue;

    public KernelandProcess(UserlandProcess up, Priority.Level level) {
        thread = new Thread(up);
        this.PID = nextPID;
        nextPID = PID + 1;
        started = false;
        this.level = level;
        processTimeoutCount = 0;
        indexArray = new int[] {-1, -1, -1, -1, -1, -1, -1, -1, -1, -1}; // Length of 10.
        processName = getClass().getName(); // not right
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
        if(processTimeoutCount >= 5 && (level != Priority.Level.Background)) {
            switch(level) {
                case RealTime -> level = Priority.Level.Interactive;
                case Interactive -> level = Priority.Level.Background;
            }
            processTimeoutCount = 0;
            System.out.println("We have demoted to " + level + " for PID: " + PID);
        }
    }

    public int getVFSIndex(int index) {
        return indexArray[index];
    }

    public int[] getIndexArray() {
        return indexArray;
    }

    public void resetIndexArray(int ID) {
        indexArray[ID] = -1;
    }

    public void setIndexArray(int index, int input) {
        indexArray[index] = input;
    }

    public void resetProcessTimeoutCount() {
        processTimeoutCount = 0;
    }

    public int getPID() {
        return PID;
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

    public String getProcessName() {
        return processName;
    }
}
