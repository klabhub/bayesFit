package edu.mit.bcs.bayesphys.models.tc;

import edu.mit.bcs.bayesphys.core.kernels.StimRespKernel;
import edu.mit.bcs.bayesphys.core.states.StimRespState;
import edu.mit.bcs.bayesphys.util.Numerical;
import edu.mit.csail.javablaise.core.SDK;
import edu.mit.csail.javablaise.core.queries.PeriodicQuery;
import edu.mit.csail.javablaise.core.queries.Query;
import edu.mit.csail.javablaise.core.walk.Walk;
import edu.mit.csail.javablaise.core.walk.WalkControllerFixedLengthRun;
import edu.mit.csail.javablaise.distributions.Distributions;

public class WalkUtils {
	public static Walk<StimRespState> burninWalk(SDK<StimRespState> sdk, int numIters) {
		StimRespKernel.setAdaptationEnabled(true);
		WalkControllerFixedLengthRun<StimRespState> walkController = new WalkControllerFixedLengthRun<StimRespState>(numIters);
		Walk<StimRespState> walk = new Walk<StimRespState>(sdk, Distributions.randomSource(), walkController);
		return walk;
	}

	public static TCSampleCollectionObserver attachObserver(SDK<StimRespState> sdk, int samplePeriod) {
		TCSampleCollectionObserver observer = new TCSampleCollectionObserver();
		sdk.declareQuery(new PeriodicQuery<StimRespState>(observer, samplePeriod));
		return observer;
		
	}
	
	public static Walk<StimRespState> samplingWalk(SDK<StimRespState> sdk, int numIters) {
		StimRespKernel.setAdaptationEnabled(false);
		WalkControllerFixedLengthRun<StimRespState> walkController = new WalkControllerFixedLengthRun<StimRespState>(numIters);
		Walk<StimRespState> walk = new Walk<StimRespState>(sdk, Distributions.randomSource(), walkController);
		return walk;
	}
	
	public static double[] getP1Values(TCSampleCollectionObserver observer) {
		return Numerical.listToDoubleArray(observer.p1s);
	}
	
	public static double[] getP2Values(TCSampleCollectionObserver observer) {
		return Numerical.listToDoubleArray(observer.p2s);
	}
	
	public static double[] getP3Values(TCSampleCollectionObserver observer) {
		return Numerical.listToDoubleArray(observer.p3s);
	}
	
	public static double[] getP4Values(TCSampleCollectionObserver observer) {
		return Numerical.listToDoubleArray(observer.p4s);
	}
	
	public static double[] getP5Values(TCSampleCollectionObserver observer) {
		return Numerical.listToDoubleArray(observer.p5s);
	}
	
	public static double[] getPLlhdValues(TCSampleCollectionObserver observer) {
		return Numerical.listToDoubleArray(observer.pLlhds);
	}
	
	public static double[] getLlhdValues(TCSampleCollectionObserver observer) {
		return Numerical.listToDoubleArray(observer.llhds);
	}
	
	public static double[] getPriorValues(TCSampleCollectionObserver observer) {
		return Numerical.listToDoubleArray(observer.priors);
	}
}
