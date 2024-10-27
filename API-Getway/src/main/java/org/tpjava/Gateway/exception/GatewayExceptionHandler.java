package org.tpjava.Gateway.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(value = HttpStatus.NOT_FOUND)
public class GatewayExceptionHandler extends RuntimeException{
    private static final long serialVersionUID = 1L;
    public GatewayExceptionHandler(String message){
        super(message);
    }
}
