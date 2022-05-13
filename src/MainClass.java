import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class MainClass {

    public static void main(String[] args) {

        String directoryLocation = System.getProperty("user.dir");
        System.out.println("======== Entering with start directory " + directoryLocation + " ========");
        String fileSuffix = "txt";

        if (args.length > 0) {
            fileSuffix = args[0];
        }

        System.out.println("File suffix is: " + fileSuffix);
        File folder = new File(directoryLocation);

        if (folder.listFiles() != null) {
            ArrayList<File> fileArray = new ArrayList<>(Arrays.asList(folder.listFiles()));
            ArrayList<File> actualFileList = new ArrayList<>();

            System.out.println("Listing file contained:");

            for (File file : fileArray) {
                if (file.isFile() && ! file.getName().endsWith(".jar") && file.getName().endsWith(fileSuffix)) {
                    System.out.println("    " + file.getName());
                    actualFileList.add(file);
                }
            }

            if (actualFileList.size() > 0) {

                try {
                    Files.createDirectories(Paths.get(directoryLocation + File.separator + "dst"));
                } catch (IOException e) {
                    System.out.println("======== Unable to create result directory ========");
                }

                ArrayList<File> filesToProcess = new ArrayList<>(actualFileList);
                int iterationNumber = 0;

                while (filesToProcess.size() != 1) {
                    //Get group number
                    int groupNumber = filesToProcess.size() / 2;
                    System.out.println("Group number is: " + groupNumber);

                    //Odd
                    boolean isOdd = false;
                    File lastToProcess = null;

                    if (filesToProcess.size() % 2 != 0) {
                        isOdd = true;
                        lastToProcess = filesToProcess.get(0);
                        filesToProcess.remove(0);
                    }

                    //Group files
                    ExecutorService executor = Executors.newFixedThreadPool(groupNumber);
                    ArrayList<File> resultFiles = new ArrayList<>();
                    String lastResultFileName = null;

                    for (int i = 0; i < filesToProcess.size(); i++) {
                        String resultFileName = directoryLocation + File.separator + "dst" + File.separator + iterationNumber + "-" + i + ".txt";
                        lastResultFileName = resultFileName;
                        Runnable worker = new WorkerThread(resultFileName, filesToProcess.get(i).getAbsolutePath(), filesToProcess.get(i+1).getAbsolutePath());
                        executor.execute(worker);
                        resultFiles.add(new File(resultFileName));
                        i = i+1;
                    }

                    executor.shutdown();
                    while (!executor.isTerminated()) {
                    }

                    System.out.println("Finished all threads");

                    if (isOdd) {

                        executor = Executors.newFixedThreadPool(1);

                        String tmpFileName = directoryLocation + File.separator + "dst" + File.separator + "final" +".txt";
                        Runnable worker = new WorkerThread(tmpFileName, lastToProcess.getAbsolutePath(), lastResultFileName);
                        executor.execute(worker);

                        executor.shutdown();
                        while (!executor.isTerminated()) {
                        }

                        try {
                            Files.copy(new File(tmpFileName).toPath(), new File(lastResultFileName).toPath(), StandardCopyOption.REPLACE_EXISTING);
                        } catch (IOException e) {
                        }
                    }

                    filesToProcess = new ArrayList<>(resultFiles);
                    iterationNumber++;
                }

                iterationNumber--;
                String finalFilePath = directoryLocation + File.separator + "dst" + File.separator + iterationNumber + "-" + "0.txt";

                String mergedFileResultPath = directoryLocation + File.separator + "result" + File.separator + "merged" +".txt";
                try {
                    Files.createDirectories(Paths.get(directoryLocation + File.separator + "result"));
                } catch (IOException e) {
                    System.out.println("======== Unable to create result directory ========");
                }

                try {
                    Files.copy(new File(finalFilePath).toPath(), new File(mergedFileResultPath).toPath(), StandardCopyOption.REPLACE_EXISTING);
                    System.out.println("Merged file generated");
                } catch (IOException e) {
                    System.out.println("======== Unable to copy result file ========");
                }
            } else {
                System.out.println("======== Nothing to process ========");
            }

        } else {
            System.out.println("======== Nothing to process ========");
        }

        System.out.println("======== Exiting ========");
    }
}