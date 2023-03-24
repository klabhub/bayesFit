package edu.mit.bcs.bayesphys.core.states.experiments;

import java.util.List;

import edu.mit.csail.javablaise.core.namespaces.CloneContext;
import edu.mit.csail.javablaise.core.states.State;
import edu.mit.csail.javablaise.core.states.StateAbstract;

public class StreamDescriptionState extends StateAbstract {
	protected String name;
	protected List<AxisType> axisTypes;
	protected List<ElementType> elementTypes;
	protected List<Integer> axisSizes;

	public StreamDescriptionState() {
		super();
	}
	
	public StreamDescriptionState(String name, List<AxisType> axisTypes, List<ElementType> elementTypes, List<Integer> axisSizes) {
		super();
		this.name = name;
		this.axisTypes = axisTypes;
		this.elementTypes = elementTypes;
		this.axisSizes = axisSizes;
	}

	@Override
	public void cloneFrom(State other, CloneContext context) {
		super.cloneFrom(other, context);
		StreamDescriptionState otherCasted = (StreamDescriptionState) other;
		this.name = otherCasted.name;
		this.axisTypes = otherCasted.axisTypes;
		this.elementTypes = otherCasted.elementTypes;
		this.axisSizes = otherCasted.axisSizes;
	}
	
	public boolean validateStreamData(Object dataObject) {
		//TODO implement
		return true;
	}
}
