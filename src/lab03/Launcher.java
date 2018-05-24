package lab03;

import java.io.*;

import static utils.JFrameUtils.promptToChooseFile;
import static utils.JFrameUtils.showTextInFrame;

public class Launcher {

    private final static String OUTPUT_FILENAME = "processed.txt";

    public static void main(String[] args) {
        new Launcher();
    }

    private Launcher() {

        promptToChooseFile(s -> {
            try (FileInputStream fis = new FileInputStream(s);
                 PrintWriter pw = new PrintWriter(OUTPUT_FILENAME)) {

                StringBuilder inputBuilder = new StringBuilder();
                StringBuilder outputBuilder = new StringBuilder();
                while (fis.available() > 0) {
                    final char in = (char) fis.read();
                    inputBuilder.append(in);
                    if (in != '\n' && in != '\r') {
                        final char out = in == '\t' ? ' ' : in;
                        outputBuilder.append(out);
                        pw.print(out);
                    }
                }
                showTextInFrame("input", inputBuilder.toString());
                showTextInFrame("output", outputBuilder.toString());

            } catch (IOException e) {
                System.out.println("Something went wrong: " + e);
            }
        }, "txt");
    }

}
