@SuppressWarnings("all")
public class KernelandProcess {
    private Thread thread;
    private boolean started;
    public int PID;
    private static int nextPID;

    public KernelandProcess(UserlandProcess up, int PID) {
        thread = new Thread(up);
        this.PID = PID;
        nextPID = PID + 1;
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

    // Resume thread if already started; otherwise, start it.
    public void run() {
        if(started) {
            thread.resume();
        }
        else {
            thread.start();
            started = true;
        }
    }
}
