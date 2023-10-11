public class Kernel implements Device {
    private Scheduler scheduler;
    private VirtualFileSystem VFS;
    private KernelandProcess runningProcess;

    public Kernel() {
        scheduler = new Scheduler(this);
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
        // This if else-if statement helps make sure that we return -1 (error) if runningProcess
        // is null, and that we only assign the scheduler's runningProcess to it provided it
        // isn't null, which helps keep runningProcess not null once a process starts.
        KernelandProcess schedulerRunningProcess = scheduler.getRunningProcess();
        if(schedulerRunningProcess != null && !(schedulerRunningProcess.isDone())) {
            // If the scheduler's runningProcess is not null, assign it to this.runningProcess.
            runningProcess = scheduler.getRunningProcess();
        }
        else if(runningProcess == null) {
            return -1; // Return -1 as we don't have a runningProcess.
        }

        // Find an empty spot in the array and assign idVFS to it.
        int[] processInts = runningProcess.getIndexArray();
        for(int i = 0; i < 10; i++) {
            if(processInts[i] == -1) {
                int idVFS;
                if((idVFS = VFS.Open(input)) != -1) {
                    runningProcess.setIndexArray(i, idVFS);
                    return i; // Return index since execution was successful.
                }
            }
        }
        return -1; // Return -1 since execution failed.
    }

    @Override
    public void Close(int ID) {
        VFS.Close(runningProcess.getVFSIndex(ID));
        runningProcess.resetIndexArray(ID);
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
