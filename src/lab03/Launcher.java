package lab03;

import java.io.*;

public class Launcher {

    private final static String FILENAME = "loremIpsum.txt";
    private final static String OUTPUT_FILENAME = "processed.txt";

    public static void main(String[] args) {

        try (FileInputStream fis = new FileInputStream(FILENAME);
             PrintWriter pw = new PrintWriter(OUTPUT_FILENAME)) {

            while (fis.available() > 0) {
                final char in = (char) fis.read();
                if (in != '\n' && in != '\r')
                    pw.print(in == '\t' ? ' ' : in);
            }


        } catch (IOException e) {
            System.out.println("Something went wrong: " + e);
        }

    }

}
