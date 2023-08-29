public class OS {
    private static Kernel kernelInstance;

    public static void sleep(int milliseconds) {
        kernelInstance.sleep(milliseconds);
    }

    public static void startup(UserlandProcess init) {
        kernelInstance = new Kernel();
        kernelInstance.createProcess(init);
    }

    public static int createProcess(UserlandProcess up) {
        return kernelInstance.createProcess(up);
    }
}
