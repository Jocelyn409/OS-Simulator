import java.util.Arrays;

public abstract class UserlandProcess implements Runnable {

    public static byte[] memory;
    public static int[][] translationLookasideBuffer;

    public UserlandProcess() {
        memory = new byte[1048576]; // 1 MB.
        translationLookasideBuffer = new int[][] {{-1, -1}, {-1, -1}}; // Virtual pages -> Physical pages.
    }

    public byte readMemory(int virtualAddress) throws Exception {
        int virtualPageNumber = virtualAddress / 1024;
        int pageOffset = virtualAddress % 1024;
        int rowNumber = -1;

        int flag = 2;
        while(flag > 0) {
            if(translationLookasideBuffer[0][0] == virtualPageNumber) {
                rowNumber = 0;
            }
            else if(translationLookasideBuffer[1][0] == virtualPageNumber) {
                rowNumber = 1;
            }
            System.out.println(virtualPageNumber + " virtual page number\n"
                    + Arrays.deepToString(translationLookasideBuffer));
            if(rowNumber != -1) {
                int physicalAddress = translationLookasideBuffer[rowNumber][1] * 1024 + pageOffset;
                System.out.println("Read " + memory[physicalAddress] + " from memory.");
                return memory[physicalAddress];
            }
            try {
                OS.getMapping(virtualPageNumber);
            } catch(Exception exception) {
                System.out.println(exception + " reading memory.");
            }
            flag--;
        }
        throw new Exception("Was not able to read memory.");
    }

    public void writeMemory(int virtualAddress, byte value) throws Exception {
        int virtualPageNumber = virtualAddress / 1024;
        int pageOffset = virtualAddress % 1024;
        int rowNumber = -1;

        int flag = 2;
        while(flag > 0) {
            if(translationLookasideBuffer[0][0] == virtualPageNumber) {
                rowNumber = 0;
            }
            else if(translationLookasideBuffer[1][0] == virtualPageNumber) {
                rowNumber = 1;
            }
            System.out.println(virtualPageNumber + " is the virtual page number\n"
                    + Arrays.deepToString(translationLookasideBuffer));
            if(rowNumber != -1) {
                int physicalAddress = translationLookasideBuffer[rowNumber][1] * 1024 + pageOffset;
                memory[physicalAddress] = value;
                System.out.println("Wrote " + memory[physicalAddress] + " to memory.");
                return;
            }
            try {
                OS.getMapping(virtualPageNumber);
            } catch(Exception exception) {
                System.out.println(exception + " writing memory.");
            }
            flag--;
        }
        throw new Exception("Was not able to write memory.");
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
