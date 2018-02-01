package com.tmind.orpheus.model;
/**
 * @COPYRIGHT (C) 2018 Schenker AG
 * <p>
 * All rights reserved
 */


/**
 * TODO The class VoiceUploadResult is supposed to be documented...
 *
 * @author Vani Li
 */
public class VoiceUploadResult {
    private int states;
    private String message;
    private String rank;
    private String level;

    public int getStates() {
        return states;
    }

    public String getLevel() {
        return level;
    }

    public void setLevel(String level) {
        this.level = level;
    }

    public void setStates(int states) {
        this.states = states;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getRank() {
        return rank;
    }

    public void setRank(String rank) {
        this.rank = rank;
    }
}
