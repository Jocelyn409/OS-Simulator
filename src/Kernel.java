public class Kernel {
    public Kernel() {
        scheduler = new Scheduler();
    }

    private Scheduler scheduler;

    public int createProcess(UserlandProcess up) {
        return scheduler.createProcess(up);
    }
}
