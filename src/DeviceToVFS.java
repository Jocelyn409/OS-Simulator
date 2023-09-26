public class DeviceToVFS {
    private Device device;
    private int id;

    public DeviceToVFS(Device device, int id) {
        this.device = device;
        this.id = id;
    }

    public Device getDevice() {
        return device;
    }

    public void setDevice(Device device) {
        this.device = device;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }
}
