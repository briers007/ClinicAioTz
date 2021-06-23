package com.minorfish.clinicwaste.module.bagin.weight;

import java.io.Serializable;

public class ScaleBean implements Serializable {
    public String sformatNetWeight = "0";
    public String sUnit = "0";
    public boolean bWeiStaFlag;
    public boolean bZeroFlag;
    public boolean bOverFlag;

    public String toString() {
        return sformatNetWeight + " | " + sUnit + " | " + bWeiStaFlag + " | " + bZeroFlag + " | " + bOverFlag;
    }
}