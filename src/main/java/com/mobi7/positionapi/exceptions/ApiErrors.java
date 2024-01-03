package com.mobi7.positionapi.exceptions;

import org.springframework.validation.BindingResult;

import java.util.List;
import java.util.stream.Collectors;

public class ApiErrors {

    private final List<String> errors;

    public ApiErrors(BindingResult bindingResult) {
        this.errors = bindingResult.getAllErrors().stream()
                .map(error -> error.getDefaultMessage())
                .collect(Collectors.toList());
    }

    public List<String> getErrors() {
        return errors;
    }
}
