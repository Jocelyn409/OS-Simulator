import java.util.LinkedList;
import java.util.Timer;
import java.util.TimerTask;

public class Scheduler {
    private LinkedList<KernelandProcess> processLinkedList = new LinkedList<>();
    private KernelandProcess runningProcess = null;
    private Timer timer;
    private TimerTask timerTask;
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

    // Create and add new process to LL; if there is no running process,
    // call switchProcess() to run the new process.
    public int createProcess(UserlandProcess up) {
        KernelandProcess newProcess = new KernelandProcess(up, PID++);
        processLinkedList.add(newProcess);
        if(runningProcess == null) {
            switchProcess();
        }
        return PID;
    }

    // Stop running process if there is one; add it to the end of the LL
    // if it hasn't finished, then run first process in LL.
    public void switchProcess() {
        if(runningProcess != null) {
            // If there is a running process, stop it.
            runningProcess.stop();
            int index = processLinkedList.indexOf(runningProcess); // Get index of the stopped process.
            runningProcess = null; // Make runningProcess null as it was stopped.
            KernelandProcess removedProcess = processLinkedList.remove(index); // Remove process from LL since it was stopped.
            if(!(removedProcess.isDone())) {
                // If the process did not finish, add it back to the end of the LL.
                processLinkedList.add(removedProcess);
            }
        }
        // Run first process in LL and assign runningProcess to it.
        processLinkedList.get(0).run();
        runningProcess = processLinkedList.get(0);
    }
}
