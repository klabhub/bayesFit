package edu.mit.bcs.bayesphys.models.tc;

import java.util.LinkedList;
import java.util.List;

import edu.mit.bcs.bayesphys.core.densities.StimRespDensity;
import edu.mit.bcs.bayesphys.core.states.StimRespState;
import edu.mit.csail.javablaise.core.densities.Density;
import edu.mit.csail.javablaise.core.queries.Query;
import edu.mit.csail.javablaise.core.util.DoubleState;

public class TCSampleCollectionObserver implements Query<StimRespState> {
	List<Double> llhds;
	List<Double> priors;
	List<Double> p1s;
	List<Double> p2s;
	List<Double> p3s;
	List<Double> p4s;
	List<Double> p5s;
	List<Double> pLlhds;
	
	public TCSampleCollectionObserver() {
		llhds = new LinkedList<Double>();
		priors = new LinkedList<Double>();
		p1s = new LinkedList<Double>();
		p2s = new LinkedList<Double>();
		p3s = new LinkedList<Double>();
		p4s = new LinkedList<Double>();
		p5s = new LinkedList<Double>();
		pLlhds = new LinkedList<Double>();
	}

	public void visit(int currentSample, StimRespState state, Density density) {
		StimRespDensity casted = (StimRespDensity) density;
		llhds.add(casted.likelihood.get().logDensity());
		priors.add(casted.prior.get().logDensity());
		p1s.add(((SimpleTCState) state.rf.get()).p1.get().getValue());
		p2s.add(((SimpleTCState) state.rf.get()).p2.get().getValue());
		p3s.add(((SimpleTCState) state.rf.get()).p3.get().getValue());
		p4s.add(((SimpleTCState) state.rf.get()).p4.get().getValue());
		p5s.add(((SimpleTCState) state.rf.get()).p5.get().getValue());
		DoubleState pLlhdState = ((SimpleTCState) state.rf.get()).pLlhd.get();
		if (pLlhdState != null) pLlhds.add(pLlhdState.getValue());
	}

}
