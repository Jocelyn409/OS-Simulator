public class VirtualFileSystem implements Device {
    private DeviceToVFS[] deviceMap;
    private static int ID = 0;

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
                return -1; // Return -1 since execution failed.
            }
        }
        for(int i = 0; i < 10; i++) {
            if(deviceMap[i] == null) {
                deviceMap[i] = new DeviceToVFS(chosenDevice, ID);
                deviceMap[i].getDevice().Open(splitInput[1]);
                return ID++; // Return ID since execution was successful.
            }
        }
        return -1; // Return -1 since execution failed.
    }

    @Override
    public void Close(int ID) {
        deviceMap[ID] = null;
    }

    @Override
    public byte[] Read(int ID, int size) {
        return deviceMap[ID].getDevice().Read(ID, size);
    }

    @Override
    public int Write(int ID, byte[] data) {
        return deviceMap[ID].getDevice().Write(ID, data);
    }

    @Override
    public void Seek(int ID, int to) {
        deviceMap[ID].getDevice().Seek(ID, to);
    }
}
