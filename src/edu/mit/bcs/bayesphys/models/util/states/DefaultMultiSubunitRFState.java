package edu.mit.bcs.bayesphys.models.util.states;

import java.util.List;

import edu.mit.bcs.bayesphys.core.states.NonlinearResponseState;
import edu.mit.bcs.bayesphys.core.states.experiments.StimulusDescriptionState;
import edu.mit.bcs.bayesphys.core.states.experiments.StimulusState;
import edu.mit.bcs.bayesphys.util.Numerical;
import edu.mit.csail.javablaise.core.namespaces.Link;
import edu.mit.csail.javablaise.core.states.State;


/**
 * A simple multi-subunit RF which just sums the outputs of its subunits (and assumes equal weighting)
 * @author koerdingLab
 *
 */
public class DefaultMultiSubunitRFState extends MultiSubunitRFState {

	@Override
	public List<Double> computeLinearResponse(Object trialKey) {
		List<Double> result = null;
		for (Link<State,SubunitState,String> link : this.subunits.get().pool.getLinks()) {
			SubunitState subunitState = link.get();
			NonlinearResponseState nlrState = subunitState.nlr.get();
			List<Double> subunitResponse = nlrState.computeNonlinearResponse(trialKey);
			if (result == null)
				result = subunitResponse;
			else
				Numerical.sumInPlace(result, subunitResponse);
		}
		return result;
	}
	
	@Override
	public List<Double> computeResponseToStimulus(StimulusDescriptionState descriptionState, StimulusState stimState) {
		// This class doesn't need this method
		return null;
	}

}
