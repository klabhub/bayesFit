function results= fit(x,y,pv)
% Use Hierarchical Bayes model and Markov Chain Monte Carlo Sampling for
% Tuning Curve analysis. This function relies on the bayesphys_v1 toolbox,
% the ideas and math behind this approach is described in
% Cronin et al J Neurophysiol. 2010 January; 103(1): 591?602.
%
% INPUT
% x - The stimulus (For circular variables, use degrees)
% y - The response  (e.g., spikes per second)
% Parameter/Value pairs:
% 'fun'    - The tuning function to fit. See getTCVal for a list of
%                   alloweable functions. Extending this list requires
%                   changes in the Java source code of the toolbox...
% 'compare'     -  The model to compare to. Use 'constant' to test against
%                   a constant model and thereby test for significant tuning.
% 'probabilityModel' - The probability model to use ('poisson')
% 'opts'        - Options for the Monte Carlo sampling.
% 'graphic'   - Show graphical output
%
% OUTPUT
% One struct per tuning curve with
% .samples - Monte Carlo samples of the tuning curve parameters
% .median - Median tuning curve (median posterior)
% ci       - 95% Confidence intervals for each of the parameters. These are
%           based on the *joint* posterior.
%
% BK  Jan 2013.
% Updated Mar 2023
arguments
    x
    y
    pv.fun {mustBeMember(pv.fun,{'constant','linear','gaussian',...
        'circular_gaussian_360','circular_gaussian_180', ...
        'direction_selective_circular_gaussian',...
        'sigmoid','velocity_tuning_1','rectifiedcosine', ...
        'positivecosine','loggaussian'})} 
    pv.compare {mustBeMember(pv.compare,{'constant','linear','gaussian',...
        'circular_gaussian_360','circular_gaussian_180','direction_selective_circular_gaussian',...
        'sigmoid','velocity_tuning_1','rectifiedcosine', ...
        'positivecosine','loggaussian'})} = 'constant'
    pv.probabilityModel {mustBeMember(pv.probabilityModel,{'poisson','negative_binomial','add_normal','mult_normal'})} ='add_normal'
    pv.opts (1,1) struct = struct('burnin_samples',5000,'num_samples',5000,'sample_period',50)
    pv.graphics(1,1) logical = true
    pv.outlierRemoval {mustBeMember(pv.outlierRemoval,{'none','median','mean','quartiles','grubbs','gesd'})} = 'none';
end
tbx = which('bayesPhys.tc_sample');
if isempty(tbx)
    error('This function needs the bayesFit toolbox (https://github.com/klabhub/bayesFit).');
end
if ~strcmpi(pv.outlierRemoval,"none")
     io = isoutlier(y,pv.outlierRemoval);
     x =x(~io);
     y = y(~io);
else
    io = 0;
end

% Scale to [-1 1]
maxY = max(y,[],"ComparisonMethod","abs");
y = y./maxY;

results = bayesPhys.toStruct(bayesPhys.tc_sample(x,y,pv.fun,pv.probabilityModel,pv.opts));
results.compare = bayesPhys.toStruct(bayesPhys.tc_sample(x,y,pv.compare,pv.probabilityModel,pv.opts));
results.bf = bayesPhys.compute_bf(results,results.compare);
results.parms = pv;
results.outliers = mean(io);


% Show output
if  pv.graphics
    % Raw data
    plot(x,y,'k.');
    hold on
    %  Median per x.
    [uX,~,xIx]=unique(x);
    m = accumarray(xIx,y,[],@(x) median(x,1));
    plot(uX,m,'g*','MarkerSize',15)
    
    % Samples of TC from the posterior
    nrSamples = size(results.samples,1);
    % Interpolate the TC
    xi = linspace(min(x),max(x),100)';       
    yi=zeros(nrSamples,length(xi));
    for i=1:nrSamples
        yi(i,:)=getTCval(xi,pv.fun,results.samples(i,:))';
    end
    plot(xi,yi,'Color',[0.8 0.8 0.8]);

    % And the median TC with a stdev shading based on the posterior
    e =0.5*std(yi,0,1)';
    yi=getTCval(xi,pv.fun,results.median);
    ploterr(xi,yi,e,'Color','r');
    xlabel 'X'
    title (['Bayes Fit : ' strrep(pv.fun,'_',' ') ': (' num2str(results.median,3) '), BF (vs ' pv.compare    ') : ' num2str(results.bf,3)]);
end


end
