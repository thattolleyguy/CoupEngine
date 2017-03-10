package com.ttolley.coup.player;

import com.google.common.base.Predicates;
import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import com.ttolley.coup.Role;
import com.ttolley.coup.model.Action;
import com.ttolley.coup.model.Counteraction;
import com.ttolley.coup.model.PlayerInfo;

import java.util.List;
import java.util.Random;
import java.util.Set;

/**
 * Created by tylertolley on 2/8/17.
 */
public class RandomPlayerHandler extends PlayerHandler {

    final Random random;
    List<Action.ActionType> validTurnActions = Lists.newArrayList();

    public RandomPlayerHandler(PlayerInfo myInfo, List<Integer> otherPlayerIds) {
        super(myInfo, otherPlayerIds);
        validTurnActions.add(Action.ActionType.ASSASSINATE);
        validTurnActions.add(Action.ActionType.COUP);
        validTurnActions.add(Action.ActionType.EXCHANGE);
        validTurnActions.add(Action.ActionType.FOREIGN_AID);
        validTurnActions.add(Action.ActionType.INCOME);
        validTurnActions.add(Action.ActionType.STEAL);
        validTurnActions.add(Action.ActionType.TAX);
        random = new Random(myInfo.hashCode());
    }

    @Override
    public Action taketurn() {

        if (playerInfo.coins >= 10) {
            int targetId = otherPlayerIds.get(random.nextInt(this.otherPlayerIds.size()));
            while (targetId == playerInfo.playerId) {
                targetId = otherPlayerIds.get(random.nextInt(this.otherPlayerIds.size()));
            }
            Action result = new Action(Action.ActionType.COUP, playerInfo.playerId, targetId);


            return result;
        }

        int i = random.nextInt(validTurnActions.size());
        boolean selectedAction = false;
        Action.ActionType selection = null;

        do {
            selection = validTurnActions.get(i);

            switch (selection) {
                case ASSASSINATE:
                    selectedAction = playerInfo.coins >= 3;
                    break;
                case COUP:
                    selectedAction = playerInfo.coins >= 7;
                    break;
                default:
                    selectedAction = true;
                    break;
            }
            i = random.nextInt(validTurnActions.size());

        } while (!selectedAction);
        if (selection.requiresTarget) {
            int targetId = otherPlayerIds.get(random.nextInt(this.otherPlayerIds.size()));
            while (targetId == playerInfo.playerId) {
                targetId = otherPlayerIds.get(random.nextInt(this.otherPlayerIds.size()));
            }
            return new Action(selection, playerInfo.playerId, targetId);
        } else
            return new Action(selection, playerInfo.playerId);

    }

    @Override
    public void informAction(Action action) {

    }

    @Override
    public Counteraction counteract(Action action) {
        int num = random.nextInt(100);
        switch (action.type) {
            case ASSASSINATE:
                if (num <= 50)
                    return new Counteraction(Counteraction.CounteractionType.BLOCK_ASSASSINATION, playerInfo.playerId, action);
                else
                    return new Counteraction(Counteraction.CounteractionType.ALLOW, playerInfo.playerId, action);
            case STEAL:
                if (num <= 33)
                    return new Counteraction(Counteraction.CounteractionType.BLOCK_AS_AMBASSADOR, playerInfo.playerId, action);
                else if (num <= 67)
                    return new Counteraction(Counteraction.CounteractionType.BLOCK_AS_CAPTAIN, playerInfo.playerId, action);
                else
                    return new Counteraction(Counteraction.CounteractionType.ALLOW, playerInfo.playerId, action);
            case FOREIGN_AID:

                if (num <= 50)
                    return new Counteraction(Counteraction.CounteractionType.BLOCK_FOREIGN_AID, playerInfo.playerId, action);
                else
                    return new Counteraction(Counteraction.CounteractionType.ALLOW, playerInfo.playerId, action);
        }
        return new Counteraction(Counteraction.CounteractionType.ALLOW, playerInfo.playerId, action);
    }

    @Override
    public boolean challengeAction(Action action) {
//        return random.nextInt(otherPlayerIds.size() * 2) == 0;
        return false;
    }

    @Override
    public Role revealRole() {
        return playerInfo.roleStates.stream().filter(Predicates.not(PlayerInfo.RoleState::isRevealed)).findFirst().get().getRole();
    }

    @Override
    public void rolesUpdated() {

    }

    @Override
    public Action.ExchangeResult exchangeRoles(List<Role> rolesToChooseFrom, long rolesToKeep) {
        Set<Integer> indexesToKeep = Sets.newHashSet();
        long rolesLeftToPick = rolesToKeep;
        while (rolesLeftToPick > 0) {
            int index = random.nextInt(rolesToChooseFrom.size());
            while (indexesToKeep.contains(index)) {
                index = random.nextInt(rolesToChooseFrom.size());
            }
            indexesToKeep.add(index);
            rolesLeftToPick--;
        }
        Action.ExchangeResult result = new Action.ExchangeResult();
        for (int i = 0; i < rolesToChooseFrom.size(); i++) {
            if (indexesToKeep.contains(i))
                result.keepRole(rolesToChooseFrom.get(i));
            else
                result.returnRole(rolesToChooseFrom.get(i));
        }
        return result;
    }

    @Override
    public void informReveal(int playerId, Role revealedRole) {

    }

    @Override
    public void informDeath(int playerId) {
        otherPlayerIds.remove((Integer) playerId);
    }

    @Override
    public boolean challengeCounteraction(Counteraction counteraction) {
        return random.nextInt(otherPlayerIds.size() * 2) == 0;
    }

    @Override
    public void informCounteraction(Counteraction counteraction) {

    }
}
