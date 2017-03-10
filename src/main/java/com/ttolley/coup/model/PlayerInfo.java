package com.ttolley.coup.model;

import com.google.common.base.Predicates;
import com.google.common.collect.Lists;
import com.ttolley.coup.Role;

import java.util.List;

/**
 * Created by tylertolley on 2/7/17.
 */
public class PlayerInfo {
	public String type;
    public int coins;
    public List<RoleState> roleStates = Lists.newArrayList();
    public boolean dead = false;
    public final int playerId;

    public PlayerInfo(String type, int playerId, boolean dead) {
    	this(type, playerId, -1);
    	this.dead = dead;
    }
    
    public PlayerInfo(String type, int playerId, int numOfCoins, Role... roles)
    {
        this.type = type;
    	this.playerId = playerId;
        coins = numOfCoins;
        for (Role role : roles) {
            roleStates.add(new RoleState(role));
        }
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        PlayerInfo that = (PlayerInfo) o;

        if (coins != that.coins) return false;
        if (dead != that.dead) return false;
        if (playerId != that.playerId) return false;
        return roleStates != null ? roleStates.equals(that.roleStates) : that.roleStates == null;
    }

    @Override
    public int hashCode() {
        int result = coins;
        result = 31 * result + (roleStates != null ? roleStates.hashCode() : 0);
        result = 31 * result + (dead ? 1 : 0);
        result = 31 * result + playerId;
        return result;
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

        public void setRole(Role role){
            this.role = role;
        }


        public void setRevealed(boolean revealed) {
            this.revealed = revealed;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;

            RoleState roleState = (RoleState) o;

            if (revealed != roleState.revealed) return false;
            return role == roleState.role;
        }

        @Override
        public int hashCode() {
            int result = role != null ? role.hashCode() : 0;
            result = 31 * result + (revealed ? 1 : 0);
            return result;
        }
    }
    public String toString(){
        return "Player " + playerId + " has " + coins + " coins and " + roleStates.stream().filter(Predicates.not(PlayerInfo.RoleState::isRevealed)).count() + " cards alive.";
    }

}
