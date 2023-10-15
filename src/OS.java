public class OS{
    private static Kernel kernelInstance = null;

    public static void startup(UserlandProcess init, Priority.Level level) {
        if(kernelInstance == null) {
            kernelInstance = new Kernel();
            kernelInstance.createProcess(init, level);
            createProcess(new TransferText());
            createProcess(new GoodbyeWorld(), Priority.Level.Background);
            createProcess(new RealTimeProcess(), Priority.Level.RealTime);
            createProcess(new InteractiveProcess(), Priority.Level.Interactive);
            createProcess(new BackgroundProcess(), Priority.Level.Background);
        }
        else {
            throw new RuntimeException("Startup already initiated.");
        }
    }

    public static void startup(UserlandProcess init) {
        startup(init, Priority.Level.Interactive);
    }

    public static int createProcess(UserlandProcess up) {
        return kernelInstance.createProcess(up);
    }

    public static int createProcess(UserlandProcess up, Priority.Level level) {
        return kernelInstance.createProcess(up, level);
    }

    public static void sleep(int milliseconds) {
        kernelInstance.sleep(milliseconds);
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
}
