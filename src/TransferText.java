public class TransferText extends UserlandProcess {
    @Override
    public void run() {
        int indexFrom = OS.Open("file TransferFrom.txt");
        OS.Seek(indexFrom, 0);
        byte[] bufferFrom = OS.Read(indexFrom, 100);
        int index0 = OS.Open("file TransferTo.txt");
        OS.Seek(index0, 0);
        OS.Write(index0, bufferFrom);
    }
}
