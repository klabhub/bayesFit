package edu.mit.bcs.bayesphys.models.tc;

import edu.mit.bcs.bayesphys.core.BayesPhysException;
import edu.mit.bcs.bayesphys.core.densities.SingleResponseLikelihoodDensity;
import edu.mit.bcs.bayesphys.core.densities.StimRespDensity;
import edu.mit.bcs.bayesphys.core.kernels.AdaptingDoubleDriftKernel;
import edu.mit.bcs.bayesphys.core.kernels.StimRespKernel;
import edu.mit.bcs.bayesphys.core.states.StimRespState;
import edu.mit.bcs.bayesphys.core.states.experiments.AxisType;
import edu.mit.bcs.bayesphys.core.states.experiments.ElementType;
import edu.mit.bcs.bayesphys.core.states.experiments.ExperimentState;
import edu.mit.bcs.bayesphys.models.util.ContinuousDistributionDensity;
import edu.mit.bcs.bayesphys.models.util.LikelihoodDensityFactory;
import edu.mit.bcs.bayesphys.models.util.densities.AddNormalLikelihoodDensity;
import edu.mit.bcs.bayesphys.models.util.densities.MultNormalLikelihoodDensity;
import edu.mit.bcs.bayesphys.models.util.densities.PoissonLikelihoodDensity;
import edu.mit.csail.javablaise.core.SDK;
import edu.mit.csail.javablaise.core.kernels.LetKernel;
import edu.mit.csail.javablaise.core.paths.Path;
import edu.mit.csail.javablaise.core.paths.elements.KeyPathElement;
import edu.mit.csail.javablaise.core.util.DoubleState;
import edu.mit.csail.javablaise.distributions.Distributions;
import edu.mit.csail.javablaise.distributions.NormalDistribution;
import edu.mit.csail.javablaise.distributions.ScalarContinuousDistribution;
import edu.mit.csail.javablaise.distributions.UniformDistribution;

public class TCModelUtils {
	public static SDK<StimRespState> createTCModel(String tcFuncName) {
		final StimRespState mainState = new StimRespState();
		mainState.defaultConfiguration();
				
		// Set up response model
		SimpleTCState rfState = new SimpleTCState();
		rfState.defaultConfiguration();
		rfState.setTCFunc(tcFuncName);
		mainState.setRF(rfState);
		
		// Set up density		
		StimRespDensity mainDensity = new StimRespDensity();
		LikelihoodDensityFactory densityFactory = new LikelihoodDensityFactory() {
			public SingleResponseLikelihoodDensity createDensity() {
				PoissonLikelihoodDensity likelihoodDensity = new PoissonLikelihoodDensity();
				likelihoodDensity.state.set(mainState);
				return likelihoodDensity;
			}
		};
		mainDensity.defaultConfiguration(mainState, densityFactory);

		StimRespKernel mainKernel = new StimRespKernel();

		SDK<StimRespState> sdk = new SDK<StimRespState>(mainState, mainDensity, mainKernel);
		return sdk;
	}
	
	/**
	 * Create an 'empty' tuning curve model.  This model must have its TC function set,
	 * and its probability model configured, before it can be used.  Intended for use
	 * from the Matlab toolbox.
	 */
	public static SDK<StimRespState> createEmptyTCModel() {
		final StimRespState mainState = new StimRespState();
		mainState.defaultConfiguration();
				
		SimpleTCState rfState = new SimpleTCState();
		rfState.defaultConfiguration();
		mainState.setRF(rfState);
		
		StimRespDensity mainDensity = new StimRespDensity();
		
		StimRespKernel mainKernel = new StimRespKernel();

		SDK<StimRespState> sdk = new SDK<StimRespState>(mainState, mainDensity, mainKernel);
		return sdk;		
	}
	
	/**
	 * Set the TC function for the model; works by calling the #setTCFunc method on the 
	 * TC state.
	 * @param sdk
	 * @param tcFuncName
	 */
	public static void configureTCFunc(SDK<StimRespState> sdk, String tcFuncName) {
		SimpleTCState rfState = (SimpleTCState) sdk.getState().rf.get();
		rfState.setTCFunc(tcFuncName);
	}
	
	public static void configureProbabilityModel(SDK<StimRespState> sdk, String probModelName) {
		final StimRespState mainState = sdk.getState();
		StimRespDensity mainDensity = (StimRespDensity) sdk.getDensity();
		
		LikelihoodDensityFactory densityFactory = null;
		if (probModelName.equalsIgnoreCase("poisson")) {
			densityFactory = new LikelihoodDensityFactory() {
				public SingleResponseLikelihoodDensity createDensity() {
					PoissonLikelihoodDensity likelihoodDensity = new PoissonLikelihoodDensity();
					likelihoodDensity.state.set(mainState);
					return likelihoodDensity;
				}
			};
			
			mainDensity.defaultConfiguration(mainState, densityFactory);
		} else if (probModelName.equalsIgnoreCase("negative_binomial")) {
			// TODO implement
		} else if (probModelName.equalsIgnoreCase("mult_normal")) {
			setupParameterWithUniformPrior(sdk, -1, 1.0, 0.0, 1.0e3);
			final DoubleState multState = ((SimpleTCState) mainState.rf.get()).pLlhd.get();
			
			densityFactory = new LikelihoodDensityFactory() {
				public SingleResponseLikelihoodDensity createDensity() {
					MultNormalLikelihoodDensity likelihoodDensity = new MultNormalLikelihoodDensity();
					likelihoodDensity.state.set(mainState);
					likelihoodDensity.multiplier.set(multState);
					return likelihoodDensity;
				}
			};
			
			mainDensity.defaultConfiguration(mainState, densityFactory);
		} else if (probModelName.equalsIgnoreCase("add_normal")) {
			setupParameterWithUniformPrior(sdk, -1, 1.0, 0.0, 1.0e3);
			final DoubleState sigmaState = ((SimpleTCState) mainState.rf.get()).pLlhd.get();
			
			densityFactory = new LikelihoodDensityFactory() {
				public SingleResponseLikelihoodDensity createDensity() {
					AddNormalLikelihoodDensity likelihoodDensity = new AddNormalLikelihoodDensity();
					likelihoodDensity.state.set(mainState);
					likelihoodDensity.sigma.set(sigmaState);
					return likelihoodDensity;
				}
			};

			mainDensity.defaultConfiguration(mainState, densityFactory);
		} else {
			throw new BayesPhysException("Probability model "+probModelName+" is not recognized");
		}
	}
	
	public static void setupParameter(SDK<StimRespState> sdk, int paramNum, double initialVal) {
		setupParameter(sdk, paramNum, initialVal, null);
	}
	
	public static void setupParameter(SDK<StimRespState> sdk, int paramNum, double initialVal, ScalarContinuousDistribution dist) {
		SimpleTCState tcState = (SimpleTCState) sdk.getState().rf.get();
		StimRespDensity density = (StimRespDensity) sdk.getDensity();
		StimRespKernel kernel = (StimRespKernel) sdk.getKernel();
		
		DoubleState pState = new DoubleState(initialVal);
		LetKernel letKernel = new LetKernel();
		letKernel.subkernel.set(new AdaptingDoubleDriftKernel(.1, 1.1));
		kernel.putSubkernel(letKernel, 1.0);
		switch (paramNum) {
		case -1:
			// the likelihood parameter
			tcState.pLlhd.set(pState);
			letKernel.path.rewrite((new Path()).add(new KeyPathElement(sdk.getState().rf)).add(new KeyPathElement(tcState.pLlhd)));
			break;
		case 1:
			tcState.p1.set(pState);
			letKernel.path.rewrite((new Path()).add(new KeyPathElement(sdk.getState().rf)).add(new KeyPathElement(tcState.p1)));
			break;
		case 2:
			tcState.p2.set(pState);
			letKernel.path.rewrite((new Path()).add(new KeyPathElement(sdk.getState().rf)).add(new KeyPathElement(tcState.p2)));
			break;
		case 3:
			tcState.p3.set(pState);
			letKernel.path.rewrite((new Path()).add(new KeyPathElement(sdk.getState().rf)).add(new KeyPathElement(tcState.p3)));
			break;
		case 4:
			tcState.p4.set(pState);
			letKernel.path.rewrite((new Path()).add(new KeyPathElement(sdk.getState().rf)).add(new KeyPathElement(tcState.p4)));
			break;
		case 5:
			tcState.p5.set(pState);
			letKernel.path.rewrite((new Path()).add(new KeyPathElement(sdk.getState().rf)).add(new KeyPathElement(tcState.p5)));
			break;
		default:
			throw new BayesPhysException("Parameter number must be between 1 and 5");
		}
		
		if (dist != null) {
			ContinuousDistributionDensity distDensity = new ContinuousDistributionDensity(dist);
			distDensity.x.set(pState);
			density.addPrior(distDensity);
		}
	}

	public static void setupParameterWithUniformPrior(SDK<StimRespState> sdk, int paramNum, double initialVal, double a, double b) {
		UniformDistribution dist = Distributions.defaultFactory().uniformWithInterval(Distributions.randomSource(), a, b);
		setupParameter(sdk, paramNum, initialVal, dist);
	}
	
	public static void setupParameterWithNormalPrior(SDK<StimRespState> sdk, int paramNum, double initialVal, double mean, double var) {
		NormalDistribution dist = Distributions.defaultFactory().normalWithMeanVariance(Distributions.randomSource(), mean, var);
		setupParameter(sdk, paramNum, initialVal, dist);
	}
	
	public static void addData(SDK<StimRespState> sdk, double[] x, double[] y) {
		ExperimentState stimulusSetState = sdk.getState().experiment.get();
		stimulusSetState.addStream("x", AxisType.OTHER, ElementType.DOUBLE, 1);
		for (int i = 0; i < x.length; i++) {
			Object stimKey = stimulusSetState.inventStimulus();
			stimulusSetState.addStimulusData(stimKey, "x", x[i]);
			stimulusSetState.addTrial(stimKey,y[i]);
		}
	}
}
