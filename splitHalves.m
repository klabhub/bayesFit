function  [r,bf,parms,parmsError] = splitHalves(x,y,pv)
% Asess performance by comparing parameter estimates based on
% split halves of the data.
% If the estimation of the rate is successful then identical
% repeats of the same condition (e.g., stimulus orientation)
% should result in identical estimates of the underlying rate.
% Therefore the correlation between parameters based on one half of
% trials and comparing it with the other half of trials
% quantifies how well the algorithm works.
% Even a perfect estimator would have a correlation less than 1
% because of Poisson noise on the spike counts.
% See Pachitariu et al. J Neurosci 2018 38(37):7976 –7985.

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
    pv.opts (1,1) struct = struct('burnin_samples',1000,'num_samples',1000,'sample_period',50)
    pv.graphics(1,1) logical = false
    pv.outlierRemoval {mustBeMember(pv.outlierRemoval,{'none','median','mean','quartiles','grubbs','gesd'})} = 'none';
    pv.nrBoot =100;
    pv.nrWorkers =1;
end
uX = unique(x);
args= namedargs2cell(pv);
out = find(~cellfun(@(x) (ismember(x,{'fun','compare','probabilityModel','opts','graphics','outlierRemoval'})),args(1:2:end)))*2-1;
out  = [ out out+1];
args(out) =[];
resultsFullSet = bayesFit(x,y,args{:});

nrParms = size(resultsFullSet.median,2);

bootParmsOne   = nan(nrParms,pv.nrBoot);
bootParmsOther = nan(nrParms,pv.nrBoot);
bf = nan(pv.nrBoot,2);
r = nan(pv.nrBoot,1);
% Bootstrap fitting on split halves
parfor (i=1:pv.nrBoot, pv.nrWorkers)
    %    for i=1:pv.nrBoot % Uncomment for debugging
    [oneHalfTrials,otherHalfTrials] =resampleTrials(x,false,0.5) ;
    % First half
    oneResults = bayesFit(x(oneHalfTrials),y(oneHalfTrials),args{:}); %#ok<PFBNS>
    bootParmsOne(:,i) = oneResults.median';

    % Second half
    otherResults = bayesFit(x(otherHalfTrials),y(otherHalfTrials),args{:});
    bootParmsOther(:,i) = otherResults.median';

    bf(i,:) = [oneResults.bf otherResults.bf];
end


%% Determime correlations
for i=1:pv.nrBoot
    % Predict the tuning and correlate those
    tc1=bayesPhys.getTCval(uX,pv.fun,bootParmsOne(:,i));
    tc2=bayesPhys.getTCval(uX,pv.fun,bootParmsOther(:,i));
    r(i) = corr(tc1,tc2);
end

r = mean(r);
bf =mean(bf(:));
if nargout>2
    bp = cat(2,bootParmsOne,bootParmsOther);
    parms =mean(bp,2);
    parmsError = std(bp,0,2);
end

end

function [inSet,notInSet] = resampleTrials(stimulus,withReplacement,frac)
% Resample trials to use in bootstrapping. This resampling
% makes sure to resample trials from each of the unique
% stimulus conditions so that the resampled trials have all of
% the conditions.
%
% Input
% withReplacement - set to true to sample with replacement
% (used by bootstrapping).
% frac  - The fraction of trials to resample. 1 means resample
%           all, 0.5 with replacement=false means split halves.
% OUTPUT
% inSet - The set of selected trials
% outSet - The trials not in the set.
%
arguments
    stimulus (1,:) double
    withReplacement (1,1) logical = false
    frac (1,1) double {mustBeInRange(frac,0,1)} =  1
end
uStimulus = unique(stimulus);
stimCntr= 1;
trialsPerStim = cell(1,numel(uStimulus));
for u= uStimulus
    trialsPerStim{stimCntr} = find(stimulus==u);
    stimCntr = stimCntr+1;
end
if withReplacement
    % Sampling with replacement
    inSet = cellfun(@(x) (x(randi(numel(x),[1 ceil(frac*numel(x))]))),trialsPerStim,'uni',false);
else
    % Sampling without replacement (e.g. to split 80/20 or
    % split halves)
    inSet = cellfun(@(x) (x(randperm(numel(x),ceil(frac*numel(x))))),trialsPerStim,'uni',false);
end
notInSet = cellfun(@(x,y) setxor(x,y),trialsPerStim,inSet,'uni',false);
inSet = cat(2,inSet{:});
notInSet =cat(2,notInSet{:});
end