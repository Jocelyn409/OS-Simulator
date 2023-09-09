public class Kernel {
    public Kernel() {
        scheduler = new Scheduler();
    }

    private final Scheduler scheduler;

    public int createProcess(UserlandProcess up) {
        return scheduler.createProcess(up);
    }
}
