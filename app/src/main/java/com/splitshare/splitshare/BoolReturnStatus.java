package com.splitshare.splitshare;

/**
 * Created by armando on 10/13/17.
 * Use this class to return a boolean status with a message
 */

public class BoolReturnStatus {
    public boolean status;
    public String message;

    /* Default constructor
     * Creates a successful OK status message
     */
    public BoolReturnStatus() {
        this.status = true;
        this.message = "OK";
    }

    /* Create true/false status message
     * Default message: "NO DETAILS"
     */
    public BoolReturnStatus(boolean status) {
        this.status = status;
        this.message = "NO DETAILS";
    }

    /* Creates a fully customizeable
     * return status message
     */
    public BoolReturnStatus(boolean status, String message) {
        this.status = status;
        this.message = message;
    }
}
