@(RootPackage: NameExpr, MoveName: NameExpr, DraggingCardVariableName: NameExpr, Type:NameExpr)
package @{Java(RootPackage)}.model;

import ks.common.model.*;
import ks.common.games.Solitaire;

/**
 * Move element from one stack to another.
 *
 * Parameters:
 *    RootPackage
 *    Designate
 *    DraggingCard
 */
public class Potential@{Java(MoveName)} extends @{Java(MoveName)} {

    /** Destination. */
    public Potential@{Java(MoveName)} (Stack from, Stack to) {
        super(from, to);
    }

    @@Override
    public boolean valid(Solitaire game) {
        if (@Java(DraggingCardVariableName) == null) {
            if (source.empty()) { return false; }

            @Java(DraggingCardVariableName) = new @{Java(Type)}();
            @{Java(DraggingCardVariableName)}.add(source.get());
            
            boolean result = super.valid(game);
            source.push(@Java(DraggingCardVariableName));

            return result;
        } else {
            return super.valid(game);
        }
    }

    @@Override
    public boolean doMove(Solitaire game) {
        if (!valid(game)) { return false; }

        @Java(DraggingCardVariableName) = new @{Java(Type)}();
        @{Java(DraggingCardVariableName)}.add(source.get());
        boolean result = super.doMove(game);
        
        return result;
    }
}