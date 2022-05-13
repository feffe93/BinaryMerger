import java.io.BufferedReader;
import java.io.FileReader;
import java.io.PrintWriter;

public class WorkerThread implements Runnable {

    private final String outputName;
    private final String firstFilePath;
    private final String secondFilePath;
    public WorkerThread(String outputName, String firstFilePath, String secondFilePath) {
        this.outputName = outputName;
        this.firstFilePath = firstFilePath;
        this.secondFilePath = secondFilePath;
    }

    public void run() {
        System.out.println(Thread.currentThread().getName() + " (Start) with outputName " + outputName);

        try {

            PrintWriter pw = new PrintWriter(outputName);

            BufferedReader br = new BufferedReader(new FileReader(firstFilePath));

            String line = br.readLine();

            while (line != null) {
                pw.println(line);
                line = br.readLine();
            }

            br = new BufferedReader(new FileReader(secondFilePath));

            line = br.readLine();

            while (line != null) {
                pw.println(line);
                line = br.readLine();
            }

            pw.flush();

            // closing resources
            br.close();
            pw.close();

        } catch (Exception e) {
            System.out.println("Some error occurred");
        }

        System.out.println(Thread.currentThread().getName() + " (End)");
    }
}