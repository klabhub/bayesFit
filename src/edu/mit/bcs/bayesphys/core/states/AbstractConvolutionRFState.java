package edu.mit.bcs.bayesphys.core.states;

import java.util.List;

import edu.mit.bcs.bayesphys.core.states.experiments.StimulusDescriptionState;
import edu.mit.bcs.bayesphys.core.states.experiments.StimulusState;

abstract public class AbstractConvolutionRFState extends RFState {
	private static class Filter {
		
	}

	public List<Double> computeResponseToStimulus(StimulusDescriptionState descriptionState, StimulusState stimState) {
		Filter filter = computeFilter();
		return computeConvolution(descriptionState, stimState, filter);
	}
	
	public List<Double> computeConvolution(StimulusDescriptionState descriptionState, StimulusState stimState, Filter filter) {
		return null;
	}
	
	abstract public Filter computeFilter(); 

}
