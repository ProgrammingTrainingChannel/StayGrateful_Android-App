package com.btitsolutions.staygrateful.Models;

/**
 * Created by Bereket on 5/11/2017.
 */

public class GratitudeModel {
    private String code;
    private String created_date;
    private String content;

    public GratitudeModel()
    {
    }

    public GratitudeModel(String code, String content, String created_date)
    {
        this.code = code;
        this.content = content;
        this.created_date = created_date;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getCreated_date() {
        return created_date;
    }

    public void setCreated_date(String created_date) {
        this.created_date = created_date;
    }
}
