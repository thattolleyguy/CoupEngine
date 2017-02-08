package com.ttolley.coup.player;

import com.google.common.base.Predicates;
import com.ttolley.coup.Action;
import com.ttolley.coup.Game;
import com.ttolley.coup.PlayerInfo;
import com.ttolley.coup.Role;

import java.util.List;

/**
 * Created by tylertolley on 2/7/17.
 */
public class TestPlayerHandler extends PlayerHandler {

    public TestPlayerHandler(PlayerInfo playerInfo, List<Integer> playerIds) {
        super(playerInfo, playerIds);
    }

    public Action taketurn() {


        return new Action(Action.ActionType.EXCHANGE);
    }

    public Action respondToTarget(Action action) {

        return new Action(Action.ActionType.ALLOW);
    }

    public boolean challengeAction(Action action) {
        return false;
    }

    public void informAction(Action action) {

    }

    public Role revealRole() {
        PlayerInfo.RoleState roleState = myInfo.roleStates.stream().filter(Predicates.not(PlayerInfo.RoleState::isRevealed)).findFirst().get();
        return roleState.getRole();
    }

    @Override
    public void rolesUpdated() {

    }

    @Override
    public Game.ExchangeResult exchangeRoles(List<Role> newRoles, long rolesToKeep) {
        Game.ExchangeResult result = new Game.ExchangeResult();
        for (int i = 0; i < newRoles.size(); i++) {
            if (i < rolesToKeep)
                result.keepRole(newRoles.get(i));
            else
                result.returnRole(newRoles.get(i));
        }
        return result;
    }

    @Override
    public void informReveal(int playerId, Role revealedRole) {

    }

    @Override
    public void informDeath(int playerId) {

    }
}
