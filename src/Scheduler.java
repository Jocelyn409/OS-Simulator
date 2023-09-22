import java.util.List;
import java.util.Timer;
import java.util.TimerTask;
import java.time.Clock;
import java.util.Random;
import java.util.ArrayList;
import java.util.Collections;

public class Scheduler {
    private List<KernelandProcess>[] processListsArray;
    private List<KernelandProcess> realTimeProcesses;
    private List<KernelandProcess> interactiveProcesses;
    private List<KernelandProcess> backgroundProcesses;
    private List<KernelandProcess> sleepingProcesses;
    private KernelandProcess runningProcess;
    private Timer timer;
    private TimerTask timerTask;
    private int PID = 0;
    private Clock clock;

    public Scheduler() {
        processListsArray = new List[3];
        processListsArray[0] = realTimeProcesses = Collections.synchronizedList(new ArrayList<>());
        processListsArray[1] = interactiveProcesses = Collections.synchronizedList(new ArrayList<>());
        processListsArray[2] = backgroundProcesses = Collections.synchronizedList(new ArrayList<>());
        sleepingProcesses = Collections.synchronizedList(new ArrayList<>());
        runningProcess = null;
        timer = new Timer();
        timerTask = new Interrupt();
        timer.schedule(timerTask, 250, 250);
        clock = Clock.systemUTC();
    }

    private class Interrupt extends TimerTask {
        @Override
        public void run() {
            switchProcess();
        }
    }

    private void checkProcessDemotion() {
        if(runningProcess.getRunsToTimeout() == 5) {
            switch(runningProcess.getLevel()) {
                case RealTime -> runningProcess.setLevel(Priority.Level.Interactive);
                case Interactive -> runningProcess.setLevel(Priority.Level.Background);
            }
        }
    }

    // Puts all processes that should be awakened on the correct queue.
    private void awakenProcesses() {
        for(int i = 0; i < sleepingProcesses.size(); i++) {
            if(sleepingProcesses.get(i).getSleepUntil() <= clock.millis()) {
                addProcess(sleepingProcesses.remove(i));
            }
        }
    }

    private int checkEmptyLists() {
        int listCounter = 0;
        if(!processListsArray[0].isEmpty()) {
            listCounter += 1;
        }
        if(!processListsArray[1].isEmpty()) {
            listCounter += 2;
        }
        if(!processListsArray[2].isEmpty()) {
            listCounter += 4;
        }
        return listCounter;
    }

    private int decidePriority() {
        Random random = new Random();
        int listCounter = checkEmptyLists();
        switch(listCounter) {
            // One list isn't empty.
            case 1: return 0;
            case 2: return 1;
            case 4: return 2;
            // Two lists aren't empty.
            case 3: // Real-time and interactive aren't empty.
                switch(random.nextInt(5)) {
                    case 0, 1, 2 -> { return 0; }
                    case 3, 4 -> { return 1; }
                }
            case 5: // Real-time and background aren't empty.
                switch(random.nextInt(10)) {
                    case 0, 1, 2, 3, 4, 5, 6, 7, 8 -> { return 0; }
                    case 9 -> { return 2; }
                }
            case 6: // Interactive and background aren't empty.
                switch(random.nextInt(4)) {
                    case 0, 1, 2 -> { return 1; }
                    case 3 -> { return 2; }
                }
            // No lists are empty.
            case 7:
                switch(random.nextInt(10)) {
                    case 0, 1, 2, 3, 4, 5 -> { return 0; }
                    case 6, 7, 8 -> { return 1; }
                    case 9 -> { return 2; }
                }
            default:
                return -1;
        }
    }

    // Gets a process's level and adds it to the appropriate list.
    private void addProcess(KernelandProcess process) {
        switch(process.getLevel()) {
            case RealTime       -> realTimeProcesses.add(process);
            case Interactive    -> interactiveProcesses.add(process);
            case Background     -> backgroundProcesses.add(process);
        }
    }

    // Searches each priority list and trys to find the running process in order to remove it.
    private void removeRunningProcess() {
        int runningProcessIndex;
        for(int arrayIndex = 0; arrayIndex <= 2; arrayIndex++) {
            runningProcessIndex = processListsArray[arrayIndex].indexOf(runningProcess);
            if(runningProcessIndex >= 0) {
                // If index is >= 0, process is in list and is then removed.
                processListsArray[arrayIndex].remove(runningProcessIndex);
                return;
            }
        }
        throw new RuntimeException("Running process removal was attempted but process was not found.");
    }

    // Sleep the currently running process.
    public void sleep(int milliseconds) {
        // Process sleepUntil time will be the current time plus the added time.
        runningProcess.setSleepUntil(clock.millis() + milliseconds);
        runningProcess.resetRunsToTimeout();

        // Stop and remove runningProcess.
        var temp = runningProcess;
        switch(runningProcess.getLevel()) {
            // Find and remove process from its list, then add it to the sleeping process list.
            case RealTime       -> sleepingProcesses.add(
                    realTimeProcesses.remove(realTimeProcesses.indexOf(runningProcess)));
            case Interactive    -> sleepingProcesses.add(
                    interactiveProcesses.remove(interactiveProcesses.indexOf(runningProcess)));
            case Background     -> sleepingProcesses.add(
                    backgroundProcesses.remove(backgroundProcesses.indexOf(runningProcess)));
        }
        runningProcess = null;
        temp.stop();

        switchProcess();
    }

    // If createProcess() is called with no overload, call the overloaded method
    // with the level being Interactive as a default.
    public int createProcess(UserlandProcess up) {
        return createProcess(up, Priority.Level.Interactive);
    }

    // Create and add new process to LL; if there is no running process, call switchProcess().
    public int createProcess(UserlandProcess up, Priority.Level level) {
        KernelandProcess newProcess = new KernelandProcess(up, PID++, level);
        addProcess(newProcess); // Add process to correct list.
        if(runningProcess == null) {
            switchProcess();
        }
        return PID;
    }

    // Stop running process if there is one; add it to the end of the LL
    // if it hasn't finished, then run first process in LL.
    private void switchProcess() {
        if(runningProcess != null) {
            // If there is a running process, stop it and remove it.
            var temp = runningProcess;
            removeRunningProcess();
            if(!(temp.isDone())) {
                // If the process did not finish, add it back to the end of the LL.
                addProcess(temp);
            }
            runningProcess.incrementRunsToTimeout(); // where to put???
            checkProcessDemotion();
            runningProcess = null;
            temp.stop();
        }
        awakenProcesses(); // Awaken any processes that need to be before a new process is run.
        int priority;
        if((priority = decidePriority()) != -1) {
            // Only run a process if there exists at least one that isn't asleep.
            runningProcess = processListsArray[priority].get(0);
            runningProcess.run();
        }
    }
}
