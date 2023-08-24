public class KernelandProcess {
    public static int PID;
    private static int nextPID = 1;
    private boolean started;
    private Thread thread;

    public KernelandProcess(UserlandProcess up) {
        thread = new Thread();
        PID = nextPID;
        nextPID++;
    }

    public void stop() {
        if(started) {
            started = false;
        }
    }

    public boolean isDone() {
        return started && !(thread.isAlive());
    }

    public void run() {
        // resume()/start()
        started = true;
    }
}
