public class OS {
    public static Kernel kernelInstance;

    public static void startup(UserlandProcess init) {
        kernelInstance = new Kernel();
        Kernel.createProcess(init);
    }

    public static int createProcess(UserlandProcess up) {
        return Kernel.createProcess(up);
    }
}
