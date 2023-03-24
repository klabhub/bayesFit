package edu.mit.bcs.bayesphys.core.kernels;

import edu.mit.csail.javablaise.core.kernels.ConcreteMixtureKernel;

public class StimRespKernel extends ConcreteMixtureKernel {
	static boolean adaptationEnabled;

	public static boolean isAdaptationEnabled() {
		return adaptationEnabled;
	}

	public static void setAdaptationEnabled(boolean enabled) {
		adaptationEnabled = enabled;
	}
}
