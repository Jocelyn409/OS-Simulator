import java.util.Arrays;
import java.util.Random;

public class RandomDevice implements Device {
    private static RandomDevice singleton = null;
    private Random[] arrayRandom;

    public static synchronized RandomDevice getInstance() {
        if(singleton == null) {
            singleton = new RandomDevice();
        }
        return singleton;
    }

    private RandomDevice() {
        arrayRandom = new Random[10];
    }

    @Override
    public int Open(String seed) {
        Random arrayInput;
        if(!seed.isEmpty()) {
            // If the seed isn't empty, make arrayInput based on parsing it as an integer.
            arrayInput = new Random(Integer.parseInt(seed));
        }
        else {
            arrayInput = new Random();
        }

        // Find an empty spot in the array and assign arrayInput to it.
        for(int i = 0; i < 10; i++) {
            if(arrayRandom[i] == null) {
                arrayRandom[i] = arrayInput;
                System.out.println("Opened RandomDevice with ID " + i);
                return i; // Return index since execution was successful.
            }
        }
        return -1; // Return -1 since execution failed (no empty spot in array).
    }

    @Override
    public void Close(int ID) {
        System.out.println("Closed " + arrayRandom[ID] + " in ID " + ID);
        arrayRandom[ID] = null;
    }

    @Override
    public byte[] Read(int ID, int size) {
        byte[] randomBytes = new byte[size];
        arrayRandom[ID].nextBytes(randomBytes);
        System.out.println("Read " + Arrays.toString(randomBytes) + " in ID " + ID);
        return randomBytes;
    }

    @Override
    public int Write(int ID, byte[] data) {
        System.out.println("Nothing written to RandomDevice.");
        return 0;
    }

    @Override
    public void Seek(int ID, int to) {
        Read(ID, to);
    }
}
