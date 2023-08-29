public class Kernel {
    public Kernel() {
        scheduler = new Scheduler();
    }

    private Scheduler scheduler;

    public void sleep(int milliseconds) {
        scheduler.sleep(milliseconds);
    }

    public int createProcess(UserlandProcess up) {
        return scheduler.createProcess(up);
    }
}
