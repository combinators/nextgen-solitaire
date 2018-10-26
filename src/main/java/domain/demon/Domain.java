package domain.demon;

import domain.*;
import domain.Container;
import domain.constraints.*;
import domain.constraints.movetypes.BottomCardOf;
import domain.constraints.movetypes.MoveComponents;
import domain.constraints.movetypes.TopCardOf;
import domain.deal.*;
import domain.deal.steps.DealToTableau;
import domain.deal.steps.DealToFoundation;
import domain.deal.steps.DealToWaste;
import domain.moves.*;
import domain.ui.HorizontalPlacement;
import domain.ui.Layout;
import domain.ui.VerticalPlacement;
import domain.ui.View;
import domain.win.BoardState;

import java.awt.*;

/**
 * Programmatically construct full domain model for "Hello-World"  Alpha variation
 */
public class Domain extends Solitaire {

	private Deal deal;
	private Layout layout;
	private Tableau tableau;
	private Stock stock;
	private Foundation foundation;
	private Waste waste;
	private Container demonColumn;


	/*public int numToDeal() {
		return 3;
	}*/

	public Tableau getTableau() {
		if (tableau == null) {
			tableau = new Tableau();
			for (int i=0; i<4; i++)
				tableau.add(new Column());
		}
		return tableau;
	}

	protected Container getFoundation() {
		if (foundation == null) {
			foundation = new Foundation();
			for (int i = 0; i < 4; i++) { foundation.add(new Pile()); }
		}

		return foundation;
	}

	protected Container getDemonColumn() {
		if (demonColumn == null) {
			demonColumn = new Container(DemonContainerTypes.demonContainer);
			demonColumn.add(new BuildablePile());
		}

		return demonColumn;
	}

	protected Container getWaste() {
		if (waste == null) {
			waste = new Waste();
			//waste.add(new WastePile());
			waste.add (new FanPile(3));
			//waste.add(new Pile());
		}

		return waste;
	}

	/** Override deal as needed. Nothing dealt. */
	@Override
	public Deal getDeal() {
		if (deal == null) {
			deal = new Deal()
					.append(new DealStep(new ContainerTarget(DemonContainerTypes.demonContainer), new Payload(12, false)))
					.append(new DealStep(new ContainerTarget(DemonContainerTypes.demonContainer), new Payload(1, true)))
					.append(new DealToTableau(1))
					.append(new DealStep(new ElementTarget(SolitaireContainerTypes.Foundation, 0)));
		}
		return deal;
	}

	/** Override layout as needed. */
	@Override
	public Layout getLayout() {
		if (layout == null) {
			layout = new Layout()
					.add(SolitaireContainerTypes.Stock, new HorizontalPlacement(new Point(15, 20),
							Solitaire.card_width,  Solitaire.card_height,  Solitaire.card_gap))
					.add(SolitaireContainerTypes.Tableau, new HorizontalPlacement(new Point(293, 200),
							Solitaire.card_width, 13*Solitaire.card_height, Solitaire.card_gap))
					.add(SolitaireContainerTypes.Foundation, new HorizontalPlacement(new Point(293, 20),
							Solitaire.card_width,  Solitaire.card_height,  Solitaire.card_gap))
					.add(SolitaireContainerTypes.Waste, new HorizontalPlacement(new Point(95, 20),
							Solitaire.card_width,  Solitaire.card_height,  Solitaire.card_gap))
					.add(DemonContainerTypes.demonContainer, new HorizontalPlacement(new Point(95, 200),
							Solitaire.card_width,  Solitaire.card_height*13,  Solitaire.card_gap));
		}
		return layout;
	}

	public Stock getStock() {
		if (stock == null) {
			// Alpha has a single deck
			stock = new Stock(1);
		}
		return stock;
	}

	public Domain() {
		super ("Demon");
		init();
	}

	@Override
	public void registerElements() {
		//registerElementAndView(new WastePile(), new View("WastePileView", "PileView", "WastePile"));
		registerElement(new FanPile(3));
		//registerElementAndView(new FanPile(3), new View("FanPileView", "ColumnView", "FanPile"));
		registerElementAndView(new DemonColumn(), new View("DemonColumnView", "ColumnView", "DemonColumn"));
	}

	public Constraint buildOnTableau(MoveInformation bottom) {
		TopCardOf topDestination = new TopCardOf(MoveComponents.Destination);
		return new AndConstraint(new NextRank(topDestination, bottom), new OppositeColor(bottom, topDestination));
	}

	private void init() {
		// we intend to be solvable
		setSolvable(true);

		//registerElements();
		//registerElement(new FanPile(numToDeal()));
		placeContainer(getTableau());
		placeContainer(getFoundation());
		placeContainer(getStock());
		placeContainer(getWaste());
		placeContainer(getDemonColumn());

		// Rules of Klondike defined below
		IsEmpty isEmpty = new IsEmpty(MoveComponents.Destination);
		BottomCardOf bottomMoving = new BottomCardOf(MoveComponents.MovingColumn);
		TopCardOf topDestination = new TopCardOf(MoveComponents.Destination);
		TopCardOf topSource = new TopCardOf(MoveComponents.Source);

		Constraint buildCol = new IfConstraint(isEmpty, new Truth(), buildOnTableau(bottomMoving));
		Constraint buildCard = new IfConstraint(isEmpty, new Truth(), buildOnTableau(MoveComponents.MovingCard));

		// Note in Klondike, we can take advantage of fact that any face-up column of cards WILL be in Descending rank
		// and alternating colors. We could choose to be paranoid and check, but see how this is done in FreeCell.
		addDragMove(new ColumnMove("MoveColumn", getTableau(), new Truth(), getTableau(), buildCol));
		addDragMove(new SingleCardMove("MoveCard", getWaste(), getTableau(), buildCard));

		// Demon -> tableau
		addDragMove(new SingleCardMove("MoveCardFromDemon", getDemonColumn(), getTableau(), buildCard));

		// deal card from stock
		NotConstraint deck_move = new NotConstraint(new IsEmpty(MoveComponents.Source));
		DeckDealNCardsMove deckDeal = new DeckDealNCardsMove(3, "DealDeck", getStock(), deck_move, getWaste());
		addPressMove(deckDeal);
		addPressMove(new ResetDeckMove("ResetDeck", getStock(),
				new IfConstraint(new IsEmpty(MoveComponents.Source)), getWaste()));

		// Move to foundation from Waste. For Java we never checked that card at least existed; need to do so for PysolFC
		Constraint wf_tgt = new IfConstraint(isEmpty,
				new AndConstraint (new IsSingle(MoveComponents.MovingCard), new StartingBase(MoveComponents.MovingCard)),
				new AndConstraint(new NextRank(MoveComponents.MovingCard, topDestination),
						new SameSuit(MoveComponents.MovingCard, topDestination)));

		// Tableau to foundation
		Constraint tf_tgt = new IfConstraint(isEmpty,
				new AndConstraint (new IsSingle(MoveComponents.MovingColumn), new StartingBase(bottomMoving)),
				new AndConstraint (new IsSingle(MoveComponents.MovingColumn),
						new NextRank(bottomMoving, topDestination),
						new SameSuit(bottomMoving, topDestination)));

		// build on the foundation, from tableau and waste. Note that any SINGLECARD can be theoretically moved.
		addDragMove (new ColumnMove("BuildFoundation",
				getTableau(), new IsSingle(MoveComponents.MovingColumn), getFoundation(), tf_tgt));
		addDragMove (new SingleCardMove("BuildFoundationFromWaste", getWaste(), new Truth(), getFoundation(), wf_tgt));

		// Demon -> foundation
		addDragMove (new SingleCardMove("BuildFoundationFromDemon", getDemonColumn(), new Truth(), getFoundation(), wf_tgt));

		// Flip a face-down card on Demon.
		Constraint faceDown = new NotConstraint(new IsFaceUp(topSource));
		addPressMove(new FlipCardMove("FlipCard", getDemonColumn(), faceDown));


		// When all cards are in the AcesUp and KingsDown
		BoardState state = new BoardState();
		state.add(SolitaireContainerTypes.Tableau, 52);
		setLogic (state);
	}
}
