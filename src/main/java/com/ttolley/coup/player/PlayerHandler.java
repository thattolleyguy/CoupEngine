package com.ttolley.coup.player;

import com.ttolley.coup.Action;
import com.ttolley.coup.Game;
import com.ttolley.coup.PlayerInfo;
import com.ttolley.coup.Role;

import java.util.List;

/**
 * Created by tylertolley on 2/7/17.
 */
public abstract class PlayerHandler {
    public PlayerInfo myInfo;
    public final List<Integer> playerIds;

    public PlayerHandler(PlayerInfo myInfo, List<Integer> playerIds){
        this.myInfo = myInfo;
        this.playerIds = playerIds;
    }

    public abstract Action taketurn();


    public abstract void informAction(Action action);

    public abstract Action respondToTarget(Action action);

    public abstract boolean challengeAction(Action action);

    public abstract Role revealRole();

    public abstract void rolesUpdated();

    public abstract Game.ExchangeResult exchangeRoles(List<Role> newRole, long rolesToKeep);

    public abstract void informReveal(int playerId, Role revealedRole);

    public abstract void informDeath(int playerId);
}
