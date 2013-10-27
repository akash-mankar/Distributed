package ut.distcomp.threephase;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;

/**
 * Created with IntelliJ IDEA.
 * User: Akash
 * Date: 9/28/13
 * Time: 10:42 PM
 * To change this template use File | Settings | File Templates.
 */
// This is a static class
// One instance per process
public class DTlog{
    private static final String FILEPATH = "./log/";
    private static final String FILENAME = "DTLog";
    private static final String EXTENSION = ".log";
    private static DTlog dtlog;
    private static String logName;
    private BufferedWriter out;

    public static synchronized DTlog getInstance(int procNum) throws IOException {
        if (dtlog == null) {
            dtlog = new DTlog(procNum);
        }
        return dtlog;
    }

    private DTlog (int procNum) throws IOException {
        try {
            logName = FILEPATH + FILENAME + String.valueOf(procNum) + EXTENSION;
            out = new BufferedWriter(new FileWriter(logName , true));
            out.write("<<<" + procNum + ">>> Start Logging");
            out.newLine();
            out.flush();
        }
        catch (Exception e) {
            throw new IOException(this.getClass().getName() + ": Could not create log file" + e.getMessage());
        }
    }
    public void log(String message) throws Exception {
        try {
            out.write(message);
            out.newLine();
            out.flush();
        }
        catch (Exception e) {
            throw new Exception(this.getClass().getName() + ": Logger encountered IOException!");
        }
    }
    public void close() throws IOException {
        out.close();
    }
    public String getLogName() {
        return logName;
    }

}


