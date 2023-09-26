public class VirtualFileSystem implements Device {
    private Device[] deviceMap;
    private int[] intMap;

    public VirtualFileSystem() {
        // cant we just use a hashmap?????
        deviceMap = new Device[10]; // do we fill this or does open fill it?
        intMap = new int[10];
    }

    @Override
    public int Open(String input) {
        String[] splitInput = input.split(" ", 2);
        String chosenDevice = splitInput[0];

        // assign the chosenDevice to the array. that determines its position i think.
        // again, why cant we just use a hashmap?

        deviceMap[0].Open(splitInput[1]);
        return 0;
    }

    @Override
    public void Close(int id) {
        deviceMap[id].Close(id);
        //intMap[id] = null;
    }

    @Override
    public byte[] Read(int id, int size) {
        return deviceMap[id].Read(id, size);
    }

    @Override
    public int Write(int id, byte[] data) {
        return deviceMap[id].Write(id, data);
    }

    @Override
    public void Seek(int id, int to) {
        deviceMap[id].Seek(id, to);
    }
}
