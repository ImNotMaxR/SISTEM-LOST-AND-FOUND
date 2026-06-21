package com.frame.dashboard;

import com.model.User;
import com.service.AuthService;

public class DashboardSecurity extends DashboardUser {

    public DashboardSecurity() {
        this(AuthService.getCurrentUser());
    }

    public DashboardSecurity(User currentUser) {
        super(currentUser);
        setTitle("Dashboard Security - Sistem Lost & Found");
    }
}
