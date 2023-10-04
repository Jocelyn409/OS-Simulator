import java.util.Random;

public class RandomDevice implements Device {
    private Random[] arrayRandom;

    public RandomDevice() {
        arrayRandom = new Random[10];
    }

    @Override
    public int Open(String seed) {
        Random arrayInput;
        if(!seed.isEmpty()) {
            arrayInput = new Random(Integer.parseInt(seed));
        }
        else {
            arrayInput = new Random();
        }
        for(int i = 0; i < 10; i++) {
            if(arrayRandom[i] == null) {
                arrayRandom[i] = arrayInput;
                return i; // Return index since execution was successful.
            }
        }
        return -1; // Return -1 since execution failed (no empty spot in array).
    }

    @Override
    public void Close(int ID) {
        arrayRandom[ID] = null;
    }

    @Override
    public byte[] Read(int ID, int size) {
        byte[] randomBytes = new byte[size];
        arrayRandom[ID].nextBytes(randomBytes);
        return randomBytes;
    }

    @Override
    public int Write(int ID, byte[] data) {
        return 0;
    }

    @Override
    public void Seek(int ID, int to) {
        Read(ID, to);
    }
}
