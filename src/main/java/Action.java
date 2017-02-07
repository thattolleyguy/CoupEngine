/**
 * Created by tylertolley on 2/7/17.
 */
public class Action {
    Role claimedRole;
    public final ActionType type;
    PlayerInfo target;
    PlayerInfo source;
    Action countering;

    public Action(ActionType type) {
        this.type = type;
    }

    public enum ActionType {
        TURN,
        COUNTER,
        CHALLENGE,
        ALLOW
    }

}
