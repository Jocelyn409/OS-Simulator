public class DeviceToVFS {
    private Device device;
    private int ID;

    public DeviceToVFS(Device device, int ID) {
        this.device = device;
        this.ID = ID;
    }

    public Device getDevice() {
        return device;
    }

    public void setDevice(Device device) {
        this.device = device;
    }

    public int getID() {
        return ID;
    }

    public void setID(int id) {
        this.ID = id;
    }
}
