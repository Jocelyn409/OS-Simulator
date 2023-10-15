import java.io.IOException;
import java.io.RandomAccessFile;
import java.util.Arrays;

public class FakeFileSystem implements Device {
    private static FakeFileSystem singleton = null;
    private RandomAccessFile[] files;

    public static synchronized FakeFileSystem getInstance() {
        if(singleton == null) {
            singleton = new FakeFileSystem();
        }
        return singleton;
    }

    private FakeFileSystem() {
        files = new RandomAccessFile[10];
    }

    @Override
    public int Open(String filename) {
        if(filename.isEmpty()) {
            throw new IllegalArgumentException("No filename given to FakeFileSystem.");
        }
        System.out.println(filename);

        // Find an empty spot in the array and assign a new RandomAccessFile to it.
        for(int i = 0; i < 10; i++) {
            if(files[i] == null) {
                try {
                    files[i] = new RandomAccessFile(filename, "rw");
                    return i; // Return index since execution was successful.
                } catch(IOException e) {
                    return -1;
                }
            }
        }
        return -1; // Return -1 since execution failed (no empty spot in array).
    }

    @Override
    public void Close(int ID) {
        try {
            files[ID].close();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        System.out.println("Closed " + files[ID] + " in ID " + ID);
        files[ID] = null;
    }

    @Override
    public byte[] Read(int ID, int size) {
        byte[] readBytes = new byte[size];
        try {
            files[ID].read(readBytes);
        } catch(IOException e) {
            throw new RuntimeException("Error reading from file.");
        }
        System.out.println("Read " + Arrays.toString(readBytes) + " in ID " + ID);
        return readBytes;
    }

    @Override
    public int Write(int ID, byte[] data) {
        try {
            files[ID].write(data);
            System.out.println("Wrote " + Arrays.toString(data) + " to " + files[ID]);
        } catch(IOException e) {
            throw new RuntimeException("Error writing to file " + e);
        }
        return 0;
    }

    @Override
    public void Seek(int ID, int to) {
        try {
            files[ID].seek(to);
        } catch(IOException e) {
            throw new RuntimeException("Error seeking in file " + e);
        }
    }
}
