import java.util.LinkedList;
import java.util.Timer;
import java.util.TimerTask;

public class Scheduler {
    private LinkedList<KernelandProcess> processLinkedList = new LinkedList<>();
    private Timer timer = new Timer();
    private KernelandProcess kernelandProcessReference;
    private int PID = 0;

    public Scheduler() {
        TimerTask timerTask = new TimerTask() {
            @Override
            public void run() {
                switchProcess();
            }
        };
        timer.schedule(timerTask, 0, 250);
    }

    public int createProcess(UserlandProcess up) {
        KernelandProcess newProcess = new KernelandProcess(up, PID++);
        processLinkedList.add(newProcess);
        if(processLinkedList.getFirst().equals(newProcess)) {
            switchProcess();
        }
        return PID;
    }

    public void switchProcess() {
        for(int i = 0; i < processLinkedList.size(); i++) {
            // if something is running, stop() it.
            // aka if an old process exists?
            if(!(processLinkedList.get(i).isDone())) {
                processLinkedList.add(processLinkedList.remove(i));
                processLinkedList.get(i).run();
            }
        }
    }
}
