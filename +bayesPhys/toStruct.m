function m = toStruct(tbx)
% The toolbox returns the results in a format that is very vector
% unfriendly. This function reshapes the struct to make it easier to use in
% subsequent Matlab analyses.
% INPUT
% tbx -  The struct output of tc_sample
% OUTPUT
% m - A reformatted struct with the same information.
%
% BK - Mar 2023.

maxN = 100; % tc_sample creates structs with names P1, P2, P3.. 
for i=1:maxN
    this = ['P' num2str(i)];
    if isfield(tbx,this)
        % The toolbox has this P output
        m.samples(:,i) = tbx.(this);
        m.median(i)= tbx.([this '_median']);
        m.ci(:,i) = tbx.([this '_CI'])';
    else
        % Done - assuming P1..PN are consecutive.
        break;
    end
end
m.log_prior= tbx.log_prior;
m.log_llhd = tbx.log_llhd;
m.log_post = tbx.log_post;

end