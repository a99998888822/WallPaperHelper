package com.example.octopus.wallpaperhelper.entity;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by octopus on 2019/3/14.
 */

public class ImageUriVOList {
    public static class imageUriVO{
        public int getImageId() {
            return imageId;
        }
        public void setImageId(int imageId) {
            this.imageId = imageId;
        }

        public String getImageUri() {
            return imageUri;
        }
        public void setImageUri(String imageUri) {
            this.imageUri = imageUri;
        }

        public int getImageLoc() {
            return imageLoc;
        }
        public void setImageLoc(int imageLoc) {
            this.imageLoc = imageLoc;
        }

        private int imageId;
        private String imageUri;
        private int imageLoc;
    }

    public static List<imageUriVO> getList() {
        return imageUriVO;
    }

    public static void setList(List<imageUriVO> list) {
        imageUriVO = list;
    }

    public static List<imageUriVO> imageUriVO = new ArrayList<>();

}
