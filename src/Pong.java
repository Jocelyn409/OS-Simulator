public class Pong extends UserlandProcess {
    KernelMessage message;

    @Override
    public void run() {
        message = new KernelMessage(OS.waitForMessage());
        message.setMessageJob(message.incrementMessageJob());
        message.setTargetPID(OS.getPidByName("Ping"));
        OS.sendMessage(message);
        System.out.println("Pong from PID " + message.getSenderPID() +
                            " to PID " + message.getTargetPID() +
                            " with " + message.getMessageJob());
    }
}
