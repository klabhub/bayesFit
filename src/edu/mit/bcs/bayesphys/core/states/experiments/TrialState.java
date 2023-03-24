package edu.mit.bcs.bayesphys.core.states.experiments;

import edu.mit.csail.javablaise.core.namespaces.Link;
import edu.mit.csail.javablaise.core.states.State;
import edu.mit.csail.javablaise.core.states.StateAbstract;

public class TrialState extends StateAbstract {
	public Link<State,StimulusState,String> stimulus = declareDependency("stimulus");
}
