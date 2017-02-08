package com.ttolley.coup;

import static com.ttolley.coup.Action.ActionType.*;

/**
 * Created by tylertolley on 2/8/17.
 */
public class CounterAction {
    public final CounterActionType type;
    public final Integer sourcePlayerId;
    public final Action counteredAction;
    CounterActionResult result = CounterActionResult.SUCCEEDED_WITHOUT_CHALLENGE;

    public CounterAction(CounterActionType type, Integer sourcePlayer, Action counteredAction){
        this.type = type;
        this.counteredAction = counteredAction;
        this.sourcePlayerId =sourcePlayer;
    }

    public enum CounterActionResult {
        SUCCEEDED_WITHOUT_CHALLENGE,
        SUCCEEDED_WITH_CHALLENGE,
        FAILED_BY_CHALLENGE
    }

    public enum CounterActionType{
        BLOCK_ASSASSINATION(Role.CONTESSA, ASSASSINATE),
        BLOCK_AS_CAPTAIN(Role.CAPTAIN, STEAL),
        BLOCK_AS_AMBASSADOR(Role.AMBASSADOR, STEAL),
        BLOCK_FOREIGN_AID(Role.DUKE,FOREIGN_AID),
        ALLOW(null, null);

        public final Role requiredRole;
        public final Action.ActionType counters;

        CounterActionType(Role requiredRole, Action.ActionType counters) {
            this.requiredRole = requiredRole;
            this.counters = counters;
        }
    }

    public boolean hasFailed() {
        return this.result == CounterActionResult.FAILED_BY_CHALLENGE;
    }
}
