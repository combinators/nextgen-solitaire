@(WidgetVariable: NameExpr, IgnoreWidgetVariable: NameExpr)

@Java(IgnoreWidgetVariable) = false;

Column srcCol = (Column) src.getModelElement();
//the column on which the mouse has pressed

//Return in the case that the column clicked on is empty
if (srcCol.count() == 0) {
	return;
}

// To get the view Widget for the column being moved itself.
// Note: This method will alter the model for columnView if the condition is met.	
//CardView cardView = src.getCardViewForTopCard(me);
@Java(WidgetVariable) = src.getColumnView (me);

// Safety Check
if (@Java(WidgetVariable)  == null) {
	return;
}

Column draggingSource = (Column) @Java(WidgetVariable) .getModelElement();

// precheck to make sure column is suitable
if (!draggingSource.alternatingColors() || !draggingSource.descending()) {
	src.returnWidget(@Java(WidgetVariable));
	@Java(IgnoreWidgetVariable) = true;
	c.releaseDraggingObject();
	return;
}