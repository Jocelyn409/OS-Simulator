import java.util.Arrays;

public class HelloWorld extends UserlandProcess {
    @Override
    public void run() {
        while(true) {
            System.out.println("Hello World");
            OS.sleep(50);
        }
    }
}
