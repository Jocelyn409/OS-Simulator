public abstract class UserlandProcess implements Runnable {

    public static byte[] memory;
    public static int[][] translationLookasideBuffer;

    public UserlandProcess() {
        memory = new byte[1048576]; // 1 MB.
        translationLookasideBuffer = new int[][] {{-1, -1}, {-1, -1}}; // Virtual pages -> Physical pages.
    }

    public byte readMemory(int virtualAddress) {
        int virtualPageNumber = virtualAddress / 1024;
        int pageOffset = virtualAddress % 1024;

        boolean flag = false;
        while(!flag) {
            if(translationLookasideBuffer[virtualPageNumber][pageOffset] != -1) {
                int physicalAddress = translationLookasideBuffer[virtualPageNumber][pageOffset] * 1024 + pageOffset;
                System.out.println("Read " + memory[physicalAddress] + " from memory.");
                return memory[physicalAddress];
            }
            OS.getMapping(virtualPageNumber);
            flag = true;
        }
        throw new RuntimeException("Was not able to read memory");
    }

    public void writeMemory(int virtualAddress, byte value) {
        int virtualPageNumber = virtualAddress / 1024;
        int pageOffset = virtualAddress % 1024;

        boolean flag = false;
        while(!flag) {
            if(translationLookasideBuffer[virtualPageNumber][pageOffset] != -1) {
                int physicalAddress = translationLookasideBuffer[virtualPageNumber][pageOffset] * 1024 + pageOffset;
                memory[physicalAddress] = value;
                System.out.println("Wrote " + memory[physicalAddress] + " to memory.");
                return;
            }
            OS.getMapping(virtualPageNumber);
            flag = true;
        }
        throw new RuntimeException("Was not able to write memory");
    }

    public static void clearMemory() {
        memory = new byte[1048576];
    }

    public static void clearTLB() {
        translationLookasideBuffer = new int[][]{{-1, -1}, {-1, -1}};
    }

    @Override
    public void run() {

    }
}
