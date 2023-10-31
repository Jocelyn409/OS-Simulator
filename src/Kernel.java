public class Kernel implements Device {
    private Scheduler scheduler;
    private VirtualFileSystem VFS;
    private KernelandProcess runningProcess;
    private boolean[] pagesInUse;

    public Kernel() {
        scheduler = new Scheduler();
        VFS = new VirtualFileSystem();
        runningProcess = null;
        pagesInUse = new boolean[1024]; // 1024 (number of pages) or 1048576 (memory's size), idk.
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
        if((runningProcess = scheduler.getRunningProcess()) == null) {
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
        if(runningProcess == null) {
            return;
        }
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

    public int getPid() {
        return scheduler.getPid();
    }

    public int getPidByName(String input) {
        return scheduler.getPidByName(input);
    }

    public void sendMessage(KernelMessage kernelMessage) {
        scheduler.sendMessage(kernelMessage);
    }

    public KernelMessage waitForMessage() {
        return scheduler.waitForMessage();
    }

    public void getMapping(int virtualPageNumber) {
        scheduler.getMapping(virtualPageNumber);
    }

    public int allocateMemory(int size) {
        // "finds number of pages to add"...?
        boolean foundSpace = true;
        // Would we be using inUseIndex += size, or just inUseIndex++?
        for(int inUseIndex = 0; inUseIndex < pagesInUse.length; inUseIndex++) {
            for(int i = inUseIndex; i < inUseIndex + size; i++) {
                if(pagesInUse[inUseIndex]) {
                    foundSpace = false;
                    break;
                }
            }
            if(foundSpace) {
                // If we find the space to allocate memory, mark the pages as in use.
                for(int i = inUseIndex; i < inUseIndex + size; i++) {
                    pagesInUse[inUseIndex] = true;
                }
                // "returns correct value"?
                return 0;
            }
            foundSpace = true;
        }
        return 1;
    }

    public boolean freeMemory(int pointer, int size) {
        for(int p = pointer; p < size; p++) {
            pagesInUse[p] = false;
        }
        // removes mappings?
        return scheduler.freeMemory(pointer, size); // ??? idk???
    }
}
