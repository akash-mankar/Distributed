package ut.distcomp.threephase;
/**
 * Created with IntelliJ IDEA.
 * User: Akash
 * Date: 10/2/13
 * Time: 11:14 PM
 * To change this template use File | Settings | File Templates.
 */
public enum TransitionMessage {
    VOTE_REQ,
    VOTE,
    PRECOMMIT,
    ACK,
    COMMIT,
    ABORT,
    YES,
    NO,
    RECOVERY_REQ,
    STATE_REQ,
    UR_ELECTED;

    private String string;
    static {
        VOTE_REQ.string="vote_req";
        VOTE.string="vote";
        PRECOMMIT.string="precommit";
        ACK.string="ack";
        COMMIT.string="commit";
        ABORT.string="abort";
        YES.string="yes";
        NO.string="no";
        RECOVERY_REQ.string="recovery_req";
        STATE_REQ.string="state_req";
        UR_ELECTED.string="ur_elected";
    }
    public String toString() {
        return string;
    }
}