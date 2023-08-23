public class KernelandProcess {
    KernelLandProcess(UserlandProcess up) /* creates thread, sets pid */
    void stop() /* if the thread has started, suspend it */
    boolean isDone() /* true if the thread started and not isAlive() */
    void run() /* resume() or start() and update “started” */

}
