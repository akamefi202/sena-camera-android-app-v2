package com.sena.senacamera.listener;

public interface Callback {
    default void processSucceed() {
    }
    default void processFailed() {
    }
    default void processAbnormal() {
    }
    default void execute() {
    }
    default void execute(boolean value) {
    }
}
