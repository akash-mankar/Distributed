package ut.distcomp.threephase;

import java.io.*;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;

/**
 * Created with IntelliJ IDEA.
 * User: tisha
 * Date: 10/6/13
 * Time: 12:00 AM
 * To change this template use File | Settings | File Templates.
 */
public class Recover {

    String filename;
    HashSet<Integer> upset;
    PlayList playListHandler;
    String state;
    BufferedReader reader;
    boolean decisionPending;
    boolean recoverValid;
    ParseMessage pendingRequest;
    HashMap<String, TransitionMessage> old_decisions;
    String lastPrecommit;
    ParticipantSTATE myState;

    public Recover(String filename, int procNum) throws FileNotFoundException {

        this.filename = filename;
        upset = new HashSet<Integer>();
        playListHandler = new PlayList(procNum);
        decisionPending = false;
        recoverValid = false;
        old_decisions = new HashMap<String, TransitionMessage>();
        lastPrecommit="";
    }

    public void parseLogFile() throws IOException {

        File file = new File(filename);
        String temp_preCommit = "";
        if(!file.exists()) {
            recoverValid = false;
            decisionPending = false;
        } else {

            recoverValid = true;
            String line="";
            String req = "";
            int counter = 0;
            BufferedReader reader = new BufferedReader(new FileReader(filename));

            while((line = reader.readLine()) != null){
                if(line.contains("Upset=")) {
                    initializeUpset(line);
                } else if (line.contains("Response=") || line.contains("Request=")) {
                    counter  ++;
                    req = line;
                } else if(line.contains("Precommit=")) {
                    temp_preCommit = line;
                } else if(line.contains("Decision=")) {
                    counter --;
                    String header= "Decision=";
                    line = line.substring((line.indexOf(header) + header.length()), line.length());
                    ParseMessage doICommit = new ParseMessage(line);

                    old_decisions.put(doICommit.getTransaction(), TransitionMessage.valueOf(doICommit.getMessageHeader().toUpperCase()));
                    if(doICommit.getMessageHeader().equalsIgnoreCase(TransitionMessage.COMMIT.toString()))
                        playListHandler.actOnDecision(doICommit);
                }
            }
            if(counter != 0){
                decisionPending = true;
                pendingRequest = new ParseMessage(req);
                lastPrecommit = temp_preCommit;
            }
        }
    }

    protected void initializeUpset(String upset) {

        this.upset = new HashSet<Integer>();

        int start_idx = upset.indexOf("Upset=") +  7;
        String set = upset.substring(start_idx, upset.length());

        set = set.replace("[", "");
        set = set.replace("]","");
        set = set.replaceAll(" ", "");

        String[] upset_array = set.split(",");

        for(int i=0;i<upset_array.length;i++)  {
            this.upset.add(Integer.parseInt(upset_array[i]));
        }

        System.out.println("Reconstructed Upset : " + upset.toString());
    }

    public ParticipantSTATE getMyState() {
        String state = lastPrecommit.substring(lastPrecommit.lastIndexOf(";")+1, lastPrecommit.length());

        if(!state.equals("")) {
            switch(TransitionMessage.valueOf(state.toUpperCase())) {
                case PRECOMMIT: return ParticipantSTATE.COMMITTABLE;
            }
        }
        return ParticipantSTATE.UNCERTAIN;
    }

    public  String getLastPrecommit() {
        return lastPrecommit;
    }
    public PlayList getPlaylist() {
        return playListHandler;
    }

    public HashSet<Integer> getUpset(){
        return upset;
    }

    public boolean isDecisionPending() {
        return decisionPending;
    }

    public ParseMessage getPendingRequest() {
        return pendingRequest;
    }

    public  HashMap<String, TransitionMessage> getOld_decisions() {
        return old_decisions;
    }
    public boolean isRecoverValid() {
        return recoverValid;
    }
}
