public abstract class UserlandProcess implements Runnable {

    public static byte[] memory;
    public static int[][] translationLookasideBuffer;

    public UserlandProcess() {
        memory = new byte[1048576]; // 1 MB.
        translationLookasideBuffer = new int[][] {{0, 0}, {0, 0}}; // Virtual address -> Physical address.
    }

    public byte readMemory(int virtualAddress) {
        int virtualPageNumber = virtualAddress / 1024;
        int pageOffset = virtualAddress % 1024;

        if(translationLookasideBuffer[virtualPageNumber][pageOffset] != 0) {
            int physicalAddress = translationLookasideBuffer[virtualPageNumber][pageOffset] * 1024 + pageOffset;
            System.out.println("Read " + memory[physicalAddress] + " from memory.");
            return memory[physicalAddress];
        }
        OS.getMapping(virtualPageNumber);
    }

    public void writeMemory(int virtualAddress, byte value) {
        int virtualPageNumber = virtualAddress / 1024;
        int pageOffset = virtualAddress % 1024;

        if(translationLookasideBuffer[virtualPageNumber][pageOffset] != 0) {
            int physicalAddress = translationLookasideBuffer[virtualPageNumber][pageOffset] * 1024 + pageOffset;
            memory[physicalAddress] = value;
            System.out.println("Wrote " + memory[physicalAddress] + " to memory.");
            return;
        }
        OS.getMapping(virtualPageNumber);
    }

    public static void clearTLB() {
        translationLookasideBuffer = new int[][]{{0, 0}, {0, 0}};
    }

    @Override
    public void run() {

    }

}
