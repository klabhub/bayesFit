package edu.mit.bcs.bayesphys.core.densities;

import edu.mit.bcs.bayesphys.core.states.StimRespState;
import edu.mit.bcs.bayesphys.core.states.experiments.ResponseState;
import edu.mit.bcs.bayesphys.models.util.LikelihoodDensityFactory;
import edu.mit.csail.javablaise.core.densities.AutoMultBagDensity;
import edu.mit.csail.javablaise.core.densities.Density;
import edu.mit.csail.javablaise.core.namespaces.CloneContext;
import edu.mit.csail.javablaise.modeling.densities.AssociatedBagDensity;
import edu.mit.csail.javablaise.modeling.densities.HDDensity;

public class StimRespDensity extends HDDensity {
	public static class LikelihoodsAssociatedBagDensity extends
			AssociatedBagDensity<ResponseState, Object> {
		private LikelihoodDensityFactory factory;
		
		public LikelihoodsAssociatedBagDensity() {
			super();
		}
		
		@Override
		public void cloneFrom(Density other, CloneContext context) {
			super.cloneFrom(other, context);
			this.factory = ((LikelihoodsAssociatedBagDensity) other).factory;
		}

		private LikelihoodsAssociatedBagDensity(LikelihoodDensityFactory factory) {
			this.factory = factory;
		}

		@Override
		public Density createChild(ResponseState state) {
			SingleResponseLikelihoodDensity likelihoodDensity = factory.createDensity();
			likelihoodDensity.response.set(state);
			return likelihoodDensity;
		}
	}
	
	public void defaultConfiguration(StimRespState state, final LikelihoodDensityFactory factory) {
		AssociatedBagDensity<ResponseState, Object> associatedBagDensity = new LikelihoodsAssociatedBagDensity(factory);
		associatedBagDensity.associatedBag.set(state.experiment.get().responses.get());
		likelihood.set(associatedBagDensity);
		
		prior.set(new AutoMultBagDensity());
	}

	public void addPrior(Density density) {
		if (prior.get() == null) 
			prior.set(new AutoMultBagDensity());
		((AutoMultBagDensity) prior.get()).add(density);
	}
}
