import java.io.IOException;
import java.io.RandomAccessFile;

public class FakeFileSystem implements Device {
    private RandomAccessFile[] randomFiles;

    public FakeFileSystem(String filename) { // might not be string, might be a literal file?
        randomFiles = new RandomAccessFile[10];
        if(filename.isEmpty()) {
            throw new IllegalArgumentException("No filename given to FakeFileSystem.");
        }
    }

    @Override
    public int Open(String name) {
        for(int i = 0; i < 10; i++) {
            if(randomFiles[i] == null) {
                // rw or rws or rwd?
                try(RandomAccessFile file = new RandomAccessFile(name, "rw")) {
                    randomFiles[i] = file;
                } catch(IOException e) {
                    throw new RuntimeException(e);
                }
                return 0; // Return 0 since execution was successful.
            }
        }
        return 1; // Return 1 since execution failed (no empty spot in array).
    }

    @Override
    public void Close(int id) {
        for(int i = 0; i < 10; i++) {
            try {
                randomFiles[i].close();
                randomFiles[i] = null;
            } catch(IOException e) {
                throw new RuntimeException(e);
            }
        }
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
