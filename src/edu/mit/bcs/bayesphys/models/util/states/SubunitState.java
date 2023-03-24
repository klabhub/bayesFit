package edu.mit.bcs.bayesphys.models.util.states;

import edu.mit.bcs.bayesphys.core.states.NonlinearResponseState;
import edu.mit.bcs.bayesphys.core.states.RFState;
import edu.mit.csail.javablaise.core.namespaces.Link;
import edu.mit.csail.javablaise.core.states.State;
import edu.mit.csail.javablaise.core.states.StateAbstract;

public class SubunitState extends StateAbstract {
	public Link<State,NonlinearResponseState,String> nlr = declareSubstate("nonlinearResponse");
	public Link<State,RFState,String> rf = declareSubstate("receptiveField");
}
