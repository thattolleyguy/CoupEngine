package com.ttolley.coup.player;

import com.google.common.base.Predicates;
import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import com.ttolley.coup.Action;
import com.ttolley.coup.Game;
import com.ttolley.coup.PlayerInfo;
import com.ttolley.coup.Role;

import java.util.List;
import java.util.Random;
import java.util.Set;

/**
 * Created by tylertolley on 2/8/17.
 */
public class RandomPlayerHandler extends PlayerHandler {

    Random random = new Random();
    List<Action.ActionType> validTurnActions = Lists.newArrayList();

    public RandomPlayerHandler(PlayerInfo myInfo, List<Integer> playerIds) {
        super(myInfo, playerIds);
        validTurnActions.add(Action.ActionType.ASSASSINATE);
        validTurnActions.add(Action.ActionType.COUP);
        validTurnActions.add(Action.ActionType.EXCHANGE);
        validTurnActions.add(Action.ActionType.FOREIGN_AID);
        validTurnActions.add(Action.ActionType.INCOME);
        validTurnActions.add(Action.ActionType.STEAL);
        validTurnActions.add(Action.ActionType.TAX);
        playerIds.remove(myInfo.playerId);
    }

    @Override
    public Action taketurn() {

        if (myInfo.coins >= 10) {
            int targetId = playerIds.get(random.nextInt(this.playerIds.size()));
            while (targetId == myInfo.playerId) {
                targetId = playerIds.get(random.nextInt(this.playerIds.size()));
            }
            Action result = new Action(Action.ActionType.COUP, targetId);


            return result;
        }

        int i = random.nextInt(validTurnActions.size());
        boolean selectedAction = false;
        Action.ActionType selection = null;

        do {
            selection = validTurnActions.get(i);

            switch (selection) {
                case ASSASSINATE:
                    selectedAction = myInfo.coins >= 3;
                    break;
                case COUP:
                    selectedAction = myInfo.coins >= 7;
                    break;
                default:
                    selectedAction = true;
                    break;
            }
            i = random.nextInt(validTurnActions.size());

        } while (!selectedAction);
        if (selection.requiresTarget) {
            int targetId = playerIds.get(random.nextInt(this.playerIds.size()));
            while (targetId == myInfo.playerId) {
                targetId = playerIds.get(random.nextInt(this.playerIds.size()));
            }
            return new Action(selection, targetId);
        } else
            return new Action(selection);

    }

    @Override
    public void informAction(Action action) {

    }

    @Override
    public Action respondToAction(Action action) {
        int num = random.nextInt(100);
        switch(action.type){
            case ASSASSINATE:
                if(num <=50)
                    return new Action(Action.ActionType.BLOCK_ASSASSINATION);
                else
                    return new Action(Action.ActionType.ALLOW);
            case STEAL:
                if(num<=33)
                    return new Action(Action.ActionType.BLOCK_AS_AMBASSADOR);
                else if(num<=67)
                    return new Action(Action.ActionType.BLOCK_AS_CAPTAIN);
                else
                    return new Action(Action.ActionType.ALLOW);
            case FOREIGN_AID:

                if(num <=50)
                    return new Action(Action.ActionType.BLOCK_FOREIGN_AID);
                else
                    return new Action(Action.ActionType.ALLOW);
        }
        return new Action(Action.ActionType.ALLOW);
    }

    @Override
    public boolean challengeAction(Action action) {
        return random.nextInt(100) >= 50;
    }

    @Override
    public Role revealRole() {
        return myInfo.roleStates.stream().filter(Predicates.not(PlayerInfo.RoleState::isRevealed)).findFirst().get().getRole();
    }

    @Override
    public void rolesUpdated() {

    }

    @Override
    public Game.ExchangeResult exchangeRoles(List<Role> newRole, long rolesToKeep) {
        Set<Integer> indexesToKeep = Sets.newHashSet();
        long rolesLeftToPick = rolesToKeep;
        while(rolesLeftToPick>0){
            int index = random.nextInt(newRole.size());
            while(indexesToKeep.contains(index))
            {
                index = random.nextInt(newRole.size());
            }
            indexesToKeep.add(index);
            rolesLeftToPick--;
        }
        Game.ExchangeResult result = new Game.ExchangeResult();
        for (int i = 0; i <newRole.size(); i++) {
            if(indexesToKeep.contains(i))
                result.keepRole(newRole.get(i));
            else
                result.returnRole(newRole.get(i));
        }
        return result;
    }

    @Override
    public void informReveal(int playerId, Role revealedRole) {

    }

    @Override
    public void informDeath(int playerId) {
        playerIds.remove((Integer)playerId);
    }
}
