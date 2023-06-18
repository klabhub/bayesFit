% I suspect that bayesFit has a bias to find 0-degree preferences more
% often. 
%
% Investigating this with simulated, known ground-truth data. 

%% parfor initialization
% To make sure each of the workers has access to Java, setup java on the client 
% before runnign this script:
TOOLBOX_HOME = extractBefore(which('bayesPhys.tc_sample'),'+bayesPhys');
packages = fullfile(TOOLBOX_HOME,'lib', ...
    {'blaise.jar', ...
    'bayesphys.jar', ...
    'trove.jar', ...
    'colt-free.jar'});
javaclasspath(packages);

%% Use workers on a cluster
c = kSlurm('Minutes',30,...                        
            'StartupFolder','/home/bart/Documents/MATLAB',...
            'AdditionalPaths',{'/home/bart/bayesFit'});
c.NumWorkers =64; % Limit to be nice
opts=  parforOpts(c);

%% Simulate Data
tic
% Define "experiment" and simulated neural responses.
% Each time bin will be treated as an independent measurement of the
% response to the orienation presented in that trial. If the analysis
% performed a fit on the mean response in a trial, set nrTimePoints=1.
oriPerTrial      = 0:30:330;  % These are the orientations in the experiment
nrRepeats        =  12;    % Each orientation is shown this many times
nrTimePoints     = 10;      % 10 "bins" in each trial

tuningParms      = [10 4 90 25]; % Offset amplitude Preferred Kappa

% Generate data
ori         = repmat(oriPerTrial,[1 nrRepeats]);
nrTrials    = numel(oriPerTrial)*nrRepeats;
% Do one example fit
lambda      =   bayesPhys.getTCval(ori,'circular_gaussian_360',tuningParms);
nrSpikes = poissrnd(lambda); 
clf
r = bayesFit(ori(:),nrSpikes(:),"compare","constant","fun","circular_gaussian_360","graphics",true,"probabilityModel","poisson");
 
%% Simulate many fits, each using eitehr
% Random spikes (isNull=true)
% Same parameters as above, but a random orientation preference
% (isNull=false)

isNull = false; % Set to true to simulate null hypothesis of no tuning.
nrBoot = 10000;
parms = nan(nrBoot,4);
bf = nan(nrBoot,1);
parfor (i=1:nrBoot,opts)
    if isNull
        % Simulate a completely random spike rate (no preferred ori)
        nrSpikes = poissrnd(tuningParms(1)*ones(size(lambda))); %#ok<*PFBNS> 
    else
        % Simulate based on the tuningParms defined above, but with a random preferred orientation        
        thisParms =tuningParms;
        thisParms(3) =randi(360);
        lambda      =   bayesPhys.getTCval(ori,'circular_gaussian_360',thisParms);
        nrSpikes = poissrnd(lambda); 
    end
    % Fit using bayesFit and compare against constant model
    r = bayesFit(ori(:),nrSpikes(:),"compare","constant","fun","circular_gaussian_360","graphics",false,"probabilityModel","poisson");
    parms(i,:)  = r.median;
    bf(i)  = r.bf;
end
toc

%% Visualize results
% Show distribution of preferred orientations and relationshwip with BF
figure(1);
clf
po =deg2rad(parms(:,3));
minBF =prctile(bf,90);
subplot(1,2,1)
polarplot(po,min(bf,minBF),'.')
hold on
polarplot(po(bf>minBF),minBF,'rx')
legend('All','Top 10%','Location','northoutside')
subplot(1,2,2)
edges = deg2rad(oriPerTrial)-0.5*deg2rad(30);
polarhistogram(po,edges,'Normalization','probability')
hold on
polarhistogram(po(bf>minBF),edges,'Normalization','probability')
legend('All','Top 10%','Location','northoutside')
% Statistical analysis
[pval,z] = circ_rtest(po); % Rayleigh R
m = circ_mean(po);
t = circ_confmean(po);
title(sprintf('M = %.0f +/- %.0f, R= %.2f (p=%.3f)',rad2deg(m),rad2deg(t),z,pval));
