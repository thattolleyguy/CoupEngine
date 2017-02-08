package com.ttolley.coup.player;

import com.google.common.base.Predicates;
import com.ttolley.coup.Action;
import com.ttolley.coup.Game;
import com.ttolley.coup.PlayerInfo;
import com.ttolley.coup.Role;

import java.util.List;

import static sun.audio.AudioPlayer.player;

/**
 * Created by tylertolley on 2/7/17.
 */
public abstract class PlayerHandler {
    public PlayerInfo playerInfo;
    public final List<Integer> otherPlayerIds;

    public PlayerHandler(PlayerInfo playerInfo, List<Integer> otherPlayerIds){
        this.playerInfo = playerInfo;
        this.otherPlayerIds = otherPlayerIds;
    }

    public abstract Action taketurn();


    public abstract void informAction(Action action);

    public abstract Action respondToAction(Action action);

    public abstract boolean challengeAction(Action action);

    public abstract Role revealRole();

    public abstract void rolesUpdated();

    public abstract Game.ExchangeResult exchangeRoles(List<Role> newRole, long rolesToKeep);

    public abstract void informReveal(int playerId, Role revealedRole);

    public abstract void informDeath(int playerId);


}
