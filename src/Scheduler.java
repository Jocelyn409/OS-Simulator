import java.util.LinkedList;
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
    private KernelandProcess runningProcess = null;
    private Timer timer;
    private TimerTask timerTask;
    private int PID = 0;
    private Clock clock;

    public Scheduler() {
        processListsArray = new List[3];
        processListsArray[0] = realTimeProcesses = Collections.synchronizedList(new ArrayList<>());
        processListsArray[1] = interactiveProcesses = Collections.synchronizedList(new ArrayList<>());
        processListsArray[2] = backgroundProcesses = Collections.synchronizedList(new ArrayList<>());
        sleepingProcesses = new LinkedList<>();
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

    private void checkProcessDemotion() {
        // if ran to time out,
        // check if its ran to timeout 5 times
        // if it has, demote it.
    }

    // Puts all processes that should be awakened on the correct queue.
    private void awakenProcesses() {
        for(int i = 0; i < sleepingProcesses.size(); i++) {
            if(sleepingProcesses.get(i).getSleepUntil() >= clock.millis()) {
                addProcess(sleepingProcesses.remove(i));
            }
        }
    }

    // Gets a process's level and adds it to the appropriate list.
    private void addProcess(KernelandProcess process) {
        switch(process.getLevel()) {
            case RealTime -> realTimeProcesses.add(process);
            case Interactive -> interactiveProcesses.add(process);
            case Background -> backgroundProcesses.add(process);
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

    // this is dumb. just add more if statements to decidePriority()..?
    // need to check if this works. maybe can still use.
    private int checkPriority() {
        int result = decidePriority();
        while(processListsArray[result].size() < 1) {
            result = decidePriority();
        }
        return result;
    }

    // Return values: 0 = Realtime, 1 = Interactive, 2 = Background.
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

    // Sleep the currently running process.
    public void sleep(int milliseconds) {
        // Process sleepUntil time will be the current time plus the added time.
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

        // Stop the process.
        KernelandProcess temp = runningProcess;
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
            // If there is a running process, stop it.
            runningProcess.stop();
            removeRunningProcess();
            checkProcessDemotion();
            if(!(runningProcess.isDone())) {
                // If the process did not finish, add it to end of the correct LL.
                addProcess(runningProcess);
            }
            runningProcess = null; // Make runningProcess null since it was stopped.
        }
        awakenProcesses(); // Awaken any processes that need to be before a new process is run.

        int priority = checkPriority();
        processListsArray[priority].get(0).run();
        runningProcess = processListsArray[priority].get(0);
    }
}
