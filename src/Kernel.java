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
            if(processInts[i] == -1) {
                int idVFS;
                if((idVFS = VFS.Open(input)) != -1) {
                    return runningProcess.fillArrayInt(idVFS);
                }
            }
        }
        return -1; // Return -1 since execution failed.
    }

    @Override
    public void Close(int ID) { // IDK IF THESE THINGS ARE RIGHT OR NOTTTT!!!!
        // we somehow need to translate the ID to VFS ID.
        if(runningProcess.findArrayInt(ID)) {
            VFS.Close(ID);
            runningProcess.resetArrayInt(ID);
        }
    }

    @Override
    public byte[] Read(int ID, int size) {
        if(runningProcess.findArrayInt(ID)) {
            return VFS.Read(ID, size);
        }
        throw new RuntimeException("No such valid ID " + ID + " in runningProcess arrayInts.");
    }

    @Override
    public int Write(int ID, byte[] data) {
        if(runningProcess.findArrayInt(ID)) {
            return VFS.Write(ID, data);
        }
        return -1;
    }

    @Override
    public void Seek(int ID, int to) {
        if(runningProcess.findArrayInt(ID)) {
            VFS.Seek(ID, to);
        }
    }
}
