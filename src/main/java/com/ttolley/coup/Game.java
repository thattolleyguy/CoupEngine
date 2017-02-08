package com.ttolley.coup;

import com.google.common.base.Predicates;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.ttolley.coup.player.PlayerHandler;
import com.ttolley.coup.player.RandomPlayerHandler;
import com.ttolley.coup.player.TruthPlayerHandler;

import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

import static com.ttolley.coup.Action.ActionResult.*;

/**
 * Created by tylertolley on 2/7/17.
 */
public class Game {

    LinkedList<PlayerInfo> players;
    LinkedList<Role> deck;
    Map<Integer, PlayerHandler> playerHandlersById;
    int currentPlayer = 0;
    final int numOfPlayers;

    public Game(int numOfPlayers) {
        this.numOfPlayers = numOfPlayers;
        players = Lists.newLinkedList();
        deck = Lists.newLinkedList();
        playerHandlersById = Maps.newHashMap();
        int numOfRoles = numOfPlayers / 2;
        if (numOfRoles < 3)
            numOfRoles = 3;
        for (Role role : Role.values()) {
            for (int i = 0; i < numOfRoles; i++) {
                deck.add(role);
            }
        }
        Collections.shuffle(deck);

        for (int i = 0; i < numOfPlayers; i++) {
            PlayerInfo playerInfo = new PlayerInfo(i, 2, deck.poll(), deck.poll());
            players.add(playerInfo);
        }

        List<Integer> playerIds = players.stream().map(p -> p.playerId).collect(Collectors.toList());
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
                            Action responseAction = targetPlayerHandler.respondToAction(playerAction);
                            while (!validateReponseAction(responseAction, playerAction)) {
                                responseAction = targetPlayerHandler.respondToAction(playerAction);
                            }


                            if (responseAction.type != Action.ActionType.ALLOW) {
                                System.out.println("Player " + targetPlayerHandler.playerInfo.playerId + " counters with " + responseAction.type.name());
                                responseAction.result = checkForChallenges(responseAction, targetPlayerHandler);
                                if (!responseAction.hasFailed()) {
                                    playerAction.result = FAILED_BY_COUNTER;
                                }
                                informPlayers(responseAction);
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
                    Action responseAction = counteringPlayerHandler.respondToAction(playerAction);
                    while (!validateReponseAction(responseAction, playerAction)) {
                        responseAction = counteringPlayerHandler.respondToAction(playerAction);
                    }

                    if (responseAction.type != Action.ActionType.ALLOW) {
                        System.out.println("Player " + counteringPlayerHandler.playerInfo.playerId + " counters with " + responseAction.type.name());
                        responseAction.result = checkForChallenges(responseAction, counteringPlayerHandler);
                        if (!responseAction.hasFailed())
                            playerAction.result = FAILED_BY_COUNTER;
                        informPlayers(responseAction);
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

    private boolean validateReponseAction(Action responseAction, Action playerAction) {
        return responseAction.type.counters == playerAction.type || responseAction.type == Action.ActionType.ALLOW;

    }

    private boolean validateAction(PlayerHandler currentPlayerHandler, Action playerAction) {
        if (currentPlayerHandler.playerInfo.coins >= 10 && playerAction.type != Action.ActionType.COUP)
            return false;
        switch (playerAction.type) {
            case ASSASSINATE:
                return currentPlayerHandler.playerInfo.coins >= 3&&playerAction.targetPlayerId!=null;
            case COUP:
                return currentPlayerHandler.playerInfo.coins >= 7&&playerAction.targetPlayerId!=null;
            case STEAL:
                return playerAction.targetPlayerId!=null;
        }
        return true;
    }

    private Action.ActionResult checkForChallenges(Action action, PlayerHandler actionPlayer) {
        Action.ActionResult result = SUCCEEDED_WITHOUT_CHALLENGE;
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
                    return SUCCEEDED_WITH_CHALLENGE;
                } else {
                    System.out.println("Player " + actionPlayer.playerInfo.playerId + " loses challenge");
                    revealRole(actionPlayer);
                    return FAILED_BY_CHALLENGE;
                }

            }
        }
        if (result == SUCCEEDED_WITHOUT_CHALLENGE)
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
                final ExchangeResult result = currentPlayerHandler.exchangeRoles(newRoles, rolesToKeep);
                //validate result has right number of roles to keep

                final MutableInteger curRoleIndex = new MutableInteger(0);
                currentPlayerHandler.playerInfo.roleStates.stream().filter(Predicates.not(PlayerInfo.RoleState::isRevealed)).forEach(rs -> {
                    if (curRoleIndex.getValue() < result.toKeep.size()) {
                        Role newRole = result.toKeep.get(curRoleIndex.getValue());
                        rs.setRole(newRole);
                        curRoleIndex.increment();
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

    public static void main(String[] args) {
        int numberOfGames = 10000;
        System.out.println("Running " + numberOfGames + " games");
        int exceptionGames = 0;
        Map<Set<Role>, Integer> winsMap = Maps.newHashMap();
        Map<Integer, Integer> winsByPlayer = Maps.newHashMap();


        for (int i = 0; i < numberOfGames; i++) {
            try {
                Game game = new Game(5);
                Map<Integer, Set<Role>> playerRoles = Maps.newHashMap();
                game.playerHandlersById.values().stream().forEach(ph -> {
                    playerRoles.put(ph.playerInfo.playerId, ph.playerInfo.roleStates.stream().map(PlayerInfo.RoleState::getRole).collect(Collectors.toSet()));
                });

                boolean gameOver = game.nextTurn();
                while (!gameOver) {
                    gameOver = game.nextTurn();
                }

                PlayerHandler winner = game.playerHandlersById.values().stream().filter(ph -> !ph.playerInfo.dead).findFirst().get();
                System.out.println("Player " + winner.playerInfo.playerId + " wins!");
                Set<Role> winningRoles = playerRoles.get(winner.playerInfo.playerId);
                Integer wins = winsMap.get(winningRoles);
                if (wins == null) {
                    wins = 0;
                }
                winsMap.put(winningRoles, wins + 1);

                wins = winsByPlayer.get(winner.playerInfo.playerId);
                if (wins == null) {
                    wins = 0;
                }
                winsByPlayer.put(winner.playerInfo.playerId, wins + 1);

            } catch (Exception ex) {
                exceptionGames++;
                ex.printStackTrace();
            }
        }
        System.out.println("Ran " + numberOfGames + " games with " + exceptionGames + " ending in exceptions");
        List<Map.Entry<Set<Role>, Integer>> sortedEntries = winsMap.entrySet().stream().sorted((l, r) -> {
            return l.getValue().compareTo(r.getValue());
        }).collect(Collectors.toList());
        System.out.println(sortedEntries);
        System.out.println(winsByPlayer);
//        System.out.println(winsMap);

    }

    public static class ExchangeResult {
        private final List<Role> toKeep = Lists.newArrayList();
        private final List<Role> toReturn = Lists.newArrayList();

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
