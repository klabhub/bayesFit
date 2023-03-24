package edu.mit.bcs.bayesphys.core.states;

import java.util.List;

import edu.mit.csail.javablaise.core.namespaces.Link;
import edu.mit.csail.javablaise.core.states.State;
import edu.mit.csail.javablaise.core.states.StateAbstract;

public abstract class NonlinearResponseState extends StateAbstract {
	public Link<State,RFState,String> rf = declareDependency("receptiveField");
	
	abstract public List<Double> computeNonlinearResponse(Object trialKey);
}
