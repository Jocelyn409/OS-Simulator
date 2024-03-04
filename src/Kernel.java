import java.util.Arrays;

public class Kernel implements Device {
    private Scheduler scheduler;
    private VirtualFileSystem VFS;
    private KernelandProcess runningProcess;
    private boolean[] pagesInUse;
    private int swapFile;
    private int pageNumber;

    public Kernel() {
        scheduler = new Scheduler();
        VFS = new VirtualFileSystem();
        runningProcess = null;
        pagesInUse = new boolean[1024]; // 1024 (number of pages).
    }

    public void startup(UserlandProcess init, Priority.Level level) {
        swapFile = VFS.Open("file swapfile.txt");
        createProcess(init, level);
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

    public void getMapping(int virtualPageNumber) throws Exception {
        scheduler.getMapping(virtualPageNumber);
    }

    public int getSwapFile() {
        return swapFile;
    }

    public int findNotInUsePage() {
        for(int i = 0; i < 1024; i++) {
            if(!pagesInUse[i]) {
                return i;
            }
        }
        return -1;
    }

    public void setInUsePage(int input) {
        pagesInUse[input] = true;
    }

     public int allocateMemory(int size) {
        int pagesToAdd = size/1024;
        boolean foundSpace = true;
        runningProcess = scheduler.getRunningProcess();
        VirtualToPhysicalMapping[] runningProcessPages = runningProcess.getPhysicalPages();

        for(int processPagesInUseIndex = 0; processPagesInUseIndex < runningProcessPages.length; processPagesInUseIndex++) {
            // Looks through current segment in the runningProcess's pages to see if gap is wide enough to allocate.
            for(int i = processPagesInUseIndex; i < processPagesInUseIndex + pagesToAdd; i++) {
                if(runningProcessPages[processPagesInUseIndex] != null) {
                    // If any one of these pages is in use, break out of the loop
                    // and indicate that no space was found to allocate.
                    foundSpace = false;
                    break;
                }
            }
            if(foundSpace) {
                // If we find the space to allocate memory, mark the pages as in use.
                int inUseIndex = 0;
                for(int i = processPagesInUseIndex; i < processPagesInUseIndex + pagesToAdd; i++) {
                    while(inUseIndex < pagesInUse.length) {
                        if(runningProcessPages[i] == null) {
                            runningProcessPages[i] = new VirtualToPhysicalMapping();
                            pagesInUse[inUseIndex] = true;
                        }
                        inUseIndex++;
                    }
                }
                runningProcess.setPhysicalPages(runningProcessPages);
                System.out.println("Memory allocated: \n" + Arrays.toString(pagesInUse));
                System.out.println("Memory allocated: \n" + Arrays.toString(runningProcessPages));
                return processPagesInUseIndex;
            }
            else {
                foundSpace = true;
            }
        }
        System.out.println("Memory not allocated.");
        return -1;
    }

    public boolean freeMemory(int pointer, int size) throws Exception {
        int pagePointer = pointer/1024;
        int pagesToRemove = size/1024;
        runningProcess = scheduler.getRunningProcess();
        VirtualToPhysicalMapping[] runningProcessPages = runningProcess.getPhysicalPages();

        System.out.println("pagesInUse before freeing:    " + Arrays.toString(pagesInUse)
                + "\nphysicalPages before freeing: " + Arrays.toString(runningProcessPages));

        for(int inUseIndex = pagePointer; inUseIndex < pagePointer + pagesToRemove; inUseIndex++) {
            if(!pagesInUse[inUseIndex]) {
                throw new Exception("Couldn't free memory since page is not in use.");
            }
            pagesInUse[inUseIndex] = false; // Mark the virtual page as no longer in use.
            for(int i = 0; i < runningProcessPages.length; i++) {
                // This for loop removes the mapping from the process's pages by finding
                // any spots in the runningProcessPages that are the same as the inUseIndex
                if(runningProcessPages[i] == null || runningProcessPages[i].getPhysicalPageNumber() == -1) {
                    throw new Exception("Couldn't free memory since process page is null or physical page is -1.");
                }
                if(runningProcessPages[i].getPhysicalPageNumber() == inUseIndex) {
                    runningProcessPages[i] = null;
                }
            }
        }
        runningProcess.setPhysicalPages(runningProcessPages);
        System.out.println("pagesInUse after freeing:     " + Arrays.toString(pagesInUse)
                + "\nphysicalPages after freeing:  " + Arrays.toString(runningProcessPages));
        return true;
    }
}
