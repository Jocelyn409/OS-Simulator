public class VirtualToPhysicalMapping {
    public int physicalPageNumber;
    public int diskPageNumber;

    public VirtualToPhysicalMapping() {
        physicalPageNumber = -1;
        diskPageNumber = -1;
    }

    public int getPhysicalPageNumber() {
        return physicalPageNumber;
    }

    public void setPhysicalPageNumber(int physicalPageNumber) {
        this.physicalPageNumber = physicalPageNumber;
    }

    public int getDiskPageNumber() {
        return diskPageNumber;
    }

    public void setDiskPageNumber(int diskPageNumber) {
        this.diskPageNumber = diskPageNumber;
    }
}
