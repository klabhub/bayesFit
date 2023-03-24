package edu.mit.bcs.bayesphys.models.util;

import edu.mit.bcs.bayesphys.core.densities.SingleResponseLikelihoodDensity;
import edu.mit.bcs.bayesphys.core.states.util.DensityFactory;

public interface LikelihoodDensityFactory extends DensityFactory {
	public SingleResponseLikelihoodDensity createDensity();
}
