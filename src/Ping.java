public class Ping extends UserlandProcess {
    KernelMessage message;

    public Ping() {
        message = new KernelMessage();
    }

    @Override
    public void run() {
        while(message.getMessageJob() < 10) {
            message.setTargetPID(OS.getPidByName("Pong"));
            OS.sendMessage(message);
            System.out.println("Ping from PID " + message.getSenderPID() +
                    " to PID " + message.getTargetPID() +
                    " with " + message.getMessageJob());
            message = OS.waitForMessage();
            OS.sleep(100);
        }
    }
}
