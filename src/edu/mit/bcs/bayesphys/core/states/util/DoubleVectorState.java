package edu.mit.bcs.bayesphys.core.states.util;

import edu.mit.bcs.bayesphys.core.BayesPhysException;
import edu.mit.csail.javablaise.core.namespaces.CloneContext;
import edu.mit.csail.javablaise.core.states.State;
import edu.mit.csail.javablaise.core.states.StateAbstract;

public class DoubleVectorState extends StateAbstract {
	double[] x;
	final int n;
	
	DoubleVectorState(int n) {
		super();
		this.n = n;
	}
	
	@Override
	public void cloneFrom(State other, CloneContext context) {
		// TODO Auto-generated method stub
		super.cloneFrom(other, context);
	}
	
	public double[] getX() {
		return x;
	}
	
	public void setX(double[] x) {
		if (x.length != n) 
			throw new BayesPhysException("length "+x.length+" does not match n "+n);
		this.x = x;
	}
}
