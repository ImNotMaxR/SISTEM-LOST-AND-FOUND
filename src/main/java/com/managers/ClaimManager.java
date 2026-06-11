package com.managers;

import com.model.Claim;
import com.enumeration.ClaimStatus;
import com.interfaces.Managerable;
import java.util.ArrayList;
import java.util.HashMap;

public class ClaimManager implements Managerable{
    private ArrayList<Claim> claims;
    private HashMap<String, Claim> claimMap;

    public ClaimManager() {
        this.claims = new ArrayList<>();
        this.claimMap = new HashMap<>();
    }

    public void add(Object obj) {}

    public void delete(String id) {}

    public Claim findById(String id) {
        return null;
    }

    public void addClaim(Claim claim) {}

    public void deleteClaim(String claimId) {}

    public void processClaim(String claimId) {}

    public ArrayList<Claim> getAllClaims() {
        return null;
    }

    public ArrayList<Claim> getClaimsByStatus(ClaimStatus status) {
        return null;
    }
}