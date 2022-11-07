package xyz.synse.datacenter.logger.strategy.log;

import java.io.File;
import java.io.FileWriter;
import java.text.Format;
import java.text.SimpleDateFormat;
import java.util.Date;

public class FileLogStrategy implements LogStrategy {
    private final File logFile;

    public FileLogStrategy(File logFile) {
        this.logFile = logFile;
    }

    @Override
    public void log(int priority, String tag, String message) {
        try{

            if(!logFile.exists())
                logFile.createNewFile();

            FileWriter fw = new FileWriter(logFile, true);
            fw.write("[" + convertTime(System.currentTimeMillis()) + "] " + tag + ": " + message + System.lineSeparator());
            fw.flush();
            fw.close();
        }catch (Exception e){
            System.err.println("Failed saving log to file");
            System.err.println(logFile.getAbsolutePath());
            e.printStackTrace();
        }
    }

    public String convertTime(long time){
        Date date = new Date(time);
        Format format = new SimpleDateFormat("yyyy MM dd HH:mm:ss");
        return format.format(date);
    }
}
