public class GoodbyeWorld extends UserlandProcess {
    @Override
    public void run() {
        while(true) {
            System.out.println("Goodbye World");
            OS.sleep(500);
        }
    }
}
