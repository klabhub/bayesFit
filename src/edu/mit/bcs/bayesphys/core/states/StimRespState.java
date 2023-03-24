package edu.mit.bcs.bayesphys.core.states;

import edu.mit.bcs.bayesphys.core.states.experiments.ExperimentState;
import edu.mit.bcs.bayesphys.models.util.states.DefaultNonlinearResponseState;
import edu.mit.csail.javablaise.core.namespaces.Link;
import edu.mit.csail.javablaise.core.states.State;
import edu.mit.csail.javablaise.core.states.StateAbstract;

public class StimRespState extends StateAbstract {
	public Link<State,ExperimentState,String> experiment = declareSubstate("experiment");
	public Link<State,RFState,String> rf = declareSubstate("receptiveField");
	public Link<State,NonlinearResponseState,String> nlr = declareSubstate("nonlinearResponse");
	
	public void defaultConfiguration() {
		ExperimentState experimentState = new ExperimentState();
		experiment.set(experimentState);
		experimentState.defaultConfiguration();

		setNLR(new DefaultNonlinearResponseState());
	}
	
	public void setRF(RFState rfState) {
		RFState oldRF = rf.get();
		if (oldRF != null) 
			oldRF.detachFromGraph();
		
		this.rf.set(rfState);
	
		NonlinearResponseState nlrState = this.nlr.get();
		if (nlrState != null)
			nlrState.rf.set(rfState);
		
		rfState.experiment.set(experiment.get());
	}
	
	public void setNLR(NonlinearResponseState nlrState) {
		NonlinearResponseState oldNLR = nlr.get();
		if (oldNLR != null)
			oldNLR.detachFromGraph();
		
		this.nlr.set(nlrState);
		nlrState.rf.set(rf.get());
	}
}
