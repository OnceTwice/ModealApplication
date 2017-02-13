package com.ff.modealapplication.app.core.vo;

/**
 * Created by sun on 2017-02-12.
 */

public class HelpVo {
    private long no;
    private String title;
    private String complain;
    private String regDate;
    private long usersNo;

    @Override
    public String toString() {
        return "HelpVo{" +
                "no=" + no +
                ", title='" + title + '\'' +
                ", complain='" + complain + '\'' +
                ", regDate='" + regDate + '\'' +
                ", userNo=" + usersNo +
                '}';
    }

    public long getNo() {
        return no;
    }

    public void setNo(long no) {
        this.no = no;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getComplain() {
        return complain;
    }

    public void setComplain(String complain) {
        this.complain = complain;
    }

    public String getRegDate() {
        return regDate;
    }

    public void setRegDate(String regDate) {
        this.regDate = regDate;
    }

    public long getUserNo() {
        return usersNo;
    }

    public void setUserNo(long userNo) {
        this.usersNo = userNo;
    }


}
