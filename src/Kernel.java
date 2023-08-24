public class Kernel {
    public Kernel() {
        scheduler = new Scheduler();
    }

    private static Scheduler scheduler;

    public static int createProcess(UserlandProcess up) {
        return scheduler.createProcess(up);
    }
}
