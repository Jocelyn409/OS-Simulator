public class RealTimeProcess extends UserlandProcess {
    @Override
    public void run() {
        while(true) {
            try {
                Thread.sleep(250);
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
            System.out.println("Real-Time Process");
        }
    }
}
