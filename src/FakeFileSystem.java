import java.io.IOException;
import java.io.RandomAccessFile;

public class FakeFileSystem implements Device {
    private RandomAccessFile[] randomFiles;

    public FakeFileSystem() {
        randomFiles = new RandomAccessFile[10];
    }

    @Override
    public int Open(String filename) {
        if(filename.isEmpty()) { // this goes here or?
            throw new IllegalArgumentException("No filename given to FakeFileSystem.");
        }
        for(int i = 0; i < 10; i++) {
            if(randomFiles[i] == null) {
                try(RandomAccessFile file = new RandomAccessFile(filename, "rw")) {
                    randomFiles[i] = file;
                } catch(IOException e) {
                    throw new RuntimeException(e);
                }
                return i; // Return index since execution was successful.
            }
        }
        return -1; // Return -1 since execution failed (no empty spot in array).
    }

    @Override
    public void Close(int ID) {
        try {
            randomFiles[ID].close();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        randomFiles[ID] = null;
    }

    @Override
    public byte[] Read(int ID, int size) {
        byte[] readBytes = new byte[size];
        try {
            randomFiles[ID].read(readBytes);
        } catch(IOException e) {
            throw new RuntimeException(e);
        }
        return readBytes;
    }

    @Override
    public int Write(int ID, byte[] data) {
        try {
            randomFiles[ID].write(data);
        } catch(IOException e) {
            return -1;
        }
        return 0;
    }

    @Override
    public void Seek(int ID, int to) {
        try {
            randomFiles[ID].seek(to);
        } catch(IOException e) {
            throw new RuntimeException(e);
        }
    }
}
