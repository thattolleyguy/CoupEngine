import com.google.common.collect.Lists;

import java.util.List;

/**
 * Created by tylertolley on 2/7/17.
 */
public class PlayerInfo {
    int coins;
    List<RoleState> roleStates = Lists.newArrayList();

    public PlayerInfo(int numOfCoins, Role... roles)
    {
        coins = numOfCoins;
        for (Role role : roles) {
            roleStates.add(new RoleState(role));
        }
    }


    public static class RoleState{
        Role role;
        boolean revealed;

        public RoleState(Role role){
            this.role =role;
            revealed = false;
        }
    }

}
