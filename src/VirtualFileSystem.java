public class VirtualFileSystem implements Device {
    private DeviceToVFS[] deviceMap;

    public VirtualFileSystem() {
        deviceMap = new DeviceToVFS[10];
    }

    @Override
    public int Open(String input) {
        String[] splitInput = input.split(" ", 2);
        String chosenDeviceString = splitInput[0];
        Device chosenDevice;
        switch(chosenDeviceString) {
            case "random" -> chosenDevice = new RandomDevice();
            case "file" -> chosenDevice = new FakeFileSystem();
            default -> {
                throw new RuntimeException("VFS could not create device " + chosenDeviceString);
            }
        }
        for(int i = 0; i < 10; i++) {
            if(deviceMap[i] == null) {
                deviceMap[i] = new DeviceToVFS(chosenDevice, -1);
                deviceMap[i].setID(deviceMap[i].getDevice().Open(splitInput[1]));
                return i; // Return index since execution was successful.
            }
        }
        return -1; // Return -1 since execution failed.
    }

    @Override
    public void Close(int ID) {
        deviceMap[ID].getDevice().Close(deviceMap[ID].getID());
        deviceMap[ID] = null;
    }

    @Override
    public byte[] Read(int ID, int size) {
        return deviceMap[ID].getDevice().Read(deviceMap[ID].getID(), size);
    }

    @Override
    public int Write(int ID, byte[] data) {
        return deviceMap[ID].getDevice().Write(deviceMap[ID].getID(), data);
    }

    @Override
    public void Seek(int ID, int to) {
        deviceMap[ID].getDevice().Seek(deviceMap[ID].getID(), to);
    }
}
