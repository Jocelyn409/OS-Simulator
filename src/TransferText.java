public class TransferText extends UserlandProcess {
    @Override
    public void run() {
        int index0 = OS.Open("file TransferTo.txt");
        byte[] buffer = new byte[5];
        buffer[0] = 'h';
        buffer[1] = 'e';
        buffer[2] = 'l';
        buffer[3] = 'l';
        buffer[4] = 'o';
        OS.Seek(index0, 1);
        OS.Write(index0, buffer);
        //byte[] buffer2 = OS.Read(index1, 10);
        //OS.Write(index0, buffer2);
    }
}
