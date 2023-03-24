package edu.mit.bcs.bayesphys.core.kernels;

import edu.mit.csail.javablaise.core.densities.Density;
import edu.mit.csail.javablaise.core.kernels.KernelEnvironment;
import edu.mit.csail.javablaise.mcmc.metropolis_hastings.MHKernel;
import edu.mit.csail.javablaise.mcmc.metropolis_hastings.MHProposalListener;
import edu.mit.csail.javablaise.mcmc.util.DoubleDriftKernel;

public class AdaptingDoubleDriftKernel extends DoubleDriftKernel {
	public static class AdaptationListener implements MHProposalListener {
		double ratio = 1.1;
		
		public AdaptationListener(double ratio) {
			super();
			this.ratio = ratio;
		}

		public void preProposal(MHKernel kernel, Density density, KernelEnvironment environment) {
			return;
		}

		public void proposed(MHKernel kernel, Density density,
				KernelEnvironment environment,
				edu.mit.csail.javablaise.core.kernels.moves.Move proposal,
				double logAcceptenceProbability, boolean accepted) {
			AdaptingDoubleDriftKernel casted = (AdaptingDoubleDriftKernel) kernel;
			if (!StimRespKernel.isAdaptationEnabled()) return;
			double drift = casted.getDriftSigma();
			if (accepted) {
//				System.out.println("accepted "+ratio);
				casted.setDriftSigma(drift * ratio);
			} else {
//				System.out.println("rejected "+ratio);
				casted.setDriftSigma(drift / ratio);
			}
//			System.out.println(casted.getDriftSigma());
		}
	}

	public AdaptingDoubleDriftKernel(double driftSigma, double ratio) {
		super(driftSigma);
		this.addProposalListener(new AdaptationListener(ratio));		
	}
//	NormalDistribution dist;
//	
//	public AdaptingDoubleDriftKernel() {
//	}
//	
//	public AdaptingDoubleDriftKernel(RandomSource randomSource, double driftSigma, double ratio) {
//		super();
//		dist = Distributions.defaultFactory().normalWithMeanVariance(randomSource, 0, driftSigma * driftSigma);
//		this.addProposalListener(new AdaptationListener(ratio));
//	}
//	
//	public AdaptingDoubleDriftKernel(RandomSource randomSource, double driftSigma) {
//		super();
//		dist = Distributions.defaultFactory().normalWithMeanVariance(randomSource, 0, driftSigma * driftSigma);
//	}
}
