package com.worldgn.connector;

/**
 * Created by WGN on 27-09-2017.
 */

class ApiResponse {

    private int table;
    private int success;
    private String description;
    private String message;


    public int getTable() {
        return table;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public void setTable(int table) {
        this.table = table;
    }

    public int getSuccess() {
        return success;
    }

    public void setSuccess(int success) {
        this.success = success;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }
}
