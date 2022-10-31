package domain.moves;

import domain.Card;
import domain.Constraint;
import domain.Container;
import domain.Element;

/**
 * Calls for the removal of a number of cards, all from different elements from within the container.
 *
 * All elements from the container would suffer the removal of a card.
 * These move classes might not be necessary.
 */
public class RemoveStackMove extends ActualMove {

    /**
     * Determine conditions for removing multiple cards from container
     */
    public RemoveStackMove(String name, Container src, Constraint srcCons) {
        super(name, src, srcCons);
    }

    /** This is slightly inconsistent, since it is
     *  expected that only a single card is moved from
     * each of the elements in the container. */
    @Override
    public boolean isSingleCardMove() {
        return false;
    }

    /** By definition, remove from all elements within the container. */
    public boolean isSingleDestination() { return false; }

    /** Get element being moved. */
    public Element   getMovableElement() {
        return new Card();
    }
}
