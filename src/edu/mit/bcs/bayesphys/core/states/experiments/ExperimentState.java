package edu.mit.bcs.bayesphys.core.states.experiments;

import java.util.LinkedList;
import java.util.List;

import edu.mit.csail.javablaise.core.namespaces.Link;
import edu.mit.csail.javablaise.core.states.AutoBagState;
import edu.mit.csail.javablaise.core.states.ManualBagState;
import edu.mit.csail.javablaise.core.states.State;
import edu.mit.csail.javablaise.core.states.StateAbstract;

public class ExperimentState extends StateAbstract {
	public Link<State,StimulusDescriptionState,String> description = declareSubstate("description");
	public Link<State,AutoBagState<StimulusState>,String> stimuli = declareSubstate("stimuli");
	public Link<State,AutoBagState<TrialState>,String> trials = declareSubstate("trials");
	public Link<State,ManualBagState<ResponseState,Object>,String> responses = declareSubstate("responses");
	
	public void defaultConfiguration() {
		StimulusDescriptionState descriptionState = new StimulusDescriptionState();
		description.set(descriptionState);
		
		AutoBagState<StimulusState> stimuliState = new AutoBagState<StimulusState>();
		stimuli.set(stimuliState);
		
		AutoBagState<TrialState> trialsState = new AutoBagState<TrialState>();
		trials.set(trialsState);
		
		ManualBagState<ResponseState,Object> responsesState = new ManualBagState<ResponseState,Object>();
		responses.set(responsesState);
	}
	
	public boolean validate() {
		// TODO implement
		for (Link<State,StimulusState,Object> stimLink : stimuli.get().pool.getLinks()) {
			if (!description.get().validateStimulusData(stimLink.get()))
				return false;
		}
		return true;
	}
	
	// Stimulus description methods
	
	public boolean hasStream(String name) {
		return this.description.get().hasStream(name);
	}
	
	public void addStream(String name, List<AxisType> axisTypes, List<ElementType> elementTypes, List<Integer> sizes) {
		this.description.get().addStream(name, axisTypes, elementTypes, sizes);
	}
	
	public void addStream(String name, AxisType axisType, ElementType elementType, int size) {
		List<AxisType> axisTypes = new LinkedList<AxisType>();
		axisTypes.add(axisType);
		List<ElementType> elementTypes = new LinkedList<ElementType>();
		elementTypes.add(elementType);
		List<Integer> sizes = new LinkedList<Integer>();
		sizes.add(size);
		this.addStream(name, axisTypes, elementTypes, sizes);
	}
	
	// Stimulus definition methods
	
	public Object inventStimulus() {
		StimulusState stimState = new StimulusState();
		Link<State,StimulusState,Object> stimLink = stimuli.get().add(stimState);
		return stimLink.getKey();
	}
	
	public void addStimulusData(Object stimulusKey, String streamName, Object stimulusData) {
		this.description.get().validateStreamData(streamName, stimulusData);
		Link<State, StimulusState, Object> stimulusLink = this.stimuli.get().pool.inventLink(stimulusKey);
		StimulusState stimState = stimulusLink.get();
		stimState.setData(streamName, stimulusData);
	}
	
	// Trial methods

	public void addTrial(Object stimulusKey, List<Double> responseData) {
		// Create and add the trial state
		TrialState trialState = new TrialState();
		Link<State, TrialState, Object> trialLink = this.trials.get().add(trialState);
		Object trialKey = trialLink.getKey();
		
		//Link this trial to the appropriate stimulus
		StimulusState stimState = (stimuli.get().pool.inventLink(stimulusKey)).get();
		trialState.stimulus.set(stimState);
		
		// Create and add the response state
		ResponseState responseState = new ResponseState(responseData);
		responseState.trial.set(trialState);
		this.responses.get().add(responseState, trialKey);
	}

	public void addTrial(Object stimulusKey, Double responseData) {
		List<Double> responseDataList = new LinkedList<Double>();
		responseDataList.add(responseData);
		this.addTrial(stimulusKey, responseDataList);
	}

	public ResponseState getResponseForTrial(Object trialKey) {
		return responses.get().pool.inventLink(trialKey).get();
	}

	public TrialState getTrial(Object trialKey) {
		return trials.get().pool.inventLink(trialKey).get();
	}
}
