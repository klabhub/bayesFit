package edu.mit.bcs.bayesphys.core.states.experiments;

import java.util.List;

import edu.mit.csail.javablaise.core.namespaces.CloneContext;
import edu.mit.csail.javablaise.core.namespaces.Link;
import edu.mit.csail.javablaise.core.states.State;
import edu.mit.csail.javablaise.core.states.StateAbstract;

public class ResponseState extends StateAbstract {
	public Link<State,TrialState,String> trial = declareDependency("trial");
	
	protected List<Double> data;
	
	public ResponseState() {
		super();
	}
	
	public ResponseState(List<Double> responseData) {
		super();
		this.setData(responseData);
	}

	@Override
	public void cloneFrom(State other, CloneContext context) {
		super.cloneFrom(other, context);
		this.data = ((ResponseState) other).data;
	}
	
	public List<Double> getData() {
		return data;
	}
	
	public void setData(List<Double> data) {
		this.data = data;
	}
}
