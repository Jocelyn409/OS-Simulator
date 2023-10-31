public abstract class UserlandProcess implements Runnable {

    private static byte[] memory;
    private static int[][] translationLookasideBuffer;

    public UserlandProcess() {
        memory = new byte[1048576];
        translationLookasideBuffer = new int[][]{}; // Virtual address -> Physical address.
    }

    public byte read(int address) {
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

    public void write(int address, byte value) {
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

    @Override
    public void run() {

    }

    public static byte[] getMemory() {
        return memory;
    }

    public static void setMemory(byte[] memory) {
        UserlandProcess.memory = memory;
    }
}
