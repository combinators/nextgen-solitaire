package domain.constraints;

import domain.*;

public class AlternatingColors extends Constraint {

    final String element;

    public AlternatingColors (String element) {
        super();
	this.element = element;
    }

    public String getElement() { return element; }

}
