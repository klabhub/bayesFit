package edu.mit.bcs.bayesphys.util;

import java.util.List;

public class Numerical {
	/**
	 * TODO: make this efficient for linked lists
	 * @param x1
	 * @param x2
	 */
	public static void sumInPlace(List<Double> x1, List<Double> x2) {
		if (x1.size() != x2.size()) {
			throw new RuntimeException("Lists must be of same size to add");
		}
		
		for (int i = 0; i < x1.size(); i++) {
			x1.set(i, x1.get(i) + x2.get(i));
		}
	}

	public static double[] listToDoubleArray(List<Double> list) {
		double[] da = new double[list.size()];
		int i = 0;
		for (Double d : list)
			da[i++] = d;
		return da;
	}
}
