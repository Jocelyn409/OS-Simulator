public class VirtualFileSystem implements Device {
    private DeviceToVFS[] deviceMap;

    public VirtualFileSystem() {
        deviceMap = new DeviceToVFS[10];
    }

    @Override
    public int Open(String input) {
        String[] splitInput = input.split(" ", 2);
        Device chosenDevice;

        // Open a device based on the input.
        switch(splitInput[0].toLowerCase()) {
            case "random" -> chosenDevice = RandomDevice.getInstance();
            case "file" -> chosenDevice = FakeFileSystem.getInstance();
            default -> {
                throw new RuntimeException("VFS could not create device " + splitInput[0]);
            }
        }

        // Find an empty spot in the array and assign a new DeviceToVFS to it.
        for(int i = 0; i < 10; i++) {
            if(deviceMap[i] == null) {
                deviceMap[i] = new DeviceToVFS(chosenDevice);
                // Set the ID with the index from opening the device.
                deviceMap[i].setID(deviceMap[i].getDevice().Open(splitInput[1]));
                System.out.println("Opened device " + splitInput[0] + " " + splitInput[1]);
                return i; // Return index since execution was successful.
            }
        }
        return -1; // Return -1 since execution failed.
    }

    @Override
    public void Close(int ID) {
        try {
            deviceMap[ID].getDevice().Close(deviceMap[ID].getID());
            deviceMap[ID] = null;
        } catch(ArrayIndexOutOfBoundsException ignore) { }
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
