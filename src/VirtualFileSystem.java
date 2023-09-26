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
            case "file" -> chosenDevice = new FakeFileSystem(chosenDeviceString);
            default -> {
                return 1; // maybe not? maybe set it to null?
            }
        }
        // IT DOESNT FEEL LIKE WE ARE DOING ANYTHING WITH THE ID????


        // assign the chosenDevice to the array. that determines its position i think.
        // again, why cant we just use a hashmap?
        for(int i = 0; i < 10; i++) {
            if(deviceMap[i] == null) {
                deviceMap[i].setDevice(chosenDevice);
                deviceMap[i].getDevice().Open(splitInput[1]);
                return 0; // Return 0 since execution was successful.
            }
        }
        return 1;
    }

    @Override
    public void Close(int id) {
        deviceMap[id] = null;
    }

    @Override
    public byte[] Read(int id, int size) {
        return deviceMap[id].getDevice().Read(id, size);
    }

    @Override
    public int Write(int id, byte[] data) {
        return deviceMap[id].getDevice().Write(id, data);
    }

    @Override
    public void Seek(int id, int to) {
        deviceMap[id].getDevice().Seek(id, to);
    }
}
