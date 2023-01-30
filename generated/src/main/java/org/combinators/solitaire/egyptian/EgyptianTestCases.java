package org.combinators.solitaire.egyptian;

import org.combinators.solitaire.egyptian.model.*;
import ks.client.gamefactory.GameWindow;
import ks.common.model.*;
import ks.launcher.Main;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

// Should hold falsified cases
public class EgyptianTestCases {

    Egyptian game;

    private Stack getValidStack() {
        Stack movingCards = new Stack();
        for (int rank = Card.KING; rank >= Card.ACE; rank--) {
            movingCards.add(new Card(rank, Card.CLUBS));
        }
        return movingCards;
    }

    private Stack notDescending(Stack stack) {
        if (stack.empty() || stack.count() == 1) {
            stack.add(new Card(Card.KING, Card.CLUBS));
            stack.add(new Card(Card.ACE, Card.CLUBS));
            return stack;
        } else {
            for (int i = 0; i < stack.count() - 1; i++) {
                if (stack.peek(i).getRank() != stack.peek(i + 1).getRank() + 1) {
                    return stack;
                }
            }
            // end of loop, stack in descending order
            Card top = stack.get();
            stack.add(new Card(Card.KING, Card.CLUBS));
            stack.add(top);
            return stack;
        }
    }

    @Before
    public void makeGame() {
        game = new Egyptian();
        final GameWindow window = Main.generateWindow(game, Deck.OrderBySuit);
        window.setVisible(true);
        try {
            Thread.sleep(500);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Test
    public void testMoveColumn1() {
        // Testing TableauToEmptyTableau$
        String type = "MultipleCards$";
        // this is where test set-up must go.
        game.tableau[1].removeAll();
        game.tableau[2].removeAll();
        Card movingCard = new Card(Card.EIGHT, Card.CLUBS);
        Stack source = game.tableau[1];
        Stack destination = game.tableau[2];
        int ss = source.count();
        int ds = destination.count();
        int ms = movingCards.count();
        MoveColumn move = new MoveColumn(source, movingCards, destination);
        // Move is valid
        Assert.assertTrue(move.valid(game));
        // Make move
        Assert.assertTrue(move.doMove(game));
        // Destination set
        Assert.assertEquals(destination.count(), ds + ms);
    }
}
