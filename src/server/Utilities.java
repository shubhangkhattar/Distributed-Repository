package server;

import java.io.PrintWriter;

public class Utilities {

    public static void sendln(PrintWriter printWriter, String string){
        printWriter.println(string);
        printWriter.flush();
    }
}
