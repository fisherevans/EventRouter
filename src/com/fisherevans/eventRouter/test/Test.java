package com.fisherevans.eventRouter.test;

import com.fisherevans.eventRouter.EventAction;
import com.fisherevans.eventRouter.EventActionError;
import com.fisherevans.eventRouter.EventActions;
import com.fisherevans.eventRouter.EventRouter;

import java.util.List;

/**
 * Author: Fisher Evans
 * Date: 2/6/14
 */
public class Test {
    public static final String CHANNEL_ONE = "test1";
    public static final String CHANNEL_TWO = "test2";
    public static final String CHANNEL_THR = "test3";

    public static final long ACTION_ONE = 1;
    public static final long ACTION_TWO = 2;
    public static final long ACTION_THR = 3;

    public static void main(String[] args) {
        Test t1 = new Test("Test One");
        Test t2 = new Test("Test Two");

        EventRouter.init();
        EventRouter.subscribe(t1, CHANNEL_ONE);
        EventRouter.subscribe(t2, CHANNEL_TWO);
        EventRouter.subscribe(t1, CHANNEL_THR);
        EventRouter.subscribe(t2, CHANNEL_THR);

        sendAction(CHANNEL_ONE, ACTION_ONE);
        sendAction(CHANNEL_TWO, ACTION_ONE);
        sendAction(CHANNEL_THR, ACTION_ONE);

        System.out.println("Should fail!");
        sendAction(CHANNEL_ONE, ACTION_TWO);
        sendAction(CHANNEL_ONE, ACTION_THR, "action thr");
        sendAction(CHANNEL_THR, ACTION_TWO, "Testing");

        EventRouter.unSubscribeAll(t2);
        sendAction(CHANNEL_THR, ACTION_TWO, "Should only call t1");
    }

    public static void sendAction(Object channelId, long actionId, Object ... args) {
        System.out.println("Sending action: " + actionId + " on channel: " + channelId + " with " + args.length + " arguments.");
        List<EventActionError> errors = EventRouter.send(channelId, actionId, args);
        if(errors != null) {
            for(EventActionError error:errors) {
                System.out.println("ERROR: " + error.toString());
            }
        }
        System.out.println();
    }

    private String _name;

    public Test(String name) {
        _name = name;
    }

    @EventAction(ACTION_ONE)
    public void testActionOne() {
        System.out.println(_name + " > Action 1 ran!");
    }

    @EventActions({ACTION_TWO, ACTION_THR})
    public synchronized void testActionTwo(String message) {
        System.out.println(_name + " > Action 2 got: " + message);
    }

    @EventAction(ACTION_THR)
    public void testActionThr(String message) {
        System.out.println(_name + " > Action 3 got: " + message);
    }
}
