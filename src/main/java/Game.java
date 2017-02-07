import com.google.common.collect.Lists;

import javax.swing.*;
import javax.swing.plaf.basic.BasicBorders;
import java.security.PublicKey;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

/**
 * Created by tylertolley on 2/7/17.
 */
public class Game {

    LinkedList<PlayerInfo> players;
    LinkedList<Role> deck;
    LinkedList<PlayerHandler> playerHandlers;

    public Game(int numOfPlayers){
        players = Lists.newLinkedList();
        deck = Lists.newLinkedList();
        playerHandlers = Lists.newLinkedList();
        int numOfRoles = numOfPlayers/2;
        if(numOfRoles<3)
            numOfRoles=3;
        for (Role role : Role.values()) {
            for(int i=0; i<numOfRoles;i++)
            {
                deck.add(role);
            }
        }
        Collections.shuffle(deck);
        for (int i = 0; i < numOfPlayers; i++) {
            PlayerInfo playerInfo = new PlayerInfo(2, deck.poll(), deck.poll());
            players.add(playerInfo);
            playerHandlers.add(new TestPlayerHandler(playerInfo));
        }

    }

    public boolean nextTurn(){
        PlayerHandler currentPlayer = playerHandlers.poll();
        Action playerAction = currentPlayer.taketurn();

        System.out.println("Player claims: "+playerAction.claimedRole);
        for (PlayerHandler playerHandler : playerHandlers) {
            Action response = playerHandler.respondToAction(playerAction);
            if(response.type!= Action.ActionType.ALLOW);
                //Handle response actions
        }
        playerHandlers.offer(currentPlayer);


        return false;
    }

    public static void main(String[] args) {
        Game game = new Game(3);
        boolean gameOver = game.nextTurn();
        while(!gameOver){
            game.nextTurn();
        }

    }




}
