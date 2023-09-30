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
                return 0; // Return 0 since execution was successful.
            }
        }
        return 1; // Return 1 since execution failed (no empty spot in array).
    }

    @Override
    public void Close(int id) {
        arrayRandom[id] = null;
    }

    @Override
    public byte[] Read(int id, int size) {
        byte[] randomBytes = new byte[size];
        arrayRandom[id].nextBytes(randomBytes);
        return randomBytes;
    }

    @Override
    public int Write(int id, byte[] data) {
        return 0;
    }

    @Override
    public void Seek(int id, int to) {
        Read(id, to);
    }
}
