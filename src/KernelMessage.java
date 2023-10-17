import java.util.Arrays;

public class KernelMessage {
    private int senderPID;
    private int targetPID;
    private int messageJob;
    private byte[] bytes;

    public KernelMessage(int senderPID) {
        this.senderPID = senderPID;
    }

    public KernelMessage(KernelMessage copyKernelMessage) {
        this(copyKernelMessage.senderPID);
    }

    public int getSenderPID() {
        return senderPID;
    }

    public void setSenderPID(int senderPID) {
        this.senderPID = senderPID;
    }

    public int getTargetPID() {
        return targetPID;
    }

    public void setTargetPID(int targetPID) {
        this.targetPID = targetPID;
    }

    public int getMessageJob() {
        return messageJob;
    }

    public void setMessageJob(int messageJob) {
        this.messageJob = messageJob;
    }

    public byte[] getBytes() {
        return bytes;
    }

    public void setBytes(byte[] bytes) {
        this.bytes = bytes;
    }

    @Override
    public String toString() {
        return "KernelMessage:" +
                "\nSenderPID: " + senderPID + ", TargetPID: " + targetPID +
                ", MessageJob: " + messageJob + ", Bytes: " + Arrays.toString(bytes);
    }
}
