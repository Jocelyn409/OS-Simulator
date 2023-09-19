public class HelloWorld extends UserlandProcess {
    @Override
    public void run() {
        while(true) {
            System.out.println("Hello World");
            try {
                OS.sleep(10000);
            } catch(Exception ignored) { }
        }
    }
}
