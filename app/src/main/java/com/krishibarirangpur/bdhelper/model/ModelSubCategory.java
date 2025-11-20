package com.krishibarirangpur.bdhelper.model;

public class ModelSubCategory {

    private String categoryId;
    private String subCategoryId;
    private String subCategoryName;
    private int iconResId; // Uri এর পরিবর্তে int রাখলাম
    private boolean isSelected;

    public ModelSubCategory() {
    }

    public ModelSubCategory(String categoryId, String subCategoryId, String subCategoryName, int iconResId, boolean isSelected) {
        this.categoryId = categoryId;
        this.subCategoryId = subCategoryId;
        this.subCategoryName = subCategoryName;
        this.iconResId = iconResId;
        this.isSelected = isSelected;
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

    public int getIconResId() {
        return iconResId;
    }

    public void setIconResId(int iconResId) {
        this.iconResId = iconResId;
    }

    public boolean isSelected() {
        return isSelected;
    }

    public void setSelected(boolean selected) {
        isSelected = selected;
    }
}
