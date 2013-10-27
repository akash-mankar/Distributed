package ut.distcomp.threephase;

/**
 * Created with IntelliJ IDEA.
 * User: Akash
 * Date: 10/2/13
 * Time: 9:16 PM
 * To change this template use File | Settings | File Templates.
 */
public enum STATE {
    START_3PC,
    WAIT_FOR_VOTE,
    SEND_PRECOMMIT,
    WAIT_FOR_ACKS,
    COMMIT,
    INVALID,
    ABORT;


    private String string;

    static {
        START_3PC.string="start";
        WAIT_FOR_VOTE.string="wait_for_vote";
        SEND_PRECOMMIT.string="send_precommit";
        WAIT_FOR_ACKS.string ="wait_for_acks";
        COMMIT.string="commit";
        INVALID.string="invalid";
        ABORT.string="abort";
    }

    public String toString() {
        return string;
    }

}
