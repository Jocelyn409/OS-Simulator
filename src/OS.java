public class OS {
    private static Kernel kernelInstance = null;

    public static void startup(UserlandProcess init) {
        if(kernelInstance == null) {
            kernelInstance = new Kernel();
            kernelInstance.createProcess(init);
        }
        else {
            throw new RuntimeException("Startup already initiated.");
        }
    }

    public static int createProcess(UserlandProcess up) {
        return kernelInstance.createProcess(up);
    }
}
