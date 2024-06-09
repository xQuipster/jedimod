package com.xquipster.jedimod.api;

public class AbilityCd {
    private final int cd;
    private final int maxCd;
    public AbilityCd(int cd, int maxCd){
        this.cd = cd;
        this.maxCd = maxCd;
    }
    private AbilityCd(){
        cd = 0;
        maxCd = 0;
    }

    public int getCd() {
        return cd;
    }

    public int getMaxCd() {
        return maxCd;
    }
}
