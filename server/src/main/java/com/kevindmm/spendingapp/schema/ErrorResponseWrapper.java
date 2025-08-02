package com.kevindmm.spendingapp.schema;

import java.util.List;

public class ErrorResponseWrapper {
    public List<ErrorResponse> errors;

    public List<ErrorResponse> getErrors() {
        return errors;
    }

    public void setErrors(List<ErrorResponse> errors) {
        this.errors = errors;
    }

    public ErrorResponseWrapper(List<ErrorResponse> errors) {
        this.errors = errors;
    }
}
