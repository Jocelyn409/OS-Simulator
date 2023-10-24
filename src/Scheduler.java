import java.util.*;
import java.time.Clock;

public class Scheduler {
    private List<List<KernelandProcess>> processListsArray;
    private List<KernelandProcess> realTimeProcesses;
    private List<KernelandProcess> interactiveProcesses;
    private List<KernelandProcess> backgroundProcesses;
    private List<KernelandProcess> sleepingProcesses;
    private HashMap<Integer, KernelandProcess> waitingProcesses;
    private HashMap<Integer, KernelandProcess> messageTargets;
    private KernelandProcess runningProcess;
    private Timer timer;
    private TimerTask timerTask;
    private Clock clock;
    private Random random;

    public Scheduler() {
        processListsArray = Collections.synchronizedList(new ArrayList<>());
        processListsArray.add(0, realTimeProcesses = Collections.synchronizedList(new ArrayList<>()));
        processListsArray.add(1, interactiveProcesses = Collections.synchronizedList(new ArrayList<>()));
        processListsArray.add(2, backgroundProcesses = Collections.synchronizedList(new ArrayList<>()));
        sleepingProcesses = Collections.synchronizedList(new ArrayList<>());
        waitingProcesses = new HashMap<>();
        messageTargets = new HashMap<>();
        runningProcess = null;
        timer = new Timer();
        timerTask = new Interrupt();
        timer.schedule(timerTask, 250, 250);
        clock = Clock.systemUTC();
        random = new Random();
    }

    private class Interrupt extends TimerTask {
        @Override
        public void run() {
            if(runningProcess != null) {
                runningProcess.checkProcessDemotion();
            }
            switchProcess();
        }
    }

    public KernelandProcess getRunningProcess() {
        return runningProcess;
    }

    // Real-Time = 0, Interactive = 1, Background = 2, If all lists are empty = -1.
    // Decides a priority list to be taken from at random based on which lists aren't empty.
    // If a list is chosen, it will check to see if that list is empty before returning its respective number,
    // else if the chosen list is empty, it will return the number for the priority higher than it.
    private int decidePriority() {
        if(!realTimeProcesses.isEmpty()) {
            switch(random.nextInt(10)) {
                case 0, 1, 2, 3, 4, 5 -> { return 0; }
                case 6, 7, 8 -> { if(interactiveProcesses.isEmpty()) return 0; return 1; }
                case 9 -> { if(backgroundProcesses.isEmpty()) return 1; return 2; }
            }
        }
        else if(!interactiveProcesses.isEmpty()) {
            switch(random.nextInt(4)) {
                case 0, 1, 2 -> { return 1; }
                case 3 -> { if(backgroundProcesses.isEmpty()) return 1; return 2;}
            }
        }
        else if(!backgroundProcesses.isEmpty()) {
            return 2;
        }
        return -1;
    }

    // Gets a process's level and adds it to the appropriate list.
    private void addProcess(KernelandProcess process) {
        switch(process.getLevel()) {
            case RealTime       -> realTimeProcesses.add(process);
            case Interactive    -> interactiveProcesses.add(process);
            case Background     -> backgroundProcesses.add(process);
        }
    }

    // Puts all processes that should be awakened on the correct queue
    // by checking if their sleepUntil time is <= the current time.
    private void awakenProcesses() {
        for(int i = 0; i < sleepingProcesses.size(); i++) {
            if(sleepingProcesses.get(i).getSleepUntil() <= clock.millis()) {
                addProcess(sleepingProcesses.remove(i));
            }
        }
    }

    // Sleeps the currently running process by removing it from
    // its current list and adding it to the sleepingProcesses list.
    public void sleep(int milliseconds) {
        // Process sleepUntil time will be the current time plus the added time.
        var tempRunningProcess = runningProcess;
        runningProcess = null;
        tempRunningProcess.setSleepUntil(clock.millis() + milliseconds);
        tempRunningProcess.resetProcessTimeoutCount(); // Reset processTimeoutCount since the process is sleeping.

        sleepingProcesses.add(tempRunningProcess);

        switchProcess(); // Switch process since we need a new process to run.
        tempRunningProcess.stop(); // tempRunningProcess needs to be stopped after switchProcess().
    }                                                                   

    // If createProcess() is called with no overload, call the overloaded method
    // with the level being Interactive as a default.
    public int createProcess(UserlandProcess up) {
        return createProcess(up, Priority.Level.Interactive);
    }

    // Create and add new process to LL; if there is no running process, call switchProcess().
    public int createProcess(UserlandProcess up, Priority.Level level) {
        KernelandProcess newProcess = new KernelandProcess(up, level);
        addProcess(newProcess); // Add process to correct list.
        messageTargets.put(newProcess.getPID(), newProcess);
        if(runningProcess == null) {
            switchProcess();
        }
        return newProcess.getPID();
    }

    public int getPid() {
        return runningProcess.getPID();
    }

    public int getPidByName(String input) {
        for(int i = 0; i <= 2; i++) {
            for(int listIndex = 0; listIndex < processListsArray.get(i).size(); listIndex++) {
                KernelandProcess temp = processListsArray.get(i).get(listIndex);
                if(temp.getProcessName().equals(input)) {
                    return temp.getPID();
                }
            }
        }
        return -1;
    }

    public void sendMessage(KernelMessage kernelMessage) {
        KernelMessage message = new KernelMessage(kernelMessage);
        message.setSenderPID(runningProcess.getPID());
        KernelandProcess targetProcess;
        if((targetProcess = messageTargets.get(message.getTargetPID())) != null) {
            targetProcess.addToMessageQueue(message);

            // idk if this is right.
            // his reasoning or explanation seems off.
            if(targetProcess.messageQueueIsEmpty()) {
                waitingProcesses.remove(targetProcess.getPID());
                addProcess(targetProcess);
            }
        }
    }

    public KernelMessage waitForMessage() {
        var tempRunningProcess = runningProcess;
        if(!tempRunningProcess.messageQueueIsEmpty()) {
            return tempRunningProcess.popFirstMessageOnQueue();
        }
        else { // idk if any of this is right tbh lol
            runningProcess = null;
            waitingProcesses.put(tempRunningProcess.getPID(), tempRunningProcess);
            switchProcess();
            tempRunningProcess.stop();
            while(tempRunningProcess.messageQueueIsEmpty()) {
                try {
                    Thread.sleep(10);
                } catch(InterruptedException e) {
                    throw new RuntimeException(e);
                }
            }
            return tempRunningProcess.popFirstMessageOnQueue();
        }
    }

    // Stop running process if there is one; add it to the end of the LL
    // if it hasn't finished, then run first process in LL.
    synchronized private void switchProcess() {
        if(runningProcess != null) {
            if(!(runningProcess.isDone())) {
                // If runningProcess is not null and the process did not finish, add it back to the end of the LL.
                addProcess(runningProcess);
            }
            else {
                // Since process is done, remove it from messageTargets and close all its devices.
                messageTargets.remove(runningProcess.getPID());
                for(int i = 0; i < 10; i++) {
                    OS.Close(i);
                }
            }
        }
        awakenProcesses(); // Awaken any processes that need to be before a new process is run.
        int priority;
        if((priority = decidePriority()) != -1 && !(processListsArray.get(priority).isEmpty())) {
            // Only run a process if there exists at least one that isn't asleep.
            runningProcess = processListsArray.get(priority).remove(0);
            runningProcess.run();
        }
    }
}
