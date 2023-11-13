import java.util.Random;

public class MemoryTesting2 extends UserlandProcess {
    @Override
    public void run() {
        Random randomBytes = new Random();
        byte[] byteValue = new byte[2];
        randomBytes.nextBytes(byteValue);

        try {
            writeMemory(4096, byteValue[0]); // Will cause a seg fault.
        } catch (Exception e) {
            System.out.println(e);
        }
        try {
            OS.freeMemory(4096, 1024);
        } catch (Exception e) {
            System.out.println(e);
        }

        // This will cause a seg fault. This is to prove that the memory
        // written in MemoryTesting won't transfer over and be read over here.
        try {
            readMemory(2048);
        } catch (Exception e) {
            System.out.println(e);
        }
    }
}
