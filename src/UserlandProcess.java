public abstract class UserlandProcess implements Runnable {

    public static byte[] memory;
    public static int[][] translationLookasideBuffer;

    public UserlandProcess() {
        memory = new byte[1048576]; // 1 MB.
        translationLookasideBuffer = new int[][] {{0, 0}, {0, 0}}; // Virtual address -> Physical address.
    }

    public byte readMemory(int address) {
        int virtualPage = address / 1024;
        int pageOffset = address % 1024;
        int physicalAddress = translationLookasideBuffer[virtualPage][pageOffset] * 1024 + pageOffset;

        while(true) {
            if(physicalAddress != pageOffset) { // idk how to tell if map is in TLB
                return memory[physicalAddress];
            }
            OS.getMapping(virtualPage);
        }
    }

    public void writeMemory(int address, byte value) {
        int virtualPage = address / 1024;
        int pageOffset = address % 1024;
        int physicalAddress = translationLookasideBuffer[virtualPage][pageOffset] * 1024 + pageOffset;

        while(true) {
            if(physicalAddress != pageOffset) { // idk how to tell if map is in TLB
                memory[physicalAddress] = value;
            }
            OS.getMapping(virtualPage);
        }
    }

    public static void clearTLB() {
        translationLookasideBuffer = new int[][]{{0, 0}, {0, 0}};
    }

    public static void clearMemory() {
        memory = new byte[1048576];
    }

    @Override
    public void run() {

    }

}
