import java.util.LinkedList;
import java.util.Timer;
import java.util.TimerTask;

public class Scheduler {
    private LinkedList<KernelandProcess> processLinkedList = new LinkedList<>();
    private Timer timer;
    private TimerTask timerTask;
    private KernelandProcess runningKernelandProcess = null;
    private int PID = 0;

    public Scheduler() {
        timer = new Timer();
        timerTask = new Interrupt();
        timer.schedule(timerTask, 250, 250);
    }

    private class Interrupt extends TimerTask {
        @Override
        public void run() {
            switchProcess();
        }
    }

    public int createProcess(UserlandProcess up) {
        KernelandProcess newProcess = new KernelandProcess(up, ++PID);
        processLinkedList.add(newProcess);
        if(runningKernelandProcess == null) {
            switchProcess();
        }
        return PID;
    }

    public void switchProcess() {
        if(runningKernelandProcess != null) {
            // If there is a running process, stop it.
            runningKernelandProcess.stop();
            int index = processLinkedList.indexOf(runningKernelandProcess); // Index of running process.
            if(!(processLinkedList.get(index).isDone())) {
                // If running process is not done, add it to end of the LL.
                processLinkedList.add(processLinkedList.remove(index));
            }
        }
        processLinkedList.get(0).run(); // put exception here?
        runningKernelandProcess = processLinkedList.get(0);
    }
}
