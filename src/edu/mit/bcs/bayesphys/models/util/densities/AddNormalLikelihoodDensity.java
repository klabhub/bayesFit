package edu.mit.bcs.bayesphys.models.util.densities;

import edu.mit.bcs.bayesphys.core.densities.SingleResponseLikelihoodDensity;
import edu.mit.csail.javablaise.core.densities.Density;
import edu.mit.csail.javablaise.core.namespaces.Link;
import edu.mit.csail.javablaise.core.util.DoubleState;
import edu.mit.csail.javablaise.distributions.Distributions;
import edu.mit.csail.javablaise.distributions.archetypes.NormalArchetype;

public class AddNormalLikelihoodDensity extends SingleResponseLikelihoodDensity {
	NormalArchetype dist = Distributions.defaultFactory().normalArchetype();
	public final Link<Density, DoubleState, String> sigma = declareDependency("sigma");
	
	@Override
	public double computeSingleLogDensity(double modelVal, double dataVal) {
		return dist.logPdf(dataVal, modelVal, sigma.get().getValue());
	}
}
