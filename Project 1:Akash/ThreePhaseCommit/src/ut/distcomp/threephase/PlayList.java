package ut.distcomp.threephase;

import java.io.*;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

/**
 * Created with IntelliJ IDEA.
 * User: Akash
 * Date: 9/30/13
 * Time: 10:23 PM
 * To change this template use File | Settings | File Templates.
 */
public class PlayList {
    private static final String FILEPATH = "./log/";
    private static final String FILENAME = "Playlist";
    private static final String EXTENSION = ".properties";
    Map<String, String> pl;
    private String logName;
    private String path="";
    Properties prop;

    public PlayList(int procNum){
       logName = FILEPATH + FILENAME + procNum + EXTENSION;
       pl = new HashMap<String, String>();
       prop = new Properties();

       File file = new File(logName);
        if(file.exists()) {
            try {
                System.out.println("file exists");
                prop.load(new FileInputStream(logName));
                for(String key: prop.stringPropertyNames()){
                    pl.put(key, prop.getProperty(key));
                }

                //TODO: Convert String to HashMap
            } catch (IOException e) {
                e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
            }
        } else {
            try {
                prop = new Properties();
                // prop.setProperty("test","value");
                prop.store(new FileOutputStream(logName), null);

                prop.load(new FileInputStream(logName));
            } catch (IOException e) {
                e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
            }
        }
    }

        public boolean actOnDecision(ParseMessage input) throws IOException {
            if(input.getInstruction().equalsIgnoreCase("add")) {
                return add(input);
            } else if(input.getInstruction().equalsIgnoreCase("del")) {
                return delete(input);
            } else if(input.getInstruction().equalsIgnoreCase("edit")) {
                return edit(input);
            } else {
                return false;
            }
        }

        private boolean add(ParseMessage input) throws IOException {
            pl.put(input.getSong(), input.getUrl());
            saveLog();
            return true;
    }

    private boolean delete(ParseMessage input) throws IOException {
        pl.remove(input.getSong());
        saveDeleteLog(input.getSong());
        return true;
    }

    private boolean edit(ParseMessage input) throws IOException {
        pl.remove(input.getOldSong());
        saveDeleteLog(input.getOldSong());
        pl.put(input.getSong(), input.getUrl());
        saveLog();
        return true;
    }

    private void saveLog() throws IOException {

//        for(Object key : prop.keySet())
//        {
//             prop.remove(key);
//        }
        for(String key : pl.keySet()){
            prop.setProperty(key, pl.get(key));
        }
        prop.store(new FileOutputStream(logName), null);
    }

    private void saveDeleteLog(String key) throws IOException {
        prop.remove(key);
        prop.store(new FileOutputStream(logName), null);
    }
    public  Map<String, String> getPlayList(){
        return pl;
    }

}
