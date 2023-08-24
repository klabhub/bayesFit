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
c = kSlurm('Minutes',60,...
    'StartupFolder','/home/bart/Documents/MATLAB',...
    'AdditionalPaths',{'/home/bart/bayesFit'});
c.NumWorkers =128; % Limit to be nice


job = script(c,'biasTest','pool',127);

%% Visualize results
% Show distribution of preferred orientations and relationshwip with BF

load(job,'bf','po')


figure(1);
clf

minBF =prctile(bf,99);
subplot(1,2,1)
polarplot(po,min(bf,minBF),'.')
hold on
polarplot(po(bf>minBF),minBF,'rx')
legend('All','Top 10%','Location','northoutside')
subplot(1,2,2)
% Statistical analysis
if strcmpi(model,"circular_gaussian_180")
    [pval,z] = circ_rtest(2*po); % Rayleigh R
    edges = deg2rad(-15:30:345);
else
    [pval,z] = circ_rtest(po); % Rayleigh R
    edges = deg2rad(-7.5:15:360-7.5);
end

polarhistogram(po,edges,'Normalization','probability')
hold on
%polarhistogram(po(bf>minBF),edges,'Normalization','probability')
polarhistogram(deg2rad(groundTruth),edges,'Normalization','probability')
legend('All','Top 10%','Location','northoutside')

m = circ_mean(po);
t = circ_std(po);
title(sprintf('M = %.0f +/- %.0f, R= %.2f (p=%.3f)',rad2deg(m),rad2deg(t),z,pval));
