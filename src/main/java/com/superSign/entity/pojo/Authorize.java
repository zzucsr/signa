package com.superSign.entity.pojo;

import lombok.Getter;
import lombok.Setter;

public class Authorize {

    @Getter
    @Setter
    private String csr;
    @Getter
    @Setter
    private String p8;
    @Getter
    @Setter
    private String iss;
    @Getter
    @Setter
    private String kid;


    public Authorize(String p8, String iss, String kid, String csr) {
        this.p8 = p8;
        this.iss = iss;
        this.kid = kid;
        this.csr = csr;
    }

    public Authorize(String p8, String iss, String kid) {
        this.p8 = p8;
        this.iss = iss;
        this.kid = kid;
    }



    @Override
    public String toString() {
        return "Authorize{" +
                "csr='" + csr + '\'' +
                ", p8='" + p8 + '\'' +
                ", iss='" + iss + '\'' +
                ", kid='" + kid + '\'' +
                '}';
    }
}
