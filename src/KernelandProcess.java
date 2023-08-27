@SuppressWarnings("all")
public class KernelandProcess {
    public int PID;
    private static int nextPID;
    private boolean started;
    private Thread thread;

    public KernelandProcess(UserlandProcess up, int PID) {
        thread = new Thread(up);
        this.PID = PID;
        nextPID = PID + 1;
    }

    public void stop() {
        if(started) {
            thread.suspend();
        }
    }

    public boolean isDone() {
        return started && !(thread.isAlive());
    }

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
