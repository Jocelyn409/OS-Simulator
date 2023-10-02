import java.io.IOException;
import java.io.RandomAccessFile;

public class FakeFileSystem implements Device {
    private RandomAccessFile[] randomFiles;
    private static int ID = 0;

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
                return ID++; // Return ID since execution was successful.
            }
        }
        return -1; // Return -1 since execution failed (no empty spot in array).
    }

    @Override
    public void Close(int id) {
        try {
            randomFiles[id].close();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        randomFiles[id] = null;
    }

    @Override
    public byte[] Read(int id, int size) {
        byte[] readBytes = new byte[size];
        try {
            randomFiles[id].read(readBytes);
        } catch(IOException e) {
            throw new RuntimeException(e);
        }
        return readBytes;
    }

    @Override
    public int Write(int id, byte[] data) {
        try {
            randomFiles[id].write(data);
        } catch(IOException e) {
            return -1;
        }
        return 0;
    }

    @Override
    public void Seek(int id, int to) {
        try {
            randomFiles[id].seek(to);
        } catch(IOException e) {
            throw new RuntimeException(e);
        }
    }
}
