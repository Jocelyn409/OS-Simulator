import java.util.Arrays;
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
    private VirtualToPhysicalMapping[] physicalPages;

    public KernelandProcess(UserlandProcess up, Priority.Level level) {
        thread = new Thread(up);
        this.PID = nextPID;
        nextPID = PID + 1;
        started = false;
        this.level = level;
        processTimeoutCount = 0;
        indexArray = new int[] {-1, -1, -1, -1, -1, -1, -1, -1, -1, -1}; // Length of 10.
        processName = up.getClass().getSimpleName();
        messageQueue = new LinkedList<>();
        physicalPages = new VirtualToPhysicalMapping[100];
        Arrays.fill(physicalPages, new VirtualToPhysicalMapping());
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

    public boolean freeMemory(int pointer, int size) {
        for(int i = pointer; i < pointer + size; i++) {
            physicalPages[i] = new VirtualToPhysicalMapping();
        }
        return true;
    }

    public void clearPhysicalPages(int start, int end) {
        for(int i = start; i < end; i++) {
            physicalPages[i] = new VirtualToPhysicalMapping();
        }
    }

    public int getInUsePhysicalPage() {
        for(int i = 0; i < 100; i++) {
            if(physicalPages[i].getPhysicalPageNumber() != -1) {
                return i;
            }
        }
        return -1;
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

    public boolean messageQueueIsEmpty() {
        if(messageQueue.size() > 0) {
            return false; // messageQueue is not empty.
        }
        return true; // messageQueue is empty.
    }

    public void addToMessageQueue(KernelMessage message) {
        messageQueue.add(message);
    }

    public KernelMessage popFirstMessageOnQueue() {
        return messageQueue.pop();
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

    public LinkedList<KernelMessage> getMessageQueue() {
        return messageQueue;
    }

    public VirtualToPhysicalMapping[] getPhysicalPages() {
        return physicalPages;
    }

    public void setPhysicalPages(VirtualToPhysicalMapping[] physicalPages) {
        this.physicalPages = physicalPages;
    }
}
