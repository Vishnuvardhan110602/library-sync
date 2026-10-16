package com.example.library.service;

public class SyncAlreadyRunningException extends RuntimeException {
    public SyncAlreadyRunningException() {
        super("A synchronization is already running. Please wait for it to finish.");
    }
}