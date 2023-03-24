package edu.mit.bcs.bayesphys.models.tc;

import java.util.LinkedList;
import java.util.List;

import edu.mit.bcs.bayesphys.core.BayesPhysException;
import edu.mit.bcs.bayesphys.core.states.RFState;
import edu.mit.bcs.bayesphys.core.states.experiments.StimulusDescriptionState;
import edu.mit.bcs.bayesphys.core.states.experiments.StimulusState;
import edu.mit.csail.javablaise.core.namespaces.CloneContext;
import edu.mit.csail.javablaise.core.namespaces.Link;
import edu.mit.csail.javablaise.core.states.State;
import edu.mit.csail.javablaise.core.util.DoubleState;

public class SimpleTCState extends RFState {
	public interface TCEvaluator {
		public double eval(double x, double p1, double p2, double p3, double p4, double p5);
	}
	
	public static class GaussianEvaluator implements TCEvaluator {
		public double eval(double x, double p1, double p2, double p3, double p4, double p5) {
			return TuningCurves.gaussian(x, p1, p2, p3, p4);
		}
	}
	
	public static class CircularGaussian360Evaluator implements TCEvaluator {
		public double eval(double x, double p1, double p2, double p3, double p4, double p5) {
			return TuningCurves.circularGaussian360(x, p1, p2, p3, p4);
		}
	}
	
	public static class CircularGaussian180Evaluator implements TCEvaluator {
		public double eval(double x, double p1, double p2, double p3, double p4, double p5) {
			return TuningCurves.circularGaussian180(x, p1, p2, p3, p4);
		}
	}
	
	public static class DirectionSelectiveCircularGaussianEvaluator implements TCEvaluator {
		public double eval(double x, double p1, double p2, double p3, double p4, double p5) {
			return TuningCurves.directionSelectiveCircularGaussian(x, p1, p2, p3, p4, p5);
		}
	}

	public static class ConstantEvaluator implements TCEvaluator {
		public double eval(double x, double p1, double p2, double p3, double p4, double p5) {
			return TuningCurves.constant(x, p1, p2, p3, p4);
		}
	}

	public static class SigmoidEvaluator implements TCEvaluator {
		public double eval(double x, double p1, double p2, double p3, double p4, double p5) {
			return TuningCurves.sigmoid(x, p1, p2, p3, p4);
		}
	}
	
	public static class LogGaussianEvaluator implements TCEvaluator {
		public double eval(double x, double p1, double p2, double p3, double p4, double p5) {
			return TuningCurves.logGaussian(x, p1, p2, p3, p4, p5);
		}
	}

	public static class VelocityTuning1Evaluator implements TCEvaluator {
		public double eval(double x, double p1, double p2, double p3, double p4, double p5) {
			return TuningCurves.velocityTuning1(x, p1, p2, p3, p4, p5);
		}
	}

	public Link<State,DoubleState,String> p1 = declareSubstate("p1");
	public Link<State,DoubleState,String> p2 = declareSubstate("p2");
	public Link<State,DoubleState,String> p3 = declareSubstate("p3");
	public Link<State,DoubleState,String> p4 = declareSubstate("p4");
	public Link<State,DoubleState,String> p5 = declareSubstate("p5");
	public Link<State,DoubleState,String> pLlhd = declareSubstate("pLlhd");

	TCEvaluator evaluator;
	String tcFuncName;
	
	public void defaultConfiguration() {
		p1.set(new DoubleState());
		p2.set(new DoubleState());
		p3.set(new DoubleState());
		p4.set(new DoubleState());
		p5.set(new DoubleState());
	}
	
	@Override
	public void cloneFrom(State other, CloneContext context) {
		super.cloneFrom(other, context);
		this.evaluator = ((SimpleTCState) other).evaluator;
		this.tcFuncName = ((SimpleTCState) other).tcFuncName;
	}
	
	@SuppressWarnings("unchecked")
	public void setTCFunc(String tcFuncName) {
		String className = this.getClass().getName()+"$"+tcFuncName+"Evaluator";
		Class<TCEvaluator> evalClazz;
		try {
			evalClazz = (Class<TCEvaluator>) Class.forName(className);
			this.evaluator = evalClazz.newInstance();
		} catch (ClassNotFoundException e) {
			throw new RuntimeException(e);
		} catch (InstantiationException e) {
			throw new RuntimeException(e);
		} catch (IllegalAccessException e) {
			throw new RuntimeException(e);
		}
	}
	
	@Override
	public List<Double> computeResponseToStimulus(StimulusDescriptionState descriptionState, StimulusState stimState) {
		List<Double> response = new LinkedList<Double>();
		if (!descriptionState.hasStream("x")) throw new BayesPhysException("Stimuli do not have \"x\" stream");
		
		response.add(evaluator.eval((Double)stimState.getData("x"), p1.get().value, p2.get().value, p3.get().value, p4.get().value, p5.get().value));
		return response;
	}

}
