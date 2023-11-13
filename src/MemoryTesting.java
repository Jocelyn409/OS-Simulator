import java.util.Random;

public class MemoryTesting extends UserlandProcess {
    @Override
    public void run() {
        Random randomBytes = new Random();
        byte[] byteValue = new byte[2];
        randomBytes.nextBytes(byteValue);

        // Base case.
        OS.allocateMemory(3072);
        try {
            writeMemory(2048, byteValue[0]);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        try {
            readMemory(2048);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        try {
            OS.freeMemory(1024, 1024);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
