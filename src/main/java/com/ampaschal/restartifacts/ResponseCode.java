/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.ampaschal.restartifacts;

/**
 *
 * @author Nnanna
 *
 */
public enum ResponseCode {

	SUCCESS(0, "Success"),
    ERROR(-1, "Error"),
    ;

    private int code;
    private String description;

    private ResponseCode(int code, String description) {
        this.code = code;
        this.description = description;
    }

    public int getCode() {
        return this.code;
    }

    public String getDescription() {
        return this.description;
    }

}