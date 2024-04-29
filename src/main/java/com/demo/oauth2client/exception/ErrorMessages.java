package com.demo.oauth2client.exception;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@ToString
@NoArgsConstructor
public class ErrorMessages implements Serializable {

    private String id;
    private List<ErrorAttributes> errors = new ArrayList<>();

    public ErrorMessages(String id) {
        this.id = id;
    }

    public ErrorMessages(String id, ErrorAttributes errorAttributes) {
        this.id = id;
        addMessage(errorAttributes);
    }

    public void addMessage(ErrorAttributes errorAttributes) {
        errors.add(errorAttributes);
    }

    public void addAllMessages(List<ErrorAttributes> errorAttributes) {
        for (ErrorAttributes attributes : errorAttributes) {
            errors.add(attributes);
        }
    }
}
