public class KernelandProcess {
    public int PID;
    private static int nextPID;
    private boolean started;
    private Thread thread;

    public KernelandProcess(UserlandProcess up) {
        thread = new Thread();
        PID = 1;
        nextPID = PID + 1;
    }

    public void stop() {
        if(started) {
            thread.interrupt(); //???
        }
    }

    public boolean isDone() {
        return started && !(thread.isAlive());
    }

    public void run() {
        thread.start();
        started = true;
    }
}
