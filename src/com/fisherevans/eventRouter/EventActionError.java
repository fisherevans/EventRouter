package com.fisherevans.eventRouter;

import java.lang.reflect.Method;

/**
 * An error that occurred during event action invocation
 *
 * Author: Fisher Evans
 * Date: 2/6/14
 */
public class EventActionError {
    private final Object _receivingObject;
    private final long _actionId;
    private final Method _method;
    private final Exception _exception;

    /**
     * Creates the error
     * @param receivingObject the object that had the method you were trying to invoke
     * @param actionId the action ID
     * @param method the method that was attempted to be invoked
     * @param exception the exception that occured
     */
    public EventActionError(Object receivingObject, long actionId, Method method, Exception exception) {
        _receivingObject = receivingObject;
        _actionId = actionId;
        _method = method;
        _exception = exception;
    }

    /**
     * @return the object that was tried to invoke a method on
     */
    public Object getReceivingObject() {
        return _receivingObject;
    }

    /**
     * @return the action id that triggered the invocation attempt
     */
    public long getActionId() {
        return _actionId;
    }

    /**
     * @return the method on the object that is registered to the action id
     */
    public Method getMethod() {
        return _method;
    }

    /**
     * The original method thrown
     * @return
     */
    public Exception getException() {
        return _exception;
    }

    @Override
    public String toString() {
        return String.format("ActionId:%d, Receiving:%s, Method:%s, Type:%s, Exception:%s",
                _actionId, _receivingObject.toString(), _method.getName(),
                _exception.getClass().toString(), _exception.getMessage());
    }
}
