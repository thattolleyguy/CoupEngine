package com.ttolley.coup;

import com.google.common.collect.Lists;

import java.util.List;

/**
 * Created by tylertolley on 2/7/17.
 */
public class Action {
    public final ActionType type;
    public final Integer targetPlayerId;
    public final Integer sourcePlayerId;
    ActionResult result = ActionResult.SUCCEEDED_WITHOUT_CHALLENGE;

    public Action(ActionType type, Integer sourcePlayer) {
        this.type = type;
        this.sourcePlayerId = sourcePlayer;
        targetPlayerId = null;
    }

    public Action(ActionType type, Integer sourcePlayer, Integer targetPlayer) {
        this.type = type;
        this.targetPlayerId = targetPlayer;
        this.sourcePlayerId = sourcePlayer;
    }

    public enum ActionResult {
        SUCCEEDED_WITHOUT_CHALLENGE,
        SUCCEEDED_WITH_CHALLENGE,
        FAILED_BY_CHALLENGE,
        FAILED_BY_COUNTER
    }

    public enum ActionType {
        INCOME(null, false),
        FOREIGN_AID(null, false),
        COUP(null, true),
        STEAL(Role.CAPTAIN, true),
        ASSASSINATE(Role.ASSASSIN, true),
        TAX(Role.DUKE, false),
        EXCHANGE(Role.AMBASSADOR, false);

        public final Role requiredRole;
        public final boolean requiresTarget;

        ActionType(Role requiredRole, boolean requiresTarget) {
            this.requiredRole = requiredRole;
            this.requiresTarget = requiresTarget;
        }

    }

    public ActionResult getActionResult() {
        return result;
    }

    public boolean hasFailed() {
        return this.result == ActionResult.FAILED_BY_CHALLENGE || this.result == ActionResult.FAILED_BY_COUNTER;
    }

    public String toString() {
        return "Player " + sourcePlayerId + " does " + type.name() + " and claims " + type.requiredRole + " targeting player " + targetPlayerId;
    }


    public static class ExchangeResult {
        final List<Role> toKeep = Lists.newArrayList();
        final List<Role> toReturn = Lists.newArrayList();

        public ExchangeResult keepRole(Role role) {
            toKeep.add(role);
            return this;
        }

        public ExchangeResult returnRole(Role role) {
            toReturn.add(role);
            return this;
        }



    }
}
