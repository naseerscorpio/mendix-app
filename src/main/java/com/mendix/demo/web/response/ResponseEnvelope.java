package com.mendix.demo.web.response;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by naseers on 14/04/2018.
 */
public class ResponseEnvelope {

    private boolean success;
    private Object data;
    private List<String> errorMessages = new ArrayList<>();

    public ResponseEnvelope(Object data, boolean success) {
        this.success = success;
        this.data = data;
    }


    public ResponseEnvelope addErrorMessage(String error) {
        this.errorMessages.add(error);
        return this;
    }

    public boolean isSuccess() {
        return success;
    }

    public Object getData() {
        return data;
    }

    public List<String> getErrorMessages() {
        return errorMessages;
    }

    @Override
    public String toString() {
        return "ResponseEnvelope{" +
                "success=" + success +
                ", data=" + data +
                ", errorMessages=" + errorMessages +
                '}';
    }
}
