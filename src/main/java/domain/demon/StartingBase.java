package domain.demon;

import domain.Constraint;
import domain.constraints.MoveInformation;

public class StartingBase extends Constraint {

    public final MoveInformation src;

    public StartingBase(MoveInformation src) {
        this.src = src;
    }
}
