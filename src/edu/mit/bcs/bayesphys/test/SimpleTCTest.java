package edu.mit.bcs.bayesphys.test;

import edu.mit.bcs.bayesphys.core.densities.StimRespDensity;
import edu.mit.bcs.bayesphys.core.states.StimRespState;
import edu.mit.bcs.bayesphys.core.states.experiments.AxisType;
import edu.mit.bcs.bayesphys.core.states.experiments.ElementType;
import edu.mit.bcs.bayesphys.core.states.experiments.ExperimentState;
import edu.mit.bcs.bayesphys.models.tc.SimpleTCState;
import edu.mit.bcs.bayesphys.models.tc.TCModelUtils;
import edu.mit.bcs.bayesphys.models.tc.WalkUtils;
import edu.mit.csail.javablaise.core.SDK;
import edu.mit.csail.javablaise.core.densities.Density;
import edu.mit.csail.javablaise.core.queries.PeriodicQuery;
import edu.mit.csail.javablaise.core.queries.Query;
import edu.mit.csail.javablaise.core.walk.Walk;

public class SimpleTCTest {
	private static final class SysOutObserver implements Query<StimRespState> {
		public void visit(int currentSample, StimRespState currentState, Density density) {
			StimRespDensity casted = (StimRespDensity) density;
			System.out.println(currentSample+" : "+casted.logDensity()+" = "+casted.prior.get().logDensity()+" + "+casted.likelihood.get().logDensity());
		}
	}

	private static final int BURNIN_ITERS = 5000;
	private static final int SAMPLING_ITERS = 10000;
	private static final int DISPLAY_PERIOD = 1000;
	private static final int SAMPLE_PERIOD = 100;

	static double[] stimVals = {0,22.5,45,67.5,90,112.5,135,157.5,180,202.5,225,247.5,
			270,292.5,315,337.5
	};
	
	static double[] y = {0,1,1,0,0,1,0,0,0,0,
			0,0,0,3,0,0,0,4,0,1,
			3,0,1,4,1,0,0,0,0,1,
			12,0,4,9,7,0,3,0,4,1,
			7,13,14,11,7,5,5,2,11,6,
			10,14,12,16,6,6,15,2,6,7,
			14,10,9,16,9,5,13,11,11,3,
			9,11,21,8,5,15,10,6,7,11,
			9,7,7,12,4,6,3,0,4,10,
			6,3,7,4,8,2,6,1,1,3,
			2,0,4,5,0,2,1,2,0,2,
			6,1,0,1,1,9,1,1,1,2,
			9,3,0,0,0,0,1,0,1,4,
			0,2,0,0,1,0,0,0,0,0,
			2,3,3,0,0,2,1,0,0,0,
			0,0,0,2,2,0,1,0,0,0
	};

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		System.out.println("Creating model");
//		SDK<StimRespState> sdk = TCModelUtils.createTCModel("CircularGaussian360");
		SDK<StimRespState> sdk = TCModelUtils.createEmptyTCModel();
		TCModelUtils.configureProbabilityModel(sdk, "add_normal");
		TCModelUtils.configureTCFunc(sdk, "CircularGaussian360");
		System.out.println("Adding parameters");
		TCModelUtils.setupParameterWithUniformPrior(sdk, 1, 5, 0, 100);
		TCModelUtils.setupParameterWithUniformPrior(sdk, 2, 5, 0, 100);
		TCModelUtils.setupParameterWithUniformPrior(sdk, 3, 100, 0, 360);
		TCModelUtils.setupParameterWithUniformPrior(sdk, 4, 30, 5, 90);
		
		// Set up experiment: stimuli, trials and responses
		System.out.println("Adding experiment");
		ExperimentState stimulusSetState = sdk.getState().experiment.get();
		stimulusSetState.addStream("x", AxisType.OTHER, ElementType.DOUBLE, 1);
		int k = 0;
		for (int i = 0; i < stimVals.length; i++) {
			Object stimKey = stimulusSetState.inventStimulus();
			stimulusSetState.addStimulusData(stimKey, "x", stimVals[i]);
			for (int j = 0; j < 10; j++) { 
				stimulusSetState.addTrial(stimKey,y[k]);
				k++;
			}
		}
		
		System.out.println("Creating burnin walk");
		Walk<StimRespState> burninEngine = WalkUtils.burninWalk(sdk, BURNIN_ITERS);
		sdk.declareQuery(new PeriodicQuery<StimRespState>(new SysOutObserver(), DISPLAY_PERIOD));
		System.out.print("Starting burnin walk...");
		burninEngine.go();
		System.out.println("done.");

		Walk<StimRespState> samplingEngine = WalkUtils.samplingWalk(burninEngine.getSDK(), SAMPLING_ITERS);
		System.out.print("Starting sampling walk...");
		samplingEngine.go();
		System.out.println("done.");
		
		StimRespState finalState = samplingEngine.getSDK().getState();
		System.out.println(samplingEngine.getSDK().getDensity().logDensity());
		System.out.println("p1 : "+ ((SimpleTCState) finalState.rf.get()).p1.get().value);
		System.out.println("p2 : "+ ((SimpleTCState) finalState.rf.get()).p2.get().value);
		System.out.println("p3 : "+ ((SimpleTCState) finalState.rf.get()).p3.get().value);
		System.out.println("p4 : "+ ((SimpleTCState) finalState.rf.get()).p4.get().value);
		System.out.println("pL : "+ ((SimpleTCState) finalState.rf.get()).pLlhd.get().value);
	}

}
