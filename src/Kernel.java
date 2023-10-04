public class Kernel implements Device {
    private Scheduler scheduler;
    private VirtualFileSystem VFS;
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
            if(processInts[i] == -1) { // Found an empty spot in the array.
                int idVFS;
                if((idVFS = VFS.Open(input)) != -1) {
                    return runningProcess.fillArrayInt(idVFS); // Return index since execution was successful.
                }
            }
        }
        return -1; // Return -1 since execution failed.
    }

    @Override
    public void Close(int ID) {
        VFS.Close(runningProcess.getVFSIndex(ID));
        runningProcess.resetArrayInt(ID);
    }

    @Override
    public byte[] Read(int ID, int size) {
        return VFS.Read(runningProcess.getVFSIndex(ID), size);
    }

    @Override
    public int Write(int ID, byte[] data) {
        return VFS.Write(runningProcess.getVFSIndex(ID), data);
    }

    @Override
    public void Seek(int ID, int to) {
        VFS.Seek(runningProcess.getVFSIndex(ID), to);
    }
}
