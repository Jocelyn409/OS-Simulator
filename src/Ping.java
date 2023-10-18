public class Ping extends UserlandProcess {
    @Override
    public void run() {
        OS.sendMessage(new KernelMessage());
        System.out.println("Ping! ");
    }
}
