public class Ping extends UserlandProcess {
    KernelMessage message;

    public Ping() {
        message = new KernelMessage();
    }

    @Override
    public void run() {
        while(message.getMessageJob() < 100) {
            message.setTargetPID(OS.getPidByName("Pong"));
            OS.sendMessage(message);
            System.out.println("Ping from PID " + message.getSenderPID() +
                    " to PID " + message.getTargetPID() +
                    " with " + message.getMessageJob());
            OS.sleep(100);
            message = OS.waitForMessage();
        }
    }
}
