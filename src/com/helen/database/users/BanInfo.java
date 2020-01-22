package com.helen.database.users;

import java.time.LocalDate;
import java.util.List;

public class BanInfo {
    List<String> userNames;
    List<String> IPs;
    private boolean isSpecial;
    private String banReason;
    private LocalDate banEnd;

    BanInfo(List<String> userNames, List<String> IPs, String banReason, LocalDate bdate, boolean isSpecial) {
        this.userNames = userNames;
        this.IPs = IPs;
        this.banReason = banReason;
        this.banEnd = bdate;
        this.isSpecial = isSpecial;
    }

    public List<String> getUserNames() {
        return userNames;
    }

    public List<String> getIPs() {
        return IPs;
    }

    public String getBanReason() {
        return banReason;
    }

    public LocalDate getBanEnd() {
        return banEnd;
    }

    public boolean isSpecial() {
        return isSpecial;
    }

    @Override
    public String toString() {
        return "BanInfo{" +
                "userNames=" + userNames +
                ", IPs=" + IPs +
                ", isSpecial=" + isSpecial +
                ", banReason='" + banReason + '\'' +
                ", banEnd=" + banEnd +
                '}';
    }
}
