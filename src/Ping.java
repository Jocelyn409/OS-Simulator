public class Ping extends UserlandProcess {
    KernelMessage message;

    public Ping() {
        message = new KernelMessage();
        message.setMessageJob(0);
    }

    @Override
    public void run() {
        message.setTargetPID(OS.getPidByName("Pong"));
        OS.sendMessage(message);
        System.out.println("Ping from PID " + message.getSenderPID() +
                            " to PID " + message.getTargetPID() +
                            " with " + message.getMessageJob());
        OS.waitForMessage();
    }
}
