package com.ttolley.coup;

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
        targetPlayerId =null;
    }

    public Action(ActionType type,  Integer sourcePlayer, Integer targetPlayer){
        this.type = type;
        this.targetPlayerId = targetPlayer;
        this.sourcePlayerId =sourcePlayer;
    }

    public enum ActionResult {
        SUCCEEDED_WITHOUT_CHALLENGE,
        SUCCEEDED_WITH_CHALLENGE,
        FAILED_BY_CHALLENGE,
        FAILED_BY_COUNTER
    }

    public enum ActionType {
        INCOME(null, null, false),
        FOREIGN_AID(null, null, false),
        COUP(null, null, true),
        STEAL(Role.CAPTAIN, null, true),
        ASSASSINATE(Role.ASSASSIN, null, true),
        TAX(Role.DUKE, null, false),
        EXCHANGE(Role.AMBASSADOR, null, false),
        BLOCK_ASSASSINATION(Role.CONTESSA, ASSASSINATE, false),
        BLOCK_AS_CAPTAIN(Role.CAPTAIN, STEAL, false),
        BLOCK_AS_AMBASSADOR(Role.AMBASSADOR, STEAL, false),
        BLOCK_FOREIGN_AID(Role.DUKE,FOREIGN_AID, false),
        ALLOW(null, null, false);

        public final Role requiredRole;
        public final ActionType counters;
        public final boolean requiresTarget;

        ActionType(Role requiredRole, ActionType counters, boolean requiresTarget) {
            this.requiredRole = requiredRole;
            this.counters = counters;
            this.requiresTarget = requiresTarget;
        }

    }

    public ActionResult getActionResult() {
        return result;
    }

    public boolean hasFailed() {
        return this.result == ActionResult.FAILED_BY_CHALLENGE || this.result == ActionResult.FAILED_BY_COUNTER;
    }

    public String toString(){
        return "Player " + sourcePlayerId + " does " + type.name() + " and claims " + type.requiredRole + " targeting player " + targetPlayerId;
    }



}
