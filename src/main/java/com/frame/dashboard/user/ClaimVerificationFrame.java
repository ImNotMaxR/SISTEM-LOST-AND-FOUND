package com.frame.dashboard.user;

import com.managers.ClaimManager;
import com.model.Report;
import com.model.User;

public class ClaimVerificationFrame extends com.frame.dashboard.shared.ClaimVerificationFrame {

    public ClaimVerificationFrame(User user, Report report, ClaimManager claimManager, Runnable onSaved) {
        super(user, report, claimManager, onSaved);
    }
}