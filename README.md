# bayesFit

This code package performs fully Bayesian estimation of parametric tuning curves with various noise models, and contains examples for model comparison and hypothesis testing. It accompanies the following paper:

Hierarchical Bayesian modeling and Markov chain Monte Carlo sampling for tuning curve analysis (2010)
Cronin B*, Stevenson IH*, Sur M, and Körding KP. Journal of Neurophysiology 103: 591-602.
<http://jn.physiology.org/cgi/content/abstract/103/1/591>

Bart Krekelberg created this git repository and added some wrapper code and namespace protectio for  the original (java-based) bayesPhys package created by Ian Stevenson and Beau Cronin.

## Installation

1. Clone the repository from GitHub to a folder (e.g., ```'c:/github/bayesFit'```)
1. Add the folder to your Matlab path (```addpath('c:/github/bayesFit')```)
1. Use ```bayesFit(x,y,...)``` (see ```help bayesFit.fit```)

## Test

On the Matlab Command line, go to the test folder (```cd c:/github/bayesFit/test```)

1. Tuning Curve Estimation.
Run the script ```'test_tc_sample.m'```. This script contains code for estimating the parameters of a circular gaussian tuning curve from simulated data (see Fig 2B and Fig 3 in the paper). Code for estimating a cosine tuning curve is commented out (lines 9-12).
2. Model Comparison
The script ```'test_tc_hypothesistest'``` contains code for reproducing Fig 7 from the paper.
3. Additional tuning curve functions and noise models are described in ```'tc_sample.m'``` and the Supplementary Material for the paper.

## Versions

1.3 Ian Stevenson added a logGaussian tuning curve option with flat priors.

## Adding additional tuning curves

Based on an email from Ian Stevenson, one of the authors.

If you want to add more tuning curves the way to do it is as follows. Rather than compiling the entire package, I've just been compiling the tuning curve components.

1. Add functions to and compile TuningCurves.java and SimpleTCState.java with blaise.jar
Make sure you use an older JDK, otherwise Matlab will throw a “ Unsupported major.minor version” error when you try to sample
from the lib directory.
```javac -source 1.6 -target 1.6 -cp ./* ../src/edu/mit/bcs/bayesphys/models/tc/TuningCurves.java ../src/edu/mit/bcs/bayesphys/models/tc/SimpleTCState.java```

2. Add updated class files to bayesphys.jar (probably a good idea to save a backup just in case)
The directories are a bit tricky here, but you want to be in the src directory so that the updated .class files have a relative path “edu/mit/bcs/bayesphys/models/tc/”
```jar uf ../lib/bayesphys.jar edu/mit/bcs/bayesphys/models/tc/TuningCurves.class edu/mit/bcs/bayesphys/models/tc/SimpleTCState$LogGaussianEvaluator.class```
Now when running:
```jar tf ../lib/bayesphys.jar```
it should  show the *Evaluator.class that you added.
