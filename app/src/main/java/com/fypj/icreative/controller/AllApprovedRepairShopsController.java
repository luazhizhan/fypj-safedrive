package com.fypj.icreative.controller;

import com.fypj.icreative.model.ApprovedRepairShopModel;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by User on 7/14/2016.
 */
public class AllApprovedRepairShopsController {
    public List<ApprovedRepairShopModel> allAvivaApprovedRepairShop() {
        List<ApprovedRepairShopModel> approvedRepairShopModelList = new ArrayList<ApprovedRepairShopModel>();
        //North
        ApprovedRepairShopModel approvedRepairShopModel1 = new ApprovedRepairShopModel("Ah Lim Motor Company",
                "10 Ang Mo Kio Ind. Park 2A #01-09 AMK Auto Point",
                "64831244", "North", "");
        ApprovedRepairShopModel approvedRepairShopModel2 = new ApprovedRepairShopModel(" Ah Lim Motor Company (Branch)",
                "176, Sin Ming Drive, #05-12 Sin Ming Autocare",
                "64563637", "North", "");
        ApprovedRepairShopModel approvedRepairShopModel3 = new ApprovedRepairShopModel("Hua Hong Pte Ltd",
                "25D Sungei Kadut Street 1",
                "67608815", "North", "");
        ApprovedRepairShopModel approvedRepairShopModel4 = new ApprovedRepairShopModel("Automotive Repair Centre Pte Ltd",
                " 38, Woodlands Industrial Park E1, #05-18",
                "64688834", "North", "");

        //East
        ApprovedRepairShopModel approvedRepairShopModel5 = new ApprovedRepairShopModel("ETHOZ Protect Pte Ltd (Branch)",
                "22 Tampines Street 92",
                "66547777", "East", "");
        ApprovedRepairShopModel approvedRepairShopModel6 = new ApprovedRepairShopModel("Progressive Automotive",
                "Blk 3022A Ubi Road 1 #01-45",
                "67415336", "East", "");
        ApprovedRepairShopModel approvedRepairShopModel7 = new ApprovedRepairShopModel("Glass-Fix (Windscreen Repairer)",
                "52 Ubi Avenue 3 #04-42 Frontier E Park @ Ubi",
                "62780887", "East", "Windscreen Repairer");

        //West
        ApprovedRepairShopModel approvedRepairShopModel8 = new ApprovedRepairShopModel("ETHOZ Protect Pte Ltd (Main)",
                "30 Bukit Batok Crescent (West)",
                "66547777", "West", "");
        ApprovedRepairShopModel approvedRepairShopModel9 = new ApprovedRepairShopModel(" Glass-Fix (Windscreen Repairer, Branch)",
                "1 Bukit Batok Crescent #08-11 WCEGA Plaza",
                "65703906", "West", "Windscreen Repairer");

        //South
        ApprovedRepairShopModel approvedRepairShopModel10 = new ApprovedRepairShopModel("Charn's Customcraft",
                "Blk 1010 Bukit Merah Lane 3 #01-105 (South)",
                "62717054", "South", "");

        approvedRepairShopModelList.add(approvedRepairShopModel1);
        approvedRepairShopModelList.add(approvedRepairShopModel2);
        approvedRepairShopModelList.add(approvedRepairShopModel3);
        approvedRepairShopModelList.add(approvedRepairShopModel4);
        approvedRepairShopModelList.add(approvedRepairShopModel5);
        approvedRepairShopModelList.add(approvedRepairShopModel6);
        approvedRepairShopModelList.add(approvedRepairShopModel7);
        approvedRepairShopModelList.add(approvedRepairShopModel8);
        approvedRepairShopModelList.add(approvedRepairShopModel9);
        approvedRepairShopModelList.add(approvedRepairShopModel10);

        return approvedRepairShopModelList;
    }
}
