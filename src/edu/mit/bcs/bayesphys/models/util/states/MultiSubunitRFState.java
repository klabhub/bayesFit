package edu.mit.bcs.bayesphys.models.util.states;

import edu.mit.bcs.bayesphys.core.states.RFState;
import edu.mit.csail.javablaise.core.namespaces.Link;
import edu.mit.csail.javablaise.core.states.ManualBagState;
import edu.mit.csail.javablaise.core.states.State;

abstract public class MultiSubunitRFState extends RFState {
	public Link<State,ManualBagState<SubunitState, String>,String> subunits = declareSubstate("subunits");

}
