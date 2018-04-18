package com.mendix.demo.web.util;

import com.mendix.demo.web.response.ResponseEnvelope;

import java.io.IOException;
import java.io.Serializable;

/**
 * Created by naseers on 18/04/2018.
 */
public interface Validator<T extends Serializable> {

    ResponseEnvelope validate(String str) throws IOException;
}
