/**
 * Created by tylertolley on 2/7/17.
 */
public class TestPlayerHandler extends PlayerHandler {

    public TestPlayerHandler(PlayerInfo playerInfo){
        super(playerInfo);
    }
    public Action taketurn(){
        Action action = new Action(Action.ActionType.TURN);
        for (PlayerInfo.RoleState roleState : myInfo.roleStates) {
            if(!roleState.revealed) {
                action.claimedRole = roleState.role;
                break;
            }
        }
        return action;
    }

    public Action respondToAction(Action action){
        Action response = new Action(Action.ActionType.ALLOW);
        return response;
    }

    public boolean challengeAction(Action action){
        return false;
    }
}
