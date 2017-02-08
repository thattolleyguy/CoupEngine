package com.ttolley.coup;

import com.ttolley.coup.Role;

/**
 * Created by tylertolley on 2/7/17.
 */
public class Action {
    public final ActionType type;
    final Integer targetId;
    Integer sourceId;
    ActionResult result = ActionResult.SUCCEEDED_WITHOUT_CHALLENGE;
    //Action countering;

    public Action(ActionType type) {
        this.type = type;
        targetId=null;
    }

    public Action(ActionType type,  Integer targetId){
        this.type = type;
        this.targetId = targetId;
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
        ALLOW(null, null, false);

        public final Role requiredRole;
        public final ActionType counters;
        public final boolean requiresTarget;

        ActionType(Role requiredRole, ActionType actionType, boolean requiresTarget) {
            this.requiredRole = requiredRole;
            this.counters = actionType;
            this.requiresTarget = requiresTarget;
        }

    }

    public ActionResult getActionResult() {
        return result;
    }

    public boolean hasFailed() {
        return this.result == ActionResult.FAILED_BY_CHALLENGE || this.result == ActionResult.FAILED_BY_COUNTER;
    }



}
