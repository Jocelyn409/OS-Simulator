public class OS {
    private static Kernel kernelInstance = null;

    public static void startup(UserlandProcess init, Priority.Level level) {
        if(kernelInstance == null) {
            kernelInstance = new Kernel();
            kernelInstance.createProcess(init, level);
            createProcess(new IAmTheWorld());
            createProcess(new GoodbyeWorld(), Priority.Level.Background);
            createProcess(new RealTimeProcess(), Priority.Level.RealTime);
            createProcess(new InteractiveProcess(), Priority.Level.Interactive);
            createProcess(new BackgroundProcess(), Priority.Level.Background);
            createProcess(new Ping(), Priority.Level.RealTime);
            createProcess(new Pong(), Priority.Level.RealTime);
            createProcess(new TransferText());
        }
        else {
            throw new RuntimeException("Startup already initiated.");
        }
    }

    public static void startup(UserlandProcess init) {
        startup(init, Priority.Level.Interactive);
    }

    public static void sleep(int milliseconds) {
        kernelInstance.sleep(milliseconds);
    }

    public static int createProcess(UserlandProcess up) {
        return kernelInstance.createProcess(up);
    }

    public static int createProcess(UserlandProcess up, Priority.Level level) {
        return kernelInstance.createProcess(up, level);
    }

    public static int Open(String s) {
        return kernelInstance.Open(s);
    }

    public static void Close(int ID) {
        kernelInstance.Close(ID);
    }

    public static byte[] Read(int ID, int size) {
        return kernelInstance.Read(ID, size);
    }

    public static int Write(int ID, byte[] data) {
        return kernelInstance.Write(ID, data);
    }

    public static void Seek(int ID, int to) {
        kernelInstance.Seek(ID, to);
    }

    public static int getPid() {
        return kernelInstance.getPid();
    }

    public static int getPidByName(String input) {
        return kernelInstance.getPidByName(input);
    }

    public static void sendMessage(KernelMessage kernelMessage) {
        kernelInstance.sendMessage(kernelMessage);
    }

    public static KernelMessage waitForMessage() {
        return kernelInstance.waitForMessage();
    }
}
