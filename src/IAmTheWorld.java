public class IAmTheWorld extends UserlandProcess {
    @Override
    public void run() {
        while(true) {
            System.out.println("I am the World");
            OS.sleep(150);
        }
    }
}
