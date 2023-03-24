package edu.mit.bcs.bayesphys.models.util;

import edu.mit.csail.javablaise.core.densities.Density;
import edu.mit.csail.javablaise.core.densities.DensityAbstract;
import edu.mit.csail.javablaise.core.namespaces.CloneContext;
import edu.mit.csail.javablaise.core.namespaces.Link;
import edu.mit.csail.javablaise.core.util.DoubleState;
import edu.mit.csail.javablaise.distributions.ScalarContinuousDistribution;

public final class ContinuousDistributionDensity extends DensityAbstract {
	public Link<Density,DoubleState,String> x = declareDependency("x");
	ScalarContinuousDistribution dist;
	
	public ContinuousDistributionDensity() {
		super();
	}

	
	public ContinuousDistributionDensity(ScalarContinuousDistribution dist) {
		this();
		this.dist = dist;
	}
	
	@Override
	public void cloneFrom(Density other, CloneContext context) {
		this.dist = ((ContinuousDistributionDensity) other).dist;
	}

	@Override
	protected double computeLogDensity() {
		return Math.log(dist.pdf(x.get().getValue()));
	}
}