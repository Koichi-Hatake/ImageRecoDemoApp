/*
 * Copyright (C) 2019 Koichi Hatakeyama
 * All rights reserved.
 */
package net.seeeno.dl.imagerecodemo;

import java.util.ArrayList;
import java.util.List;

public class ImageNetDAOFactory {

    /** */
    private final static ImageNetDAO resNet18 = ResNet18_DAO.getInstance();
    private final static ImageNetDAO mobileNet = MobileNet_DAO.getInstance();
    private final static ImageNetDAO seNet154 = SENet154_DAO.getInstance();
    private final static ImageNetDAO squeezeNet1_1 = SqueezeNet_v1_1_DAO.getInstance();

    /** */
    private final static List<ImageNetDAO> imageNetList = new ArrayList<ImageNetDAO>() {
        {
            add(resNet18);
            add(mobileNet);
            add(seNet154);
            add(squeezeNet1_1);
        }
    };

    /** */
    public static ImageNetDAO getImageNetDAO(String networkName) {
        for(ImageNetDAO imageNetModel: imageNetList) {
            if(imageNetModel.getName().equals(networkName)) {
                return imageNetModel;
            }
        }
        return null;
    }

    /** */
    public static List<String> getLineup() {
        List imageNetModelsList = new ArrayList<String>();
        for(ImageNetDAO imageNetModel: imageNetList) {
            imageNetModelsList.add(imageNetModel.getName());
        }
        return imageNetModelsList;
    }

}
