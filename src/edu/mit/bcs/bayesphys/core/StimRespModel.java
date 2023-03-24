package edu.mit.bcs.bayesphys.core;

import java.util.Collection;

import edu.mit.csail.javablaise.core.SDK;
import edu.mit.csail.javablaise.core.densities.Density;
import edu.mit.csail.javablaise.core.kernels.LetKernel;
import edu.mit.csail.javablaise.core.kernels.TransitionKernel;
import edu.mit.csail.javablaise.core.namespaces.Nameable;
import edu.mit.csail.javablaise.core.paths.Path;
import edu.mit.csail.javablaise.core.util.DoubleState;
import edu.mit.csail.javablaise.distributions.ScalarContinuousDistribution;
import edu.mit.bcs.bayesphys.core.densities.StimRespDensity;
import edu.mit.bcs.bayesphys.core.kernels.AdaptingDoubleDriftKernel;
import edu.mit.bcs.bayesphys.core.kernels.StimRespKernel;
import edu.mit.bcs.bayesphys.core.states.StimRespState;
import edu.mit.bcs.bayesphys.models.util.ContinuousDistributionDensity;

public class StimRespModel extends SDK<StimRespState> {

	public StimRespModel(StimRespState state, Density density, TransitionKernel kernel) {
		super(state, density, kernel);
	}

	
	public void addDoubleDriftKernel(Path path) {
		addDoubleDriftKernel(path, 1.0);
	}


	public void addPrior(Path path, ScalarContinuousDistribution dist) {
		StimRespDensity stimRespDensity = castDensity();
		Collection<Nameable<?>> collection = path.resolve(state);
		for (Nameable<?> nameable : collection) {
			if (nameable instanceof DoubleState) {
				DoubleState paramState = (DoubleState) nameable;
				ContinuousDistributionDensity priorDensity = new ContinuousDistributionDensity(dist);
				priorDensity.x.set(paramState);
				stimRespDensity.addPrior(priorDensity);
			}
		}
	}
	
	public void addDoubleDriftKernel(Path path, double driftSigma) {
		StimRespKernel stimRespKernel = castKernel();
		
		Collection<Nameable<?>> paramStates = path.resolve(state);
		for (Nameable<?> nameable : paramStates) {
			if (nameable instanceof DoubleState) {
				AdaptingDoubleDriftKernel ddKernel = new AdaptingDoubleDriftKernel(driftSigma, 1.1);
				LetKernel letKernel = new LetKernel(path);
				letKernel.subkernel.set(ddKernel);
				stimRespKernel.putSubkernel(letKernel, 1.0);
			}
		}
	}
	
	public StimRespDensity castDensity() {
		if (density instanceof StimRespDensity)
			return (StimRespDensity) density;
		throw new RuntimeException("Wrong kind of density!!");
		
	}
	
	public StimRespKernel castKernel() {
		if (kernel instanceof StimRespKernel)
			return (StimRespKernel) kernel;
		throw new RuntimeException("Wrong kind of kernel!!");
	}
}
