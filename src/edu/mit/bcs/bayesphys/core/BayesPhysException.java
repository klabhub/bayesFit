package edu.mit.bcs.bayesphys.core;

public class BayesPhysException extends RuntimeException {
	public BayesPhysException(String msg) {
		super(msg);
	}

	public BayesPhysException(Exception e) {
		super(e);
	}
}
