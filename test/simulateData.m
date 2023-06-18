%% Simulate Data

% Define "experiment" and simulated neural responses
oriPerTrial      = 0:30:330;  % These are the orientations in the experiment
nrRepeats        =  12;    % Each orientation is shown this many times
nrTimePoints     = 10;      % 10 "bins" in each trial
dt               = 0.25;     % One bin is 100 ms.
tuningParms      = [0 0 90 45]; % Offset amplitude Preferred Kappa


% Generate data
ori         = repmat(oriPerTrial,[1 nrRepeats]);
nrTrials    = numel(oriPerTrial)*nrRepeats;
lambda      =   bayesPhys.getTCval(ori,'circular_gaussian_360',tuningParms);

figure(1);
clf

nrBoot = 800;
parms = nan(nrBoot,4);
bf = nan(nrBoot,1);
rate = 500;
parfor i=1:nrBoot
     nrSpikes = poissrnd(rate*ones(size(lambda))); % Completely random 
    %nrSpikes    = poissrnd(lambda); % The spike counts
    r = bayesFit(ori(:),nrSpikes(:),"compare","constant","fun","circular_gaussian_360","graphics",false,"probabilityModel","poisson");
    parms(i,:)  = r.median;
    bf(i)  = r.bf;
end

