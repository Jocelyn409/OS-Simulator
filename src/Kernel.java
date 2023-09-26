public class Kernel implements Device {
    public Kernel() {
        scheduler = new Scheduler();
    }

    private Scheduler scheduler;

    public void sleep(int milliseconds) {
        scheduler.sleep(milliseconds);
    }

    public int createProcess(UserlandProcess up, Priority.Level level) {
        return scheduler.createProcess(up, level);
    }

    public int createProcess(UserlandProcess up) {
        return scheduler.createProcess(up);
    }

    @Override
    public int Open(String s) {
        scheduler.getRunningProcess();
        return 0;
    }

    @Override
    public void Close(int id) {

    }

    @Override
    public byte[] Read(int id, int size) {
        return new byte[0];
    }

    @Override
    public int Write(int id, byte[] data) {
        return 0;
    }

    @Override
    public void Seek(int id, int to) {

    }
}
