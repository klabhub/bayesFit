package edu.mit.bcs.bayesphys.core.states;

import java.util.List;

import edu.mit.bcs.bayesphys.core.states.experiments.ExperimentState;
import edu.mit.bcs.bayesphys.core.states.experiments.StimulusDescriptionState;
import edu.mit.bcs.bayesphys.core.states.experiments.StimulusState;
import edu.mit.bcs.bayesphys.core.states.experiments.TrialState;
import edu.mit.csail.javablaise.core.namespaces.Link;
import edu.mit.csail.javablaise.core.states.State;
import edu.mit.csail.javablaise.core.states.StateAbstract;

abstract public class RFState extends StateAbstract {
	public Link<State,ExperimentState,String> experiment = declareDependency("stimuli");

	public List<Double> computeLinearResponse(Object trialKey) {
		StimulusDescriptionState descriptionState = experiment.get().description.get();
		TrialState trialState = experiment.get().getTrial(trialKey);
		StimulusState stimState = trialState.stimulus.get();
		List<Double> result = computeResponseToStimulus(descriptionState, stimState);
		return result;
	}

	abstract public List<Double> computeResponseToStimulus(StimulusDescriptionState descriptionState, StimulusState stimState);
}
