package com.ff.modealapplication.app.core.domain;

/**
 * Created by BIT on 2017-02-15.
 */

public class NoticeVo {

    private Long no;
    private String title;
    private String content;
    private String regDate;
    private String saveFileName;
    private Long categoryNo;
    private String category;

    @Override
    public String toString() {
        return "NoticeVo{" +
                "no=" + no +
                ", title='" + title + '\'' +
                ", content='" + content + '\'' +
                ", regDate='" + regDate + '\'' +
                ", saveFileName='" + saveFileName + '\'' +
                ", categoryNo=" + categoryNo +
                ", category='" + category + '\'' +
                '}';
    }

    public Long getNo() {
        return no;
    }

    public void setNo(Long no) {
        this.no = no;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getRegDate() {
        return regDate;
    }

    public void setRegDate(String regDate) {
        this.regDate = regDate;
    }

    public String getSaveFileName() {
        return saveFileName;
    }

    public void setSaveFileName(String saveFileName) {
        this.saveFileName = saveFileName;
    }

    public Long getCategoryNo() {
        return categoryNo;
    }

    public void setCategoryNo(Long categoryNo) {
        this.categoryNo = categoryNo;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }
}
