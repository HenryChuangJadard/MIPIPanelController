/*
 *  /*************************************************************************
 *  *
 *  * Jadard Technology Inc. CONFIDENTIAL
 *  * __________________
 *  *  All Rights Reserved.
 *  * 2018 MIPIPanelController
 *  * NOTICE:  All information contained herein is, and remains  the property of Jadard Technology Inc..
 *  * The intellectual and technical concepts contained herein are proprietary to Jadard Technology Inc.
 *  * patents in process, and are protected by trade secret or copyright law.
 *  * Dissemination of this information or reproduction of this material is strictly forbidden unless prior
 *  * written permission is obtained from Jadard Technology Inc..
 *
 */

package com.jadard.henry.jpvr.Utils;

public class DsiCmd {
    private String Address = "";
    private String Value = "";
    private int Delay_ms = 0;
    public DsiCmd(String add, String value){
        this.Address = add;
        this.Value = value;
    }

    DsiCmd(String add){
        this.Address = add;
    }

    DsiCmd(int add, int value){
        this.Address = String.format("%2X",add);
        this.Value = String.format("%2X",value);
    }

    public void setAddress(String add){this.Address=add;}
    public void setValue(String value){this.Value=value;}
    public void setDelay_ms(int ms){this.Delay_ms = ms;}

    public String getAddress(){return this.Address;}
    public String getValue(){return this.Value;}
    public int getDelay_ms(){return this.Delay_ms;}

    @Override
    public String toString(){

        return this.Address+":"+this.Value+"- Delay "+this.Delay_ms+" ms.";
    }
}
