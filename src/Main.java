public class Main {
    public static void main(String[] args) {
        OS.startup(new HelloWorld(), Priority.Level.RealTime);
        OS.createProcess(new GoodbyeWorld(), Priority.Level.Background);
        OS.createProcess(new RealTimeProcess(), Priority.Level.RealTime);
        OS.createProcess(new InteractiveProcess(), Priority.Level.Interactive);
        OS.createProcess(new Background(), Priority.Level.Background);
    }
}