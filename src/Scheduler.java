import java.time.Clock;
import java.util.*;

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
    private int pageNumber;

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
        pageNumber = 0;
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
        for(Map.Entry<Integer, KernelandProcess> entry : messageTargets.entrySet()) {
            KernelandProcess tempProcess = entry.getValue();
            if(tempProcess.getProcessName().equals(input)) {
                return tempProcess.getPID();
            }
        }
        return -1;
    }

    // Sends a message to runningProcess.
    public void sendMessage(KernelMessage kernelMessage) {
        KernelMessage copyMessage = new KernelMessage(kernelMessage);
        int tempPID = runningProcess.getPID();

        // Set and update senderPIDs for both messages.
        kernelMessage.setSenderPID(tempPID);
        copyMessage.setSenderPID(tempPID);

        KernelandProcess targetProcess = messageTargets.get(copyMessage.getTargetPID());
        if(targetProcess != null) {
            // If targetProcess is in messageTargets (not null), add the message to its queue.
            targetProcess.addToMessageQueue(copyMessage);
            System.out.println("We have sent " + copyMessage);
            tempPID = targetProcess.getPID();
            if(waitingProcesses.get(tempPID) != null) {
                // If waitingProcess contains the targetProcess, remove it from
                // the HashMap and add it back to one of our priority queues.
                waitingProcesses.remove(tempPID);
                addProcess(targetProcess);
            }
        }
    }

    // runningProcess waits for a message to arrive, then returns it.
    public KernelMessage waitForMessage() {
        if(!runningProcess.messageQueueIsEmpty()) {
            // If runningProcess has a message, pop it and return it from its queue.
            return runningProcess.popFirstMessageOnQueue();
        }
        else {
            var tempRunningProcess = runningProcess;
            int tempPID = tempRunningProcess.getPID();

            waitingProcesses.put(tempPID, tempRunningProcess); // Add process to waitingProcesses.

            // Stop and switch process.
            runningProcess = null;
            switchProcess();
            tempRunningProcess.stop();

            // Look for a message to arrive in tempRunningProcess's messageQueue.
            while(true) {
                if(!tempRunningProcess.messageQueueIsEmpty()) {
                    // If tempRunningProcess has a message, pop it and return it from its queue.
                    return tempRunningProcess.popFirstMessageOnQueue();
                }
            }
        }
    }

    public synchronized void getMapping(int virtualPageNumber) throws Exception {
        KernelandProcess tempRunningProcess = runningProcess;
        VirtualToPhysicalMapping[] runningProcessPages = tempRunningProcess.getPhysicalPages();
        int physicalPageNumber;
        int notInUsePhysicalPage;

        KernelandProcess randomProcess;
        VirtualToPhysicalMapping[] randomProcessPages;
        int diskPageNumber;
        int randomProcessInUsePageIndex;

        // If physicalPageNumber is -1, no in use physical page was found.
        if((physicalPageNumber = runningProcessPages[virtualPageNumber].getPhysicalPageNumber()) == -1) {
            if((notInUsePhysicalPage = OS.findNotInUsePage()) != -1) {
                OS.setInUsePage(notInUsePhysicalPage);
            }
            else {
                // Page swap begins since no physical page in array was found.
                randomProcess = getRandomProcess();
                randomProcessPages = randomProcess.getPhysicalPages();
                randomProcessInUsePageIndex = randomProcess.getInUsePhysicalPage();
                diskPageNumber = randomProcess.getPhysicalPages()[randomProcessInUsePageIndex].getDiskPageNumber();

                if(diskPageNumber == -1) {
                    randomProcessPages[randomProcessInUsePageIndex].setDiskPageNumber(pageNumber);
                    pageNumber++;
                }
                int virtualAddress = virtualPageNumber * 1024;
                int pageOffset = virtualAddress % 1024;
                int rowNumber = -1;
                int flag = 2;
                while(flag > 0) {
                    if(UserlandProcess.translationLookasideBuffer[0][0] == virtualPageNumber) {
                        rowNumber = 0;
                    }
                    else if(UserlandProcess.translationLookasideBuffer[1][0] == virtualPageNumber) {
                        rowNumber = 1;
                    }
                    if(rowNumber != -1) {
                        int physicalAddress = UserlandProcess.translationLookasideBuffer[rowNumber][1] * 1024 + pageOffset;
                        byte[] victimPage = new byte[] { UserlandProcess.memory[physicalAddress] };
                        OS.Write(OS.getSwapFile(), victimPage); // Write victim page to disk.
                    }
                    flag--;
                }

                // Set runningProcess's physical page to victim page value.
                runningProcessPages[tempRunningProcess.getInUsePhysicalPage()]
                        .setPhysicalPageNumber(
                                randomProcessPages[randomProcessInUsePageIndex].getPhysicalPageNumber());

                randomProcessPages[randomProcessInUsePageIndex].setPhysicalPageNumber(-1); // Set victim's physical page to -1.
                randomProcess.setPhysicalPages(randomProcessPages); // Set physical pages of randomProcess.
            }
            runningProcess.setPhysicalPages(runningProcessPages); // Set physical pages of runningProcess.
        }
        else {
            if(runningProcessPages[virtualPageNumber].getDiskPageNumber() != -1) {
                // Load old data in.
                runningProcessPages[virtualPageNumber].setPhysicalPageNumber(OS.Read(OS.getSwapFile(), virtualPageNumber)[0]);
            }
            else {
                Arrays.fill(UserlandProcess.memory, (byte)0);
            }
        }

        Random random = new Random();
        int randomInt = random.nextInt(2);
        UserlandProcess.translationLookasideBuffer[randomInt][0] = virtualPageNumber;
        UserlandProcess.translationLookasideBuffer[randomInt][1] = physicalPageNumber;
    }

    public KernelandProcess getRandomProcess() {
        int numberOfProcesses = messageTargets.size();
        KernelandProcess randomProcess;
        int randomProcessInUsePageIndex;
        Random random = new Random();

        while(true) {
            randomProcess = messageTargets.get(random.nextInt(numberOfProcesses));
            randomProcessInUsePageIndex = randomProcess.getInUsePhysicalPage();
            if(randomProcessInUsePageIndex != -1) {
                return randomProcess;
            }
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
                // Since process is done:
                messageTargets.remove(runningProcess.getPID()); // Remove it from messageTargets.
                for(int i = 0; i < 10; i++) {
                    OS.Close(i); // Close all its devices.
                }
            }
            UserlandProcess.clearTLB();
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
