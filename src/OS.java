public class OS {
    private static Kernel kernelInstance = null;

    public static void startup(UserlandProcess init, Priority.Level level) {
        if(kernelInstance == null) {
            kernelInstance = new Kernel();
            kernelInstance.createProcess(init, level);
            int index0 = kernelInstance.Open("file toots.dat");
            kernelInstance.Read(index0, 10);
            int index1 = kernelInstance.Open("random 503");
            kernelInstance.Read(index1, 10);
            createProcess(new IAmTheWorld());
            createProcess(new GoodbyeWorld(), Priority.Level.Background);
            byte[] test = new byte[] {10, 51, 43, 9, -1};
            kernelInstance.Write(index1, test);
            createProcess(new RealTimeProcess(), Priority.Level.RealTime);
            kernelInstance.Close(index1);
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

    public static void sleep(int milliseconds) {
        kernelInstance.sleep(milliseconds);
    }

    public static int createProcess(UserlandProcess up) {
        return kernelInstance.createProcess(up);
    }

    public static int createProcess(UserlandProcess up, Priority.Level level) {
        return kernelInstance.createProcess(up, level);
    }
}
