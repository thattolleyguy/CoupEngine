package com.ttolley.coup;

import com.google.common.collect.Lists;
import com.ttolley.coup.Role;

import java.util.List;

/**
 * Created by tylertolley on 2/7/17.
 */
public class PlayerInfo {
    public int coins;
    public List<RoleState> roleStates = Lists.newArrayList();
    public boolean dead = false;
    public final int playerId;


    public PlayerInfo(int playerId, int numOfCoins, Role... roles)
    {
        this.playerId = playerId;
        coins = numOfCoins;
        for (Role role : roles) {
            roleStates.add(new RoleState(role));
        }
    }


    public static class RoleState{
        private Role role;
        private boolean revealed;

        public RoleState(Role role){
            this.role =role;
            revealed = false;
        }

        public boolean isRevealed()
        {
            return revealed;
        }

        public Role getRole(){
            return role;
        }

        void setRole(Role role){
            this.role = role;
        }


        public void setRevealed(boolean revealed) {
            this.revealed = revealed;
        }
    }

}
