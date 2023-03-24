package edu.mit.bcs.bayesphys.core.densities;

import java.util.Iterator;
import java.util.List;

import edu.mit.bcs.bayesphys.core.states.StimRespState;
import edu.mit.bcs.bayesphys.core.states.experiments.ResponseState;
import edu.mit.csail.javablaise.core.densities.Density;
import edu.mit.csail.javablaise.core.densities.DensityAbstract;
import edu.mit.csail.javablaise.core.namespaces.Link;

abstract public class SingleResponseLikelihoodDensity extends DensityAbstract {
	public Link<Density,StimRespState,String> state = declareDependency("state");
	public Link<Density,ResponseState,String> response = declareDependency("response");

	@Override
	protected double computeLogDensity() {
		double accum = 0;
	
		Object trialKey = getTrialKey();
		List<Double> modelResponse = this.state.get().nlr.get().computeNonlinearResponse(trialKey);
		List<Double> dataResponse = this.response.get().getData();
			
		Iterator<Double> dataIter = dataResponse.iterator();
		for (Double m : modelResponse) {
			Double d = dataIter.next();

			accum += this.computeSingleLogDensity(m, d);
		}
		return accum;
//		return computeDataModelLogDensity(modelResponse, dataResponse);
	}

//	abstract public double computeDataModelLogDensity(ModelResponse m, DataResponse r)
	
	abstract public double computeSingleLogDensity(double m, double d);
	
	private Object getTrialKey() {
		return response.get().getSuperstateLink().getKey();
	}
}
