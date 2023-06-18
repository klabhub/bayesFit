%% Simulate Data

% Define "experiment" and simulated neural responses
oriPerTrial      = 0:30:330;  % These are the orientations in the experiment
nrRepeats        =  12;    % Each orientation is shown this many times
nrTimePoints     = 10;      % 10 "bins" in each trial
dt               = 0.25;     % One bin is 100 ms.
tuningParms      = [100 0 90 45]; % Offset amplitude Preferred Kappa


% Generate data
ori         = repmat(oriPerTrial,[1 nrRepeats]);
nrTrials    = numel(oriPerTrial)*nrRepeats;
lambda      =   bayesPhys.getTCval(ori,'circular_gaussian_360',tuningParms);


nrBoot = 800;
parms = nan(nrBoot,4);
bf = nan(nrBoot,1);
parfor i=1:nrBoot
    if isNull
        % Simulate a completely random spike rate (no preferred ori)
        nrSpikes = poissrnd(tuningParms(1)*ones(size(lambda))); %#ok<*PFBNS> 
    else
        % Simulate based on the tuningParms defined above
        nrSpikes = poissrnd(lambda); 
    end
    % Fit using bayesFit
    r = bayesFit(ori(:),nrSpikes(:),"compare","constant","fun","circular_gaussian_360","graphics",false,"probabilityModel","poisson");
    parms(i,:)  = r.median;
    bf(i)  = r.bf;
end

%%
% Show distribution of preferred orientations and relationshwip with BF
figure(1);
clf
minBF =prctile(bf,90);
subplot(1,2,1)
polarplot(deg2rad(parms(:,3)),bf,'.')
hold on
polarplot(deg2rad(parms(bf>minBF,3)),bf(bf>minBF),'x')
legend('All','Top 10%','Location','northoutside')
subplot(1,2,2)
edges = deg2rad(oriPerTrial)-0.5*deg2rad(30);
polarhistogram(deg2rad(parms(:,3)),edges,'Normalization','probability')
hold on

polarhistogram(deg2rad(parms(bf>minBF,3)),edges,'Normalization','probability')
legend('All','Top 10%','Location','northoutside')