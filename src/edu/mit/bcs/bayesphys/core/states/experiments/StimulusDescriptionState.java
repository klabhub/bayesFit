package edu.mit.bcs.bayesphys.core.states.experiments;

import java.util.List;

import edu.mit.bcs.bayesphys.core.BayesPhysException;
import edu.mit.csail.javablaise.core.namespaces.Link;
import edu.mit.csail.javablaise.core.states.ManualBagState;
import edu.mit.csail.javablaise.core.states.State;

public class StimulusDescriptionState extends ManualBagState<StreamDescriptionState, String> {

	public boolean validateStimulusData(StimulusState stimState) {
		// TODO implement
		return true;
	}
	
	public boolean validateStreamData(String streamName, Object dataObject) {
		if (!this.hasStream(streamName))
			throw new BayesPhysException("No stream of that name : "+streamName);
		Link<State, StreamDescriptionState, String> streamLink = this.pool.inventLink(streamName);
		StreamDescriptionState descriptionState = streamLink.get();
		return descriptionState.validateStreamData(dataObject);
	}
	
	public boolean hasStream(String name) {
		for (Link<State,StreamDescriptionState,String> streamLink : this.pool.getLinks()) {
			if (streamLink.getKey().equals(name))
				return true;
		}
		return false;
	}

	public void addStream(String name, List<AxisType> axisTypes, List<ElementType> elementTypes, List<Integer> sizes) {
		if (this.hasStream(name))
			throw new BayesPhysException("Stimulus description already has a stream named "+name);
		
		StreamDescriptionState streamState = new StreamDescriptionState(name, axisTypes, elementTypes, sizes);
		Link<State, StreamDescriptionState, String> streamLink = this.pool.inventLink(name);
		streamLink.set(streamState);
	}
}
