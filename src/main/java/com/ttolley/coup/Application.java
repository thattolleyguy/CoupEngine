package com.ttolley.coup;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Import;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;

/**
 * Created by tylertolley on 2/9/17.
 */
@SpringBootApplication
@ComponentScan(basePackageClasses = {Application.class})
@Import(CoupApplicationConfiguration.class)
@EnableAutoConfiguration
public class Application {
    public static void main(String[] args) {
        SpringApplication.run(Application.class, args);
    }

//    private static void simulateGames(GameConfig config, int numberOfGames) {
//        System.out.println("Running " + numberOfGames + " games");
//        int exceptionGames = 0;
//        Map<Set<Role>, Integer> winsMap = Maps.newHashMap();
//        Map<Integer, Integer> winsByPlayer = Maps.newHashMap();
//
//        for (int i = 0; i < numberOfGames; i++) {
//            try {
//                Game game = new Game(config);
//                Map<Integer, Set<Role>> playerRoles = Maps.newHashMap();
//                game.playerHandlersById.values().stream().forEach(ph -> {
//                    playerRoles.put(ph.playerInfo.playerId, ph.playerInfo.roleStates.stream().map(PlayerInfo.RoleState::getRole).collect(Collectors.toSet()));
//                });
//
//                boolean gameOver = game.nextTurn();
//                while (!gameOver) {
//                    gameOver = game.nextTurn();
//                }
//
//                PlayerHandler winner = game.playerHandlersById.values().stream().filter(ph -> !ph.playerInfo.dead).findFirst().get();
//                System.out.println("Player " + winner.playerInfo.playerId + " wins!");
//                Set<Role> winningRoles = playerRoles.get(winner.playerInfo.playerId);
//                Integer wins = winsMap.get(winningRoles);
//                if (wins == null) {
//                    wins = 0;
//                }
//                winsMap.put(winningRoles, wins + 1);
//
//                wins = winsByPlayer.get(winner.playerInfo.playerId);
//                if (wins == null) {
//                    wins = 0;
//                }
//                winsByPlayer.put(winner.playerInfo.playerId, wins + 1);
//
//            } catch (Exception ex) {
//                exceptionGames++;
//                ex.printStackTrace();
//            }
//        }
//        System.out.println("Ran " + numberOfGames + " games with " + exceptionGames + " ending in exceptions");
//        List<Map.Entry<Set<Role>, Integer>> sortedEntries = winsMap.entrySet().stream().sorted((l, r) -> {
//            return l.getValue().compareTo(r.getValue());
//        }).collect(Collectors.toList());
//        System.out.println(sortedEntries);
//        System.out.println(winsByPlayer);
//    }
}
