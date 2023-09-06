public class Main {
    public static void main(String[] args) {
        OS.startup(new HelloWorld(), Priority.Level.RealTime);
        OS.sleep(5000);
        OS.createProcess(new GoodbyeWorld());
        OS.createProcess(new HelloWorld());
        OS.sleep(5000);
    }
}