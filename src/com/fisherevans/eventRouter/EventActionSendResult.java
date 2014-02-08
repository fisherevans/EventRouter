package com.fisherevans.eventRouter;

import java.util.List;

/**
 * Author: Fisher Evans
 * Date: 2/8/14
 */
public class EventActionSendResult {
    private final List<EventActionResult> _results;
    private final List<EventActionError> _errors;

    public EventActionSendResult(List<EventActionResult> results, List<EventActionError> errors) {
        _results = results;
        _errors = errors;
    }

    public List<EventActionResult> getResults() {
        return _results;
    }

    public List<EventActionError> getErrors() {
        return _errors;
    }
}
