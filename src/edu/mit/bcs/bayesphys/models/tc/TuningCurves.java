package edu.mit.bcs.bayesphys.models.tc;

import java.lang.Math;
import edu.mit.csail.javablaise.util.Numerical;

public class TuningCurves {
	public static final double velocityTuning1(double x, double a1, double a2,
			double mu1, double mu2, double sigma) {
		double g1 = a1 * Math.exp(-((x-mu1)*(x-mu1) / (2*sigma*sigma)));
		double g2 = a2 * Math.exp(-((x-mu2)*(x-mu2) / (2*sigma*sigma)));
		return g1 + g2;
	}
	
	public static final double circularGaussian180(double x, 
			double b, double a, double po, double tw) {
		return circularGaussian(x,b,a,po,tw,180);
	}

	public static final double circularGaussian360(double x, 
			double b, double a, double po, double tw) {
		return circularGaussian(x,b,a,po,tw,360);
	}

	public static final double circularGaussian(double x, 
			double b, double a, double po, double tw, double p) {
		double y = b;
		for (int i = -4; i <= 4; i++) {
			y+=a*Math.exp(-Math.pow(x-po+i*p,2)/(2*Math.pow(tw, 2)));
		}
		return y;
	}
	
	public static final double directionSelectiveCircularGaussian(double x,
			double b, double a1, double po, double tw, double a2) {
		double y = b;
		y += circularGaussian360(x, 0, a1, po, tw);
		y += circularGaussian360(x, 0, a2, po + 180, tw);
		return y;
	}

	public static final double normedCircularGaussian180(double x, 
			double b, double a, double po, double tw) {
		return normedCircularGaussian(x,180,b,a,po,tw);
	}

	public static final double normedCircularGaussian(double x, double p, 
			double b, double a, double po, double tw) {
		double y = b;
		double w = po + (p/2);
		double z = 0;
		for (int i = -4; i <= 4; i++) {
			y+=a*Math.exp(-Math.pow(x-po-i*p,2)/(2*Math.pow(tw, 2)));
			z+=a*Math.exp(-Math.pow(w-po-i*2,2)/(2*Math.pow(tw, 2)));
		}
		if (tw > 48)
			System.out.println("po: "+po+", w: "+w+", y: "+y+", z: "+z+", b: "+b+", a: "+a);
		return y-z;
	}

	public static final double vonmises(double x, double p, double b, double a,
			double po, double tw) {
		//TODO implement
		return 0;
	}

	public static final double sigmoid(double x, double b, double a, double x0, double c) {
		return b + a*(1.0 / (1.0 + Math.exp(-c*(x-x0))));
	}

	public static final double rectifiedCosine(double x, double b, double a, double x0, double c) {
		return b + a*Math.max(0, Math.cos(c*(x-x0)));
	}

	public static final double constant(double x, double b, double a, double x0, double c) {
		return b;
	}

	public static final double constant(double b) {
		return b;
	}

	public static final double linear(double x, double b, double a, double x0, double c) {
		return linear(x, b, a);
	}

	public static final double linear(double x, double b, double a) {
		return b + a*x;
	}

	public static final double gaussian(double x, double b, double a, double x0, double c) {
		double y = b + a * Math.exp(-Math.pow(x - x0,2) / (2 * Math.pow(c,2)));
		return y;
	}

	/* Log Gaussian */ 
	public static final double logGaussian(double x, double b, double a, double xp, double c, double x0) {
		double y = b + a * Math.exp(-Math.pow(Math.log((x + x0)/(xp+x0)),2) / (2 * Math.pow(c,2)));
		return y;
	}
	
	/**
	 * 
	 * @param x Spatial position
	 * @param t Temporal position
	 * @param sf Spatial frequency
	 * @param v Spatial velocity
	 * @param phi Spatial phase at t = 0
	 * @param mu_x Mean of spatial Gaussian
	 * @param sigma_x Std deviation of spatial Gaussian
	 * @param mu_t Mean of temporal Gaussian
	 * @param sigma_t Std deviation of temporal Gaussian
	 * @return
	 */
	public static final double spaceTimeGabor(double x, double t, double sf, double v, 
			double phi, double mu_x, double sigma_x, double mu_t, double sigma_t) {
		double temp1 = Math.sin((2.0*Math.PI*sf) * (Math.sin(v)*t + Math.cos(v)*x)+ phi);
		return temp1 * gaussian1DNonNorm(x,mu_x,sigma_x) * gaussian1DNonNorm(t,mu_t,sigma_t);
	}

	/**
	 * Evaluate a 1D Gaussian distribution at a single point
	 * @param x Evaluation point
	 * @param mu Mean
	 * @param sigma Standard deviation
	 * @return
	 */
	public static final double gaussian1D(double x, double mu, double sigma) {
		return (1.0 / (Math.sqrt(2 * Math.PI) * sigma)) * 
		Math.exp(-Math.pow(x - mu,2) / (2 * Math.pow(sigma,2)));
	}

	public static final double gaussian1DNonNorm(double x, double mu, double sigma) {
		return Math.exp(-Math.pow(x - mu,2) / (2 * Math.pow(sigma,2)));
	}

	public static final double spaceTimeGaborAlpha(double x, double t, double sf, double v,
			double phi, double mu, double sigma, double t0, double k, double n) {
		// X-Gabor, shifted by time according to velocity param
		double g = Math.sin(2.0*Math.PI*(x + t*v)*sf + phi) *
		Math.exp(-Math.pow((x + t*v) - mu,2)/(2*sigma*sigma));
		// Alpha function
		double t2 = Math.max(t-t0,0.0);
		double expn = Math.exp(n);
		double a = Math.pow(k*t2,expn) * Math.exp(-k*t2) * 
		(1.0/Numerical.gamma(expn+1) - (k*k*t2*t2) / Numerical.gamma(expn+3));
		return g*a;
	}
}
