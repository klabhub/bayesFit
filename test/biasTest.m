%% Simulate Data
tic
% Define "experiment" and simulated neural responses.
% Each time bin will be treated as an independent measurement of the
% response to the orienation presented in that trial. If the analysis
% performed a fit on the mean response in a trial, set nrTimePoints=1.
oriPerTrial      = 0:30:330;  % These are the orientations in the experiment
nrRepeats        =  12;    % Each orientation is shown this many times
nrTimePoints     = 10;      % 10 "bins" in each trial
model = 'circular_gaussian_360';
tuningParms      = [10 4 190 25]; % Offset amplitude Preferred Kappa

% Generate data
ori         = repmat(oriPerTrial,[1 nrRepeats]);
nrTrials    = numel(oriPerTrial)*nrRepeats;
% Do one example fit
% exampleLambda      =   bayesPhys.getTCval(ori,model,tuningParms);
% nrSpikes = poissrnd(exampleLambda      );
% clf
% r = bayesFit(ori(:),nrSpikes(:),"compare","constant","fun",model,"graphics",true,"probabilityModel","poisson");

%% Simulate many fits, using either
% 1. Random spikes (isNull=true)
% 2. Same parameters as above, but a random orientation preference (isNull=false)

isNull = false; % Set to true to simulate null hypothesis of no tuning.
groundTruth = repmat((1:360)',4);
nrBoot = numel(groundTruth);
parms = nan(nrBoot,4);
bf = nan(nrBoot,1);
opts=  parforOpts(parpool,'Limit',nrBoot);
groundTruth = nan(nrBoot,1);
parfor (i=1:nrBoot,opts)
    if isNull
        % Simulate a completely random spike rate (no preferred ori)
        nrSpikes = poissrnd(tuningParms(1)*ones(size(ori))); %#ok<*PFBNS>
    else
        % Simulate based on the tuningParms defined above, but with a random preferred orientation
        thisParms =tuningParms;
        thisParms(3) = groundTruth(i);
        lambda      =   bayesPhys.getTCval(ori,model,thisParms);
        nrSpikes = poissrnd(lambda);
    end
    % Fit using bayesFit and compare against constant model
    r = bayesFit(ori(:),nrSpikes(:),"compare","constant","fun",model,"graphics",false,"probabilityModel","poisson");
    parms(i,:)  = r.median;
    bf(i)  = r.bf;
end
toc
po =deg2rad(parms(:,3));
out = isnan(po);
po = po(~out);
bf = bf(~out);
