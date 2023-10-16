public class TransferText extends UserlandProcess {
    @Override
    public void run() {
        // Testing files.
        int indexFrom = OS.Open("file TransferFrom.txt");
        OS.Seek(indexFrom, 0);
        byte[] buffer = OS.Read(indexFrom, 1000);
        int indexTo = OS.Open("file TransferTo.txt");
        OS.Seek(indexTo, 0);
        OS.Write(indexTo, buffer);
        OS.Close(indexFrom);

        // Testing random.
        int indexRandom = OS.Open("random 100");
        OS.Seek(indexRandom, 0);
        byte[] bufferRandom = OS.Read(indexRandom, 10);
        OS.Seek(indexTo, 0);
        OS.Write(indexTo, bufferRandom);
        OS.Close(indexRandom);
        OS.Close(indexTo);
    }
}
