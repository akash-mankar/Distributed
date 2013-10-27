package ut.distcomp.threephase;

import ut.distcomp.threephase.STATE;

import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.logging.FileHandler;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.logging.SimpleFormatter;

/**
 * Created with IntelliJ IDEA.
 * User: Akash
 * Date: 10/2/13
 * Time: 9:07 PM
 * To change this template use File | Settings | File Templates.
 */
public class StateManager {
    // STATE validStates;

//    Logger logger;

    public StateManager() throws IOException {
/*        this.logger = Logger.getLogger(this.getClass().getName());
        FileHandler fileHandler = null;
        fileHandler = new FileHandler("log/General.log");
        fileHandler.setFormatter(new SimpleFormatter());
        this.logger.addHandler(fileHandler);*/
    }
    // common method to Coordinator State and Participant State.
    public boolean getMyVote(ParseMessage receivedMessage, PlayList playlist){
        boolean bVote= false;
        Map<String,String> currPlayList = playlist.getPlayList();
        if(receivedMessage.getInstruction().equalsIgnoreCase("ADD") ){

            if(currPlayList.isEmpty()){
                bVote = true;
                System.out.println("Playlist is empty, VOte yes, for Add");
            }
            else{
                String url = currPlayList.get(receivedMessage.getSong());
                if (url == null){
                    // song doesnt exist
                    // safe to add
                    bVote = true;

                    System.out.println("Playlist doesnt contain the song, VOTe yes for add");
                }else{
                    // song exists
                    // VOTE NO
                    bVote = false;

                    System.out.println("Playlist contains the song, cant ADD");
                }
            }

        }else if (receivedMessage.getInstruction().equalsIgnoreCase("DEL") ){

            if(currPlayList.isEmpty()){
                // playlist is empty
                // song doesnt exist
                // VOTE NO
                bVote = false;

                System.out.println("Playlist is empty, Can;t DELETe");
            }
            else{
                String url = currPlayList.get(receivedMessage.getSong());
                if (url != null){
                    // song exists
                    // safe to Deleter
                    bVote = true;

                    System.out.println("Playlist contains the song, Vote yes for delete");
                }else{
                    // song doesn't exist
                    // VOTE NO
                    bVote = false;

                    System.out.println("Playlist doesnt contain the song, Can;t delete");
                }
            }

        }else if (receivedMessage.getInstruction().equalsIgnoreCase("EDIT") ){

            if(currPlayList.isEmpty()){
                // playList Empty
                // can't EDIT
                System.out.println("Playlist is empty, Can;t EDIT");
                bVote = false;
            }
            else{
                String url = currPlayList.get(receivedMessage.getOldSong());
                if (url == null){
                    // song doesnt exist
                    //can;t EDIT
                    System.out.println("Playlist doesn't contain the song, Can;t EDIT");
                    bVote = false;
                }else{
                    // song exists
                    // can edit the song.
                    bVote = true;
                    System.out.println("Playlist contains the song, Can EDIT");

                }
            }

        }
        return bVote;
    }

    public STATE getStartState(STATE currState, ParseMessage req) {

        if(currState == null && ((req.getInstruction().equalsIgnoreCase("ADD") ||
                                  req.getInstruction().equalsIgnoreCase("DEL") ||
                                  req.getInstruction().equalsIgnoreCase("EDIT")))
                             //&& (req.getMessageHeader().equalsIgnoreCase("VOTE_REQ"))
            ) {
            return STATE.START_3PC;
        }
        return STATE.INVALID;
    }


    public STATE getCoordinatorNextState(STATE currState, List<ParseMessage> req, int numProcs) {
        switch(currState){
            case START_3PC:
                return STATE.WAIT_FOR_VOTE;
            case WAIT_FOR_VOTE:
                int yesCount=0;
                if(req.size() != numProcs) {
                    return STATE.ABORT;
                } else {
                    for(ParseMessage item: req){
                        if(item.getMessageHeader().equalsIgnoreCase("yes")){
                            yesCount++;
                        }
                    }

                    if(yesCount == numProcs){
                        return STATE.SEND_PRECOMMIT;
                    }
                    return  STATE.ABORT;
                }
            case SEND_PRECOMMIT:
                return STATE.WAIT_FOR_ACKS;
            case WAIT_FOR_ACKS:
                 return STATE.COMMIT;
           // case UNCERTAIN: break;
            default: break;
        }
        return STATE.INVALID;
    }

    public TransitionMessage getCoordinatorNextMessageHeader(STATE currState) {
        switch(currState){
            case START_3PC: return TransitionMessage.VOTE_REQ;
            case WAIT_FOR_VOTE: return  TransitionMessage.VOTE;
            case SEND_PRECOMMIT: return TransitionMessage.PRECOMMIT;
            case WAIT_FOR_ACKS: return TransitionMessage.ACK;
            case COMMIT: return TransitionMessage.COMMIT;
            case ABORT: return TransitionMessage.ABORT;
            default: return null;
        }
    }

    public ParticipantSTATE getNextParticipantState(ParticipantSTATE State, ParseMessage receivedMessage, PlayList playlist){

        if (receivedMessage.getMessageHeader().equals(TransitionMessage.VOTE_REQ.toString())){
           System.out.println("Checkpoint 1");
           switch(State){
               case WAIT_FOR_VOTE_REQ:

                   System.out.println("Checkpoint two");
                   boolean bVote = false;
                   bVote = getMyVote(receivedMessage, playlist);
                   if(bVote == true)
                       return ParticipantSTATE.UNCERTAIN;
                   else
                       return ParticipantSTATE.ABORTED;

               case UNCERTAIN:
               case COMMITTABLE:
               case COMMITTED:
               case ABORTED:
               default:
                   return ParticipantSTATE.INVALID;
           }
        }
        else if(receivedMessage.getMessageHeader().equals(TransitionMessage.PRECOMMIT.toString())){
           switch (State){
               case UNCERTAIN:
               case COMMITTABLE:
                    return ParticipantSTATE.COMMITTABLE;

               case WAIT_FOR_VOTE_REQ:
               case COMMITTED:
               case ABORTED:
               default:
           }          return  ParticipantSTATE.INVALID;
        }
        else if(receivedMessage.getMessageHeader().equals(TransitionMessage.COMMIT.toString())){
            switch (State){
                case COMMITTABLE:
                case COMMITTED:
                    return ParticipantSTATE.COMMITTED;

                case UNCERTAIN:
                case WAIT_FOR_VOTE_REQ:
                case ABORTED:
                default:
                      return  ParticipantSTATE.INVALID;
            }
        }
        else if(receivedMessage.getMessageHeader().equals(TransitionMessage.ABORT.toString())){
            switch (State){
                case UNCERTAIN:
                case ABORTED:
                    return ParticipantSTATE.ABORTED;

                case WAIT_FOR_VOTE_REQ:
                case COMMITTED:
                case COMMITTABLE:
                default:
                      return  ParticipantSTATE.INVALID;
            }
        }
        else if(receivedMessage.getMessageHeader().equals(TransitionMessage.STATE_REQ.toString())){
            switch (State){
                case UNCERTAIN:
                case COMMITTABLE:
                case COMMITTED:
                case ABORTED:
                case WAIT_FOR_VOTE_REQ:
                    return State;


                default:
                    return ParticipantSTATE.INVALID;
            }
        }
        else if(receivedMessage.getMessageHeader().equals(TransitionMessage.UR_ELECTED.toString())){
            switch (State){
                case UNCERTAIN:
                case COMMITTABLE:
                case COMMITTED:
                case ABORTED:
                case WAIT_FOR_VOTE_REQ:
                    return State;


                default:
                    return ParticipantSTATE.INVALID;
            }
        }
        else if(receivedMessage.getMessageHeader().equals(TransitionMessage.RECOVERY_REQ.toString())){
            switch (State){
                case UNCERTAIN:
                case COMMITTABLE:
                case COMMITTED:
                case ABORTED:
                case WAIT_FOR_VOTE_REQ:
                    return State;

                default:
                    return ParticipantSTATE.INVALID;
            }
        }


        return ParticipantSTATE.INVALID;
    }

    /***************************************************
       Function: getNextMessageHeaderParticipant()
       Deprecated function
       No longer in use.
       Still Keeping it. Might need it in future
    ****************************************************/
    public TransitionMessage getNextMessageHeaderParticipant(ParticipantSTATE currState) {
        switch(currState){
            case WAIT_FOR_VOTE_REQ: return  TransitionMessage.VOTE_REQ;
            case UNCERTAIN:         return  TransitionMessage.PRECOMMIT;
            case COMMITTABLE:       return  TransitionMessage.COMMIT;
            case COMMITTED:  // return TransitionMessage.DECISION;
            default: return null;
        }
    }

    /***************************************************
     Function: isNextParticipantMessageHeaderValid()
     Checks if a received Message is an expected
     message or not for a give Participant State.
     Doesn't handle STATE_REQ; RECOVERY_REQ; UR_SELECTED
     As those could be received anytime.
     Handle them separately
     ****************************************************/
    public boolean isNextParticipantMessageHeaderValid(ParticipantSTATE currState, String messageHeader) {

        switch(currState){
            case WAIT_FOR_VOTE_REQ:
                if(messageHeader.equals(TransitionMessage.VOTE_REQ))
                return  true;
                else
                return false;

            case UNCERTAIN:
                if(messageHeader.equals(TransitionMessage.PRECOMMIT) || messageHeader.equals(TransitionMessage.ABORT))
                return  true;
                else
                return false;

            case COMMITTABLE:
                if(messageHeader.equals(TransitionMessage.COMMIT))
                    return  true;
                else
                    return false;
            case COMMITTED:  // return TransitionMessage.DECISION;
            case ABORTED:
            default:
                return false;
        }
    }

}
