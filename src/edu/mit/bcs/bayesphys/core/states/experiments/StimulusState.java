package edu.mit.bcs.bayesphys.core.states.experiments;

import java.util.HashMap;
import java.util.Map;

import edu.mit.csail.javablaise.core.namespaces.CloneContext;
import edu.mit.csail.javablaise.core.states.State;
import edu.mit.csail.javablaise.core.states.StateAbstract;

public class StimulusState extends StateAbstract {
	Map<String,Object> stimData = new HashMap<String, Object>();
	
	@Override
	public void cloneFrom(State other, CloneContext context) {
		super.cloneFrom(other, context);
		this.stimData = ((StimulusState) other).stimData;
	}
	
	public void setData(String streamName, Object stimulusData) {
		stimData.put(streamName, stimulusData);
	}
	
	public Object getData(String streamName) {
		return stimData.get(streamName);
	}

}
