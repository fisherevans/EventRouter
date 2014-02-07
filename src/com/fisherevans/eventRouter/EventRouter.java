package com.fisherevans.eventRouter;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.util.*;

/**
 * A system to call object's preregistered methods by calling them on listening channels
 *
 * Author: Fisher Evans
 * Date: 2/5/14
 */
public class EventRouter {
    /** a cache of EventAction methods mapped to each known class **/
    private static Map<Class, List<EventActionMethod>> _actions;

    /** a map of Channel IDs and a list of Objects listening to that channel **/
    private static Map<Object, List<Object>> _channels;

    /**
     * Initialize this Event Router (allocate and instantiate private fields).
     * Can also be used to completely reset this EventRouter.
     */
    public synchronized static void init() {
        _actions = new HashMap<Class, List<EventActionMethod>>();
        _channels = new HashMap<Object, List<Object>>();
    }

    /**
     * Subscribe an object to receive EventAction calls on a given ChannelID
     * @param channelIds The channel ID the object should "listen" on
     * @param listener The object listening for event actions
     */
    public synchronized static void subscribe(Object listener, Object ... channelIds) {
        for(Object channelId:channelIds) {
            List<Object> channel = _channels.get(channelId);
            if(channel == null) {
                channel = new ArrayList<Object>();
                _channels.put(channelId, channel);
            }
            if(!channel.contains(listener))
                channel.add(listener);
            registerEventActions(listener.getClass());
        }
    }

    /**
     * Un subscribe an object from listening to a channel
     * @param channelIds the id of the channel you would like the object to be unsubscribed from
     * @param listener the object who no longer wants to listen on this channel
     */
    public synchronized static void unSubscribe(Object listener, Object ... channelIds) {
        for(Object channelId:channelIds) {
            List<Object> channel = _channels.get(channelId);
            if(channel != null) {
                channel.remove(listener);
                if(channel.isEmpty())
                    _channels.remove(channel);
            }
        }
    }

    /**
     * Un-subscribe the given object from all channels.
     * No event actions will be called on this object until it is re-subscribed.
     * @param listener the object who's done listening.
     */
    public synchronized static void unSubscribeAll(Object listener) {
        for(Object channelId:_channels.keySet())
            unSubscribe(channelId, listener);
    }

    /**
     * Send an Action to all objects listening to the given channel
     * @param channelId the channel to send the action event on
     * @param actionId the action to call
     * @param args the optional arguments to pass with the action
     * @return A list of errors and exceptions that may occur during transmission.
     *         Returns null if no errors were had.
     *         This method will continue to send actions if an error is had transmitting one action.
     */
    public synchronized static List<EventActionError> send(Object channelId, long actionId, Object ... args) {
        return send(new Object[] {channelId}, actionId, args);
    }

    /**
     * Send an action to all objects listening to the given channels
     * @param channelIds the channels to send the action event on
     * @param actionId the action to call
     * @param args the optional arguments to pass with the actions
     * @return A list of errors and exceptions that may occur during transmission.
     *         Returns null if no errors were had.
     *         This method will continue to send actions if an error is had transmitting one action.
     */
    public synchronized static List<EventActionError> send(Object[] channelIds, long actionId, Object ... args) {
        List<EventActionError> errors = null;
        for(Object channelId:channelIds) {
            List<Object> channel = _channels.get(channelId);
            if(channel != null) {
                for(Object listener:channel) {
                    for(EventActionMethod actionMethod:_actions.get(listener.getClass())) {
                        if(actionMethod.getActionId() == actionId) {
                            try {
                                actionMethod.getMethod().invoke(listener, args);
                            } catch (Exception e) {
                                if(errors == null)
                                    errors = new ArrayList<EventActionError>();
                                errors.add(new EventActionError(listener, actionId, actionMethod.getMethod(), e));
                            }
                        }
                    }
                }
            }
        }
        return errors;
    }

    /**
     * Used privately to dissect and cache any action event methods.
     * @param clazz the class to cache
     */
    private synchronized static void registerEventActions(Class clazz) {
        if(!_actions.containsKey(clazz)) {
            List<EventActionMethod> actionMethods = new ArrayList<EventActionMethod>();
            for(Method method:clazz.getMethods()) {
                for(Annotation annotation:method.getAnnotations()) {
                    if(annotation instanceof EventAction) {
                        EventAction eventAction = (EventAction) annotation;
                        actionMethods.add(new EventActionMethod(eventAction.value(), method));
                    } else if(annotation instanceof EventActions) {
                        EventActions eventActions = (EventActions) annotation;
                        for(long actionId:eventActions.value()) {
                            actionMethods.add(new EventActionMethod(actionId, method));
                        }
                    }
                }
            }
            _actions.put(clazz, actionMethods);
        }
    }

    /**
     * Used to store an action method and it's corresponding ID
     */
    private static class EventActionMethod {
        private final long _actionId;
        private final Method _method;

        /**
         * Create the ebent action method used for caching
         * @param actionId the action this method should be called with
         * @param method the reflection method to call
         */
        public EventActionMethod(long actionId, Method method) {
            _actionId = actionId;
            _method = method;
        }

        /**
         * @return the corresponding action id this method should be run on
         */
        private long getActionId() {
            return _actionId;
        }

        /**
         * @return the reflection method to call
         */
        private Method getMethod() {
            return _method;
        }
    }
}
