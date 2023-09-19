public class HelloWorld extends UserlandProcess {
    @Override
    public void run() {
        while(true) {
            System.out.println("Hello World");
            try {
                OS.sleep(2000);
            } catch(Exception ignored) { }
        }
    }
}
