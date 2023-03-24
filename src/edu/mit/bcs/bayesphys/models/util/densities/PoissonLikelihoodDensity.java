package edu.mit.bcs.bayesphys.models.util.densities;

import edu.mit.bcs.bayesphys.core.densities.SingleResponseLikelihoodDensity;
import edu.mit.csail.javablaise.distributions.Distributions;
import edu.mit.csail.javablaise.distributions.archetypes.PoissonArchetype;

public class PoissonLikelihoodDensity extends SingleResponseLikelihoodDensity {
	PoissonArchetype dist = Distributions.defaultFactory().poissonArchetype();
	
	@Override
	public double computeSingleLogDensity(double modelVal, double dataVal) {
		return dist.logPdf((int) dataVal, modelVal);
	}
}
