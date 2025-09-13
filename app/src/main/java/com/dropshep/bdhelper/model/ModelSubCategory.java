package com.dropshep.bdhelper.model;

import android.net.Uri;

public class ModelSubCategory {

    private String categoryId;
    private String subCategoryId;
    private String subCategoryName;
    private Uri subCategoryImage;
    private boolean yesOrNo;

    public ModelSubCategory() {
    }

    public ModelSubCategory(String categoryId, String subCategoryId, String subCategoryName, Uri subCategoryImage, boolean yesOrNo) {
        this.categoryId = categoryId;
        this.subCategoryId = subCategoryId;
        this.subCategoryName = subCategoryName;
        this.subCategoryImage = subCategoryImage;
        this.yesOrNo = yesOrNo;
    }


    public String getCategoryId() {
        return categoryId;
    }

    public void setCategoryId(String categoryId) {
        this.categoryId = categoryId;
    }

    public String getSubCategoryId() {
        return subCategoryId;
    }

    public void setSubCategoryId(String subCategoryId) {
        this.subCategoryId = subCategoryId;
    }

    public String getSubCategoryName() {
        return subCategoryName;
    }

    public void setSubCategoryName(String subCategoryName) {
        this.subCategoryName = subCategoryName;
    }

    public Uri getSubCategoryImage() {
        return subCategoryImage;
    }

    public void setSubCategoryImage(Uri subCategoryImage) {
        this.subCategoryImage = subCategoryImage;
    }

    public boolean isYesOrNo() {
        return yesOrNo;
    }

    public void setYesOrNo(boolean yesOrNo) {
        this.yesOrNo = yesOrNo;
    }
}
