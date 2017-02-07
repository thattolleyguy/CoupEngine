import java.util.Optional;

/**
 * Created by tylertolley on 2/7/17.
 */
public abstract class PlayerHandler {
    PlayerInfo myInfo;

    public PlayerHandler(PlayerInfo myInfo){
        this.myInfo = myInfo;
    }

    public abstract Action taketurn();

    public abstract Action respondToAction(Action action);

    public abstract boolean challengeAction(Action action);
}
