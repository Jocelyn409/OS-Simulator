public class GoodbyeWorld extends UserlandProcess {
    @Override
    public void run() {
        while(true) {
            System.out.println("Goodbye World");
            try {
                OS.sleep(50);
            } catch(Exception ignored) { }
        }
    }
}
