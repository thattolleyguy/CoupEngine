package com.ttolley.coup.player;

import com.ttolley.coup.Action;
import com.ttolley.coup.Counteraction;
import com.ttolley.coup.PlayerInfo;
import com.ttolley.coup.Role;

import java.util.List;

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

    /**
     * Called whenever is is your turn.
     * @return Action that you want to perform
     */
    public abstract Action taketurn();

    /**
     * Called whenever you have a chance to counteract an action. Generally this means you are the target of an action.
     * However some general actions (like taking foreign aid) can be blocked by anyone. This is done in a round robin.
     * @param action
     * @return Counteraction to perform
     */
    public abstract Counteraction counteract(Action action);

    /**
     * Called whenever you have a chance to challenge an action. NOTE: Challenges are done in a round robin fashion
     * so you may not have a chance to challenge every action
     * @param action
     * @return true if you challenge the action, false if you allow
     */
    public abstract boolean challengeAction(Action action);

    /**
     * Called whenever you have a chance to challenge an counteraction. NOTE: Challenges are done in a round robin fashion
     * so you may not have a chance to challenge every counteraction
     * @param counteraction
     * @return true if you challenge the counteraction, false if you allow
     */
    public abstract boolean challengeCounteraction(Counteraction counteraction);

    /**
     * Called when you must reveal a role (either by losing a challenge or by being killed)
     * @return Role to reveal. NOTE: The game validates that you have that role and will keep calling until you reveal a valid role.
     */
    public abstract Role revealRole();

    /**
     * Called after successfully performing the exchange action
     * @param rolesToChooseFrom This is the list of roles you can choose from. It includes your existing roles
     * @param rolesToKeep The number of roles you can keep
     * @return ExchangeResult has two lists, the roles you want to keep and the roles you want to return
     */
    public abstract Action.ExchangeResult exchangeRoles(List<Role> rolesToChooseFrom, long rolesToKeep);

    /**
     * Called whenever your roles have changed (either due to a role exchange or winning a challenge). This allows
     * you to check what your new roles are in your playerInfo
     */
    public abstract void rolesUpdated();

    /**
     * Called everytime an action is resolved, even when the action fails.
     * @param action
     */
    public abstract void informAction(Action action);

    /**
     * Called whenever a counteraction is resolved, even when the counteraction fails.
     * @param counteraction
     */
    public abstract void informCounteraction(Counteraction counteraction);

    /**
     * Called whenever a player reveals a role
     * @param playerId
     * @param revealedRole
     */
    public abstract void informReveal(int playerId, Role revealedRole);

    /**
     * Called whenever a player dies
     * @param playerId
     */
    public abstract void informDeath(int playerId);
}
