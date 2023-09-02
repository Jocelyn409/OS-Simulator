import java.util.LinkedList;
import java.util.Timer;
import java.util.TimerTask;
import java.time.Clock;
import java.util.Random;

public class Scheduler {
    private LinkedList<KernelandProcess>[] processListsArray;
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
        processListsArray = new LinkedList[3];
        processListsArray[0] = realTimeProcesses = new LinkedList<>();
        processListsArray[1] = interactiveProcesses = new LinkedList<>();
        processListsArray[2] = backgroundProcesses = new LinkedList<>();
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
        for(int i = 0; i < sleepingProcesses.size(); i++) {
            if(sleepingProcesses.get(i).getSleepUntil() >= clock.millis()) {
                addProcess(sleepingProcesses.remove(i));
            }
        }
    }

    private void addProcess(KernelandProcess process) {
        switch(process.getLevel()) {
            case RealTime -> realTimeProcesses.add(process);
            case Interactive -> interactiveProcesses.add(process);
            case Background -> backgroundProcesses.add(process);
        }
    }

    private void removeRunningProcess() {
        for(int arrayIndex = 0; arrayIndex <= 2; arrayIndex++) {
            int runningProcessIndex = processListsArray[arrayIndex].indexOf(runningProcess);
            if(runningProcessIndex > 0) {
                processListsArray[arrayIndex].remove(runningProcessIndex);
                return;
            }
        }
    }

    // this is dumb. just add more if statements to decidePriority()
    private int checkPriority() {
        int result = decidePriority();
        while(processListsArray[result].size() < 1) {
            result = decidePriority();
        }
        return result;
    }

    // Return values:
    // 0 = Realtime, 1 = Interactive, 2 = Background.
    private int decidePriority() {
        Random random = new Random();
        if(processListsArray[0].size() > 0) {
            switch(random.nextInt(10)) {
                case 0, 1, 2, 3, 4, 5 -> {
                    return 0;
                }
                case 6, 7, 8 -> {
                    return 1;
                }
                case 9 -> {
                    return 2;
                }
            }
        }
        else if(processListsArray[1].size() > 0) { // If there are no realtime processes.
            switch(random.nextInt(4)) {
                case 0, 1, 2 -> {
                    return 1;
                }
                case 3 -> {
                    return 2;
                }
            }
        }
        else if(processListsArray[2].size() > 0) {
            return 2; // Only background processes.
        }
        return -1;
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
        addProcess(newProcess);
        if(runningProcess == null) {
            switchProcess();
        }
        return PID;
    }

    public int createProcess(UserlandProcess up) {
        KernelandProcess newProcess = new KernelandProcess(up, PID++, Priority.Level.Interactive);

        // Add process to correct list.
        addProcess(newProcess);
        if(runningProcess == null) {
            switchProcess();
        }
        return PID;
    }

    // Stop running process if there is one; add it to the end of the LL
    // if it hasn't finished, then run first process in LL.
    private void switchProcess() {
        if(runningProcess != null) {
            // If there is a running process, stop it.
            runningProcess.stop();
            removeRunningProcess();
            if(!(runningProcess.isDone())) {
                // If the process did not finish, add it to end of the correct LL.
                addProcess(runningProcess);
            }
            runningProcess = null; // Make runningProcess null as it was stopped.
        }
        awakenProcesses();
        int priority = checkPriority();
        processListsArray[priority].get(0).run();
        runningProcess = processListsArray[priority].get(0);
    }
}
