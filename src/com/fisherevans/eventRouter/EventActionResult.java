package com.fisherevans.eventRouter;

import java.lang.reflect.Method;

/**
 * Author: Fisher Evans
 * Date: 2/8/14
 */
public class EventActionResult {
    private final Object _objectInvoked;
    private final Method _method;
    private final Object _result;

    public EventActionResult(Object result, Method method, Object objectInvoked) {
        _result = result;
        _method = method;
        _objectInvoked = objectInvoked;
    }

    public Object getObjectInvoked() {
        return _objectInvoked;
    }

    public Method getMethod() {
        return _method;
    }

    public Object getResult() {
        return _result;
    }
}
