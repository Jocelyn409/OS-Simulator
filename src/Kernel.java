public class Kernel implements Device {
    private Scheduler scheduler;
    private VirtualFileSystem VFS; // ??? is this correct???
    private KernelandProcess runningProcess;

    public Kernel() {
        scheduler = new Scheduler();
        VFS = new VirtualFileSystem();
        runningProcess = null;
    }

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
    public int Open(String input) {
        KernelandProcess runningProcess = scheduler.getRunningProcess();
        int[] processInts = runningProcess.getArrayInts();
        for(int i = 0; i < 10; i++) {
            if(processInts[i] == -1) {
                int idVFS;
                if((idVFS = VFS.Open(input)) != -1) {
                    return runningProcess.fillArrayInt(idVFS);
                }
            }
        }
        return -1;
    }

    @Override
    public void Close(int id) {
        int index = runningProcess.getAndResetArrayIntIndex(id);
        runningProcess.resetArrayInt(id); // this is wrong needs to reset vfs instead
    }

    @Override
    public byte[] Read(int id, int size) {
        // something like get the index from id then pass that index to vfs and close that?
        int[] processInts = runningProcess.getArrayInts();

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
