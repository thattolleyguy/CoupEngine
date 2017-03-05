package com.ttolley.coup;

import com.google.common.base.Predicates;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.ttolley.coup.model.Action;
import com.ttolley.coup.model.Counteraction;
import com.ttolley.coup.model.GameConfig;
import com.ttolley.coup.model.GameResult;
import com.ttolley.coup.model.PlayerInfo;
import com.ttolley.coup.player.PlayerCreator;
import com.ttolley.coup.player.PlayerHandler;
import com.ttolley.coup.player.RandomPlayerHandler;
import com.ttolley.coup.player.TruthPlayerHandler;

import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

import static com.ttolley.coup.model.Action.ActionResult;
import static com.ttolley.coup.model.Counteraction.CounteractionResult;

/**
 * Created by tylertolley on 2/7/17.
 */
public class Game {

    LinkedList<PlayerInfo> players= Lists.newLinkedList();
    LinkedList<Role> deck= Lists.newLinkedList();
    Map<Integer, PlayerHandler> playerHandlersById= Maps.newHashMap();
    int currentPlayer = 0;
    final int numOfPlayers;

    public Game(int numOfPlayers) {
        this.numOfPlayers = numOfPlayers;
        initializeDeck(numOfPlayers);

        List<Integer> playerIds = initializePlayerInfo(numOfPlayers);
        boolean addedTruthPlayer = false;
        for (PlayerInfo player : players) {

            ArrayList<Integer> otherPlayerIds = Lists.newArrayList(playerIds);
            otherPlayerIds.remove((Integer) player.playerId);
            if (!addedTruthPlayer) {
                playerHandlersById.put(player.playerId, new TruthPlayerHandler(player, otherPlayerIds));
                addedTruthPlayer = true;
            } else
                playerHandlersById.put(player.playerId, new RandomPlayerHandler(player, otherPlayerIds));
        }


    }

    private List<Integer> initializePlayerInfo(int numOfPlayers) {
        for (int i = 0; i < numOfPlayers; i++) {
            PlayerInfo playerInfo = new PlayerInfo(i, 2, deck.poll(), deck.poll());
            players.add(playerInfo);
        }

        return players.stream().map(p -> p.playerId).collect(Collectors.toList());
    }

    private void initializeDeck(int numOfPlayers) {
        int numOfRoles = numOfPlayers / 2;
        if (numOfRoles < 3)
            numOfRoles = 3;
        for (Role role : Role.values()) {
            for (int i = 0; i < numOfRoles; i++) {
                deck.add(role);
            }
        }
        Collections.shuffle(deck);
    }

    public Game(GameConfig config, PlayerCreator creator){
        this.numOfPlayers = config.playerTypes.size();
        initializeDeck(this.numOfPlayers);
        List<Integer> playerIds = initializePlayerInfo(numOfPlayers);

        for (int i = 0; i < numOfPlayers; i++) {
            PlayerInfo player = players.get(i);
            ArrayList<Integer> otherPlayerIds = Lists.newArrayList(playerIds);
            otherPlayerIds.remove((Integer) player.playerId);
            playerHandlersById.put(player.playerId, creator.create(config.playerTypes.get(i), player, otherPlayerIds));
        }

    }

    public boolean nextTurn() {
        PlayerHandler currentPlayerHandler = playerHandlersById.get(currentPlayer);


        if (!currentPlayerHandler.playerInfo.dead) {


            Action playerAction = currentPlayerHandler.taketurn();

            // Validate that player can take that action (coins)
            while (!validateAction(currentPlayerHandler, playerAction)) {
                playerAction = currentPlayerHandler.taketurn();
            }


            System.out.println(playerAction);

            if (playerAction.type.requiredRole != null) {

                playerAction.result = checkForChallenges(playerAction, currentPlayerHandler);

                if (!playerAction.hasFailed()) {

                    if (playerAction.targetPlayerId != null) {
                        PlayerHandler targetPlayerHandler = playerHandlersById.get(playerAction.targetPlayerId);
                        if (!targetPlayerHandler.playerInfo.dead) {
                            Counteraction counteraction = targetPlayerHandler.counteract(playerAction);
                            while (!validateCounteraction(counteraction, playerAction)) {
                                counteraction = targetPlayerHandler.counteract(playerAction);
                            }


                            if (counteraction.type != Counteraction.CounteractionType.ALLOW) {
                                System.out.println(counteraction);
                                counteraction.result = checkForCounteractionChallenge(counteraction, targetPlayerHandler);
                                if (!counteraction.hasFailed()) {
                                    playerAction.result = ActionResult.FAILED_BY_COUNTER;
                                }
                                informPlayers(counteraction);
                            }
                        }
                    }
                }
            }
            if (playerAction.type == Action.ActionType.FOREIGN_AID) {
                for (PlayerHandler counteringPlayerHandler : playerHandlersById.values()) {
                    if (currentPlayerHandler.playerInfo.playerId == counteringPlayerHandler.playerInfo.playerId || counteringPlayerHandler.playerInfo.dead) {
                        continue;
                    }
                    Counteraction counteraction = counteringPlayerHandler.counteract(playerAction);
                    while (!validateCounteraction(counteraction, playerAction)) {
                        counteraction = counteringPlayerHandler.counteract(playerAction);
                    }

                    if (counteraction.type != Counteraction.CounteractionType.ALLOW) {
                        System.out.println("Player " + counteringPlayerHandler.playerInfo.playerId + " counters with " + counteraction.type.name());
                        counteraction.result = checkForCounteractionChallenge(counteraction, counteringPlayerHandler);
                        if (!counteraction.hasFailed())
                            playerAction.result = ActionResult.FAILED_BY_COUNTER;
                        informPlayers(counteraction);
                        break;
                    }
                }
            }
            informPlayers(playerAction);
            if (!playerAction.hasFailed())
                this.applyAction(playerAction, currentPlayerHandler);


        } else {
            System.out.println("Player " + currentPlayer + " is dead");
        }
        this.currentPlayer = ++this.currentPlayer % numOfPlayers;
        for (PlayerInfo player : players) {
            System.out.println(player);
        }
        System.out.println();


        return playerHandlersById.values().stream().filter(ph -> !ph.playerInfo.dead).count() == 1;
    }

    private boolean validateCounteraction(Counteraction responseAction, Action playerAction) {
        return responseAction.type.counters == playerAction.type || responseAction.type == Counteraction.CounteractionType.ALLOW;

    }

    private boolean validateAction(PlayerHandler currentPlayerHandler, Action playerAction) {
        if (currentPlayerHandler.playerInfo.coins >= 10 && playerAction.type != Action.ActionType.COUP)
            return false;
        switch (playerAction.type) {
            case ASSASSINATE:
                return currentPlayerHandler.playerInfo.coins >= 3 && playerAction.targetPlayerId != null;
            case COUP:
                return currentPlayerHandler.playerInfo.coins >= 7 && playerAction.targetPlayerId != null;
            case STEAL:
                return playerAction.targetPlayerId != null;
        }
        return true;
    }

    private Action.ActionResult checkForChallenges(Action action, PlayerHandler actionPlayer) {
        Action.ActionResult result = ActionResult.SUCCEEDED_WITHOUT_CHALLENGE;
        for (PlayerHandler challengingPlayerHandler : playerHandlersById.values()) {
            if (actionPlayer.playerInfo.playerId == challengingPlayerHandler.playerInfo.playerId || challengingPlayerHandler.playerInfo.dead) {
                continue;
            }

            if (challengingPlayerHandler.challengeAction(action)) {
                System.out.println("Player " + challengingPlayerHandler.playerInfo.playerId + " challenges player " + actionPlayer.playerInfo.playerId + " claim of " + action.type.requiredRole.name());
                // Does player have that role
                Optional<PlayerInfo.RoleState> proofRole = actionPlayer.playerInfo.roleStates.stream().filter(rs -> rs.getRole() == action.type.requiredRole && !rs.isRevealed()).findFirst();

                if (proofRole.isPresent()) {
                    System.out.println("Player " + challengingPlayerHandler.playerInfo.playerId + " loses challenge");
                    // Add role to deck, shuffle and give a new role to player
                    deck.offer(proofRole.get().getRole());
                    Collections.shuffle(deck);
                    proofRole.get().setRole(deck.poll());
                    actionPlayer.rolesUpdated();
                    revealRole(challengingPlayerHandler);
                    return ActionResult.SUCCEEDED_WITH_CHALLENGE;
                } else {
                    System.out.println("Player " + actionPlayer.playerInfo.playerId + " loses challenge");
                    revealRole(actionPlayer);
                    return ActionResult.FAILED_BY_CHALLENGE;
                }

            }
        }
        if (result == ActionResult.SUCCEEDED_WITHOUT_CHALLENGE)
            System.out.println("No Challenge");
        return result;
    }

    private CounteractionResult checkForCounteractionChallenge(Counteraction counteraction, PlayerHandler actionPlayer) {
        CounteractionResult result = CounteractionResult.SUCCEEDED_WITHOUT_CHALLENGE;
        for (PlayerHandler challengingPlayerHandler : playerHandlersById.values()) {
            if (actionPlayer.playerInfo.playerId == challengingPlayerHandler.playerInfo.playerId || challengingPlayerHandler.playerInfo.dead) {
                continue;
            }

            if (challengingPlayerHandler.challengeCounteraction(counteraction)) {
                System.out.println("Player " + challengingPlayerHandler.playerInfo.playerId + " challenges player " + actionPlayer.playerInfo.playerId + " claim of " + counteraction.type.requiredRole.name());
                // Does player have that role
                Optional<PlayerInfo.RoleState> proofRole = actionPlayer.playerInfo.roleStates.stream().filter(rs -> rs.getRole() == counteraction.type.requiredRole && !rs.isRevealed()).findFirst();

                if (proofRole.isPresent()) {
                    System.out.println("Player " + challengingPlayerHandler.playerInfo.playerId + " loses challenge");
                    // Add role to deck, shuffle and give a new role to player
                    deck.offer(proofRole.get().getRole());
                    Collections.shuffle(deck);
                    proofRole.get().setRole(deck.poll());
                    actionPlayer.rolesUpdated();
                    revealRole(challengingPlayerHandler);
                    return CounteractionResult.SUCCEEDED_WITH_CHALLENGE;
                } else {
                    System.out.println("Player " + actionPlayer.playerInfo.playerId + " loses challenge");
                    revealRole(actionPlayer);
                    return CounteractionResult.FAILED_BY_CHALLENGE;
                }

            }
        }
        if (result == CounteractionResult.SUCCEEDED_WITHOUT_CHALLENGE)
            System.out.println("No Challenge");
        return result;
    }


    private void revealRole(PlayerHandler playerHandler) {
        if (playerHandler.playerInfo.dead)
            return;
        Role role;
        boolean validRole = false;
        PlayerInfo.RoleState roleState = null;
        while (!validRole) {
            role = playerHandler.revealRole();
            // Because JAVA...
            final Role finalRole = role;
            Optional<PlayerInfo.RoleState> first = playerHandler.playerInfo.roleStates.stream().filter(rs -> rs.getRole() == finalRole && !rs.isRevealed()).findFirst();
            validRole = first.isPresent();
            if (validRole)
                roleState = first.get();
        }
        roleState.setRevealed(true);
        System.out.println("Player " + playerHandler.playerInfo.playerId + " reveals role " + roleState.getRole().name());
        informPlayers(playerHandler.playerInfo.playerId, roleState.getRole());
        if (playerHandler.playerInfo.roleStates.stream().filter(Predicates.not(PlayerInfo.RoleState::isRevealed)).count() == 0) {
            playerHandler.playerInfo.dead = true;
            System.out.println("Player " + playerHandler.playerInfo.playerId + " is dead");
            informPlayers(playerHandler.playerInfo.playerId);
        }


    }

    private void informPlayers(int playerId, Role revealedRole) {
        for (PlayerHandler playerHandler : playerHandlersById.values()) {
            playerHandler.informReveal(playerId, revealedRole);
        }
    }

    private void informPlayers(Action action) {
        for (PlayerHandler playerHandler : playerHandlersById.values()) {
            playerHandler.informAction(action);
        }
    }

    private void informPlayers(int playerId) {
        for (PlayerHandler playerHandler : playerHandlersById.values()) {
            playerHandler.informDeath(playerId);
        }
    }

    private void informPlayers(Counteraction counteraction) {
        for (PlayerHandler playerHandler : playerHandlersById.values()) {
            playerHandler.informCounteraction(counteraction);
        }
    }


    private void applyAction(Action action, PlayerHandler currentPlayerHandler) {
        PlayerHandler targetPlayerHandler = null;
        if (action.targetPlayerId != null)
            targetPlayerHandler = playerHandlersById.get(action.targetPlayerId);

        switch (action.type) {
            case ASSASSINATE:
                currentPlayerHandler.playerInfo.coins -= 3;
                if (!targetPlayerHandler.playerInfo.dead)
                    revealRole(targetPlayerHandler);
                break;
            case EXCHANGE:
                List<Role> newRoles = Lists.newArrayList();
                newRoles.add(deck.poll());
                newRoles.add(deck.poll());

                currentPlayerHandler.playerInfo.roleStates.stream().filter(Predicates.not(PlayerInfo.RoleState::isRevealed)).map(PlayerInfo.RoleState::getRole).forEach(newRoles::add);
                long rolesToKeep = newRoles.size() - 2;
                final Action.ExchangeResult result = currentPlayerHandler.exchangeRoles(newRoles, rolesToKeep);
                //validate result has right number of roles to keep

                final AtomicInteger curRoleIndex = new AtomicInteger(0);
                currentPlayerHandler.playerInfo.roleStates.stream().filter(Predicates.not(PlayerInfo.RoleState::isRevealed)).forEach(rs -> {
                    if (curRoleIndex.get() < result.toKeep.size()) {
                        Role newRole = result.toKeep.get(curRoleIndex.get());
                        rs.setRole(newRole);
                        curRoleIndex.incrementAndGet();
                    }
                });
                currentPlayerHandler.rolesUpdated();
                result.toReturn.stream().forEach(deck::offer);
                break;

            case FOREIGN_AID:
                currentPlayerHandler.playerInfo.coins += 2;
                break;
            case INCOME:
                currentPlayerHandler.playerInfo.coins += 1;
                break;
            case STEAL:
                int coinsToSteal = targetPlayerHandler.playerInfo.coins;
                if (coinsToSteal > 2)
                    coinsToSteal = 2;
                currentPlayerHandler.playerInfo.coins += coinsToSteal;
                targetPlayerHandler.playerInfo.coins -= coinsToSteal;
                break;
            case TAX:
                currentPlayerHandler.playerInfo.coins += 3;
                break;
            case COUP:
                currentPlayerHandler.playerInfo.coins -= 7;
                revealRole(targetPlayerHandler);
                break;
        }
    }

    public GameResult getResults() {
    	return new GameResult(players);
    }

}
