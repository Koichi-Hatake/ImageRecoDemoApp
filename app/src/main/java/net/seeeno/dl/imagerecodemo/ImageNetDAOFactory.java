/*
 * Copyright (C) 2019 Koichi Hatakeyama
 * All rights reserved.
 */
package net.seeeno.dl.imagerecodemo;

public class ImageNetDAOFactory {

    /** */
    public static ImageNetDAO getImageNetDAO(String networkName) {

        return ResNet18_DAO.getInstance();
    }

}
