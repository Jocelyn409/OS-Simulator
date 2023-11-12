import java.util.Random;

public class MemoryTesting extends UserlandProcess {
    @Override
    public void run() {
        Random randomBytes = new Random();
        byte[] byteValue = new byte[7];
        randomBytes.nextBytes(byteValue);

        OS.allocateMemory(2048);

        writeMemory(1024, byteValue[0]);
        readMemory(1024);

        writeMemory(3072, byteValue[1]);
    }
}
