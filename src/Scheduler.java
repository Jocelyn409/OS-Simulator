import java.util.LinkedList;
import java.util.Timer;
import java.util.TimerTask;
import java.time.Clock;

public class Scheduler {
    private LinkedList<KernelandProcess> processLinkedList = new LinkedList<>();
    private LinkedList<KernelandProcess> sleepingProcesses = new LinkedList<>();
    private KernelandProcess runningProcess = null;
    private Timer timer;
    private TimerTask timerTask;
    private int PID = 0;
    private Clock clock;

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

    private void moveToSleepQueue(int PID) {
        for(int index = 0; index < processLinkedList.size(); index++) {
            if(processLinkedList.get(index).PID == PID) {
                sleepingProcesses.add(processLinkedList.remove(index));
                return;
            }
        }
    }

    public void sleep(int milliseconds) {
        // add milliseconds to clock.millis().
        // wherever we wake up the process, make sure that the above is <= a new millis().

        sleepingProcesses.add(
                processLinkedList.remove(
                        processLinkedList.indexOf(runningProcess)));

        KernelandProcess temp = runningProcess;
        runningProcess = null;
        temp.stop();

        switchProcess();
    }

    // Create and add new process to LL; if there is no running process, call switchProcess().
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
            if(!(processLinkedList.get(index).isDone())) {
                // If the process did not finish, add it to end of the LL.
                processLinkedList.add(processLinkedList.remove(index));
            }
            runningProcess = null; // Make runningProcess null as it was stopped.
        }
        processLinkedList.get(0).run(); // Run first process in LL.
        runningProcess = processLinkedList.get(0);
    }
}
