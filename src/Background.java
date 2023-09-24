public class Background extends UserlandProcess {
    @Override
    public void run() {
        while(true) {
            try {
                Thread.sleep(250);
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
            System.out.println("Background Process");
        }
    }
}
