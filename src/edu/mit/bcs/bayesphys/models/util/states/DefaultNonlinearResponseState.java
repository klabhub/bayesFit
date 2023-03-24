package edu.mit.bcs.bayesphys.models.util.states;

import java.util.List;

import edu.mit.bcs.bayesphys.core.states.NonlinearResponseState;

public class DefaultNonlinearResponseState extends NonlinearResponseState {

	@Override
	public List<Double> computeNonlinearResponse(Object trialKey) {
		List<Double> response = this.rf.get().computeLinearResponse(trialKey);
		return response;
	}
}
