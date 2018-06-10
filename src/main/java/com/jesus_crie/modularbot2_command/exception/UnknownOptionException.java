package com.jesus_crie.modularbot2_command.exception;

import javax.annotation.Nonnull;

public class UnknownOptionException extends RuntimeException {

    public UnknownOptionException(@Nonnull String option) {
        super("Unknown option: " + option);
    }
}
