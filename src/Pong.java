public class Pong extends UserlandProcess {
    KernelMessage message = null;

    @Override
    public void run() {
        do {
            message = new KernelMessage(OS.waitForMessage());
            int tempInt = message.getMessageJob();
            message.setMessageJob(++tempInt);
            message.setTargetPID(OS.getPidByName("Ping"));
            OS.sendMessage(message);
            System.out.println("Pong from PID " + message.getSenderPID() +
                    " to PID " + message.getTargetPID() +
                    " with " + message.getMessageJob());
            OS.sleep(100);
        } while(message.getMessageJob() < 100);
    }
}
