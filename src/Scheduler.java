import java.util.LinkedList;
import java.util.Timer;
import java.util.TimerTask;
import java.time.Clock;

public class Scheduler {
    private LinkedList<KernelandProcess>[] priorityArray;
    private LinkedList<KernelandProcess> realTimeProcesses;
    private LinkedList<KernelandProcess> interactiveProcesses;
    private LinkedList<KernelandProcess> backgroundProcesses;
    private LinkedList<KernelandProcess> sleepingProcesses = new LinkedList<>();
    private KernelandProcess runningProcess = null;
    private Timer timer;
    private TimerTask timerTask;
    private int PID = 0;
    private Clock clock;

    public Scheduler() {
        priorityArray = new LinkedList[3];
        priorityArray[0] = realTimeProcesses = new LinkedList<>();
        priorityArray[1] = interactiveProcesses = new LinkedList<>();
        priorityArray[2] = backgroundProcesses = new LinkedList<>();
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

    private void awakenProcesses() {
        // wherever we wake up the process, make sure that the above is <= a new millis() ! ! !
        for(int arrayIndex = 0; arrayIndex <= 2; arrayIndex++) {
            for(int listIndex = 0; listIndex < _; listIndex++) {

            }
        }
    }

    public void sleep(int milliseconds) {
        // Process will sleep until the current time plus the added time.
        runningProcess.setSleepUntil(clock.millis() + milliseconds);

        // Find and remove process from its list, then add it to the sleeping process list.
        switch(runningProcess.getLevel()) {
            case RealTime ->    sleepingProcesses.add(
                    realTimeProcesses.remove(realTimeProcesses.indexOf(runningProcess)));
            case Interactive -> sleepingProcesses.add(
                    interactiveProcesses.remove(interactiveProcesses.indexOf(runningProcess)));
            case Background ->  sleepingProcesses.add(
                    backgroundProcesses.remove(backgroundProcesses.indexOf(runningProcess)));
        }

        // Stop process
        KernelandProcess temp = runningProcess;
        runningProcess = null;
        temp.stop();

        switchProcess();
    }

    // Create and add new process to LL; if there is no running process, call switchProcess().
    public int createProcess(UserlandProcess up, Priority.Level level) {
        KernelandProcess newProcess = new KernelandProcess(up, PID++, level);

        // Add process to correct list.
        switch(newProcess.getLevel()) {
            case RealTime -> realTimeProcesses.add(newProcess);
            case Interactive -> interactiveProcesses.add(newProcess);
            case Background -> backgroundProcesses.add(newProcess);
        }
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
