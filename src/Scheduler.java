import java.util.LinkedList;
import java.util.Timer;
import java.util.TimerTask;

public class Scheduler {
    private LinkedList<KernelandProcess> processLinkedList = new LinkedList<>();
    private Timer timer = new Timer();
    private KernelandProcess kernelandProcessReference;

    public Scheduler() {
        TimerTask timerTask = new TimerTask() {
            @Override
            public void run() { // we gotta figure this shit out
                switchProcess();
            }
        };
        timer.schedule(timerTask, 0, 250);
    }

    public void checkRunningProcesses() {
        for(int i = 0; i < processLinkedList.size(); i++) {
            if(processLinkedList.get(i).isDone()) {
                processLinkedList.add(processLinkedList.remove(i));
                processLinkedList.get(i).run();
                return;
            }
        }
    }

    public int createProcess(UserlandProcess up) {
        // newprocess pid updated here? probably?
        KernelandProcess newProcess = new KernelandProcess(up);
        processLinkedList.add(newProcess);
        if(processLinkedList.getFirst().equals(newProcess)) {
            switchProcess();
        }
        return KernelandProcess.PID; // does this actually work? or just return a new int thing
    }

    public void switchProcess() {
        if(!checkRunningProcesses()) {

        }

        KernelandProcess process = processLinkedList.pop();
        process.run();
    }
}
