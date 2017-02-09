package com.ttolley.coup.player;

import com.google.common.base.Predicates;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import com.ttolley.coup.Action;
import com.ttolley.coup.Counteraction;
import com.ttolley.coup.PlayerInfo;
import com.ttolley.coup.Role;

import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.Set;

/**
 * Created by tylertolley on 2/8/17.
 */
public class TruthPlayerHandler extends PlayerHandler {

    final Random random;
    List<Action.ActionType> validTurnActions = Lists.newArrayList();
    List<Counteraction.CounteractionType> validCounteractions = Lists.newArrayList();

    static Map<Role, Action.ActionType> actionTypeMap;
    static Map<Role, Counteraction.CounteractionType> counteractionTypeMap;

    static {
        actionTypeMap = Maps.newHashMap();
        counteractionTypeMap = Maps.newHashMap();
        actionTypeMap.put(Role.AMBASSADOR, Action.ActionType.EXCHANGE);
        actionTypeMap.put(Role.ASSASSIN, Action.ActionType.ASSASSINATE);
        actionTypeMap.put(Role.CAPTAIN, Action.ActionType.STEAL);
        actionTypeMap.put(Role.DUKE, Action.ActionType.TAX);
        counteractionTypeMap.put(Role.AMBASSADOR, Counteraction.CounteractionType.BLOCK_AS_AMBASSADOR);
        counteractionTypeMap.put(Role.CONTESSA, Counteraction.CounteractionType.BLOCK_ASSASSINATION);
        counteractionTypeMap.put(Role.CAPTAIN, Counteraction.CounteractionType.BLOCK_AS_CAPTAIN);
        counteractionTypeMap.put(Role.DUKE, Counteraction.CounteractionType.BLOCK_FOREIGN_AID);
    }


    public TruthPlayerHandler(PlayerInfo playerInfo, List<Integer> otherPlayerIds) {
        super(playerInfo, otherPlayerIds);
        initializeValidActions();
        random = new Random(playerInfo.hashCode());
    }

    private final void initializeValidActions() {
        validTurnActions.clear();
        validCounteractions.clear();
        validTurnActions.add(Action.ActionType.COUP);
        validTurnActions.add(Action.ActionType.FOREIGN_AID);
        validTurnActions.add(Action.ActionType.INCOME);
        for (PlayerInfo.RoleState roleState : playerInfo.roleStates) {
            if (!roleState.isRevealed()) {
                if (actionTypeMap.get(roleState.getRole()) != null)
                    validTurnActions.add(actionTypeMap.get(roleState.getRole()));
                if (counteractionTypeMap.get(roleState.getRole()) != null)
                    validCounteractions.add(counteractionTypeMap.get(roleState.getRole()));
            }
        }


    }

    @Override
    public Action taketurn() {

        if (playerInfo.coins >= 10) {
            int targetId = otherPlayerIds.get(random.nextInt(this.otherPlayerIds.size()));
            while (targetId == playerInfo.playerId) {
                targetId = otherPlayerIds.get(random.nextInt(this.otherPlayerIds.size()));
            }
            return new Action(Action.ActionType.COUP, playerInfo.playerId, targetId);
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
        for (Counteraction.CounteractionType validCounteraction : validCounteractions) {
            if (validCounteraction.counters == action.type)
                return new Counteraction(validCounteraction, playerInfo.playerId, action);
        }
        return new Counteraction(Counteraction.CounteractionType.ALLOW, playerInfo.playerId, action);
    }

    @Override
    public boolean challengeAction(Action action) {
        // TODO: track what roles have been revealed and only challenge when you know that player can't be that role
        return false;
    }

    @Override
    public Role revealRole() {
        Role role = playerInfo.roleStates.stream().filter(Predicates.not(PlayerInfo.RoleState::isRevealed)).findAny().get().getRole();
        validTurnActions.remove(actionTypeMap.get(role));
        validCounteractions.remove(counteractionTypeMap.get(role));
        return role;
    }

    @Override
    public void rolesUpdated() {
        initializeValidActions();
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
        return false;
    }

    @Override
    public void informCounteraction(Counteraction counteraction) {

    }
}