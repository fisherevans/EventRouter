package com.fisherevans.eventRouter.test;

import com.fisherevans.eventRouter.*;

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
        Test t1 = new Test();

        EventRouter.init();
        EventRouter.subscribe(t1, true, CHANNEL_ONE);

        EventRouter.loadListeners("com.fisherevans.eventRouter.test");
        EventRouter.sendStatic(987);

        test(CHANNEL_ONE, ACTION_ONE, 4);
        test(CHANNEL_ONE, ACTION_ONE, "string");

        test(CHANNEL_ONE, ACTION_TWO, 4);
        test(CHANNEL_ONE, ACTION_ONE, "string");
        test(CHANNEL_ONE, ACTION_ONE, 10, 3);
        test(CHANNEL_TWO, ACTION_ONE);

        System.out.println("\n\n\n-----------------------------------------\n\n\n");
        final long start = System.currentTimeMillis();
        Runnable runnable = new Runnable() {
            @Override
            public void run() {
                while(true) {
                    if(test(CHANNEL_ONE, ACTION_ONE, 3) == 2) {
                        System.out.println("\n\n\nLost reference in " + ((System.currentTimeMillis()-start)/1000) + "s");
                        break;
                    }
                    try {
                        Thread.sleep(1000);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }
        };
        Thread thread = new Thread(runnable);
        thread.start();
        t1 = null;
        System.gc();
    }

    public static int test(Object channel, long action, Object ... args) {
        System.out.println("Sending action:" + action + " across channel:'" + channel.toString() + "'");
        int returnCode = 0;

        if(args.length > 0) {
            System.out.println("   Arguments:");
            for(Object arg:args)
                System.out.println("      " + arg.getClass().getName() + ": " + arg.toString());
        } else {
            System.out.println("   No Arguments");
        }

        EventActionSendResult sendResult = EventRouter.send(channel, action, args);

        if(sendResult.getResults() != null && sendResult.getResults().size() > 0) {
            System.out.println("   Results:");
            for(EventActionResult result:sendResult.getResults()) {
                System.out.println("      Got: " + result.getResult());
                System.out.println("         by calling: " + result.getMethod().getName());
                System.out.println("         on: " + result.getObjectInvoked().toString());
            }
        } else {
            returnCode++;
            System.out.println("   No Results");
        }

        if(sendResult.getErrors() != null && sendResult.getErrors().size() > 0) {
            System.out.println("   Errors:");
            for(EventActionError error:sendResult.getErrors()) {
                System.out.println("      Exception: " + error.getException().getClass().getName());
                System.out.println("         with message: " + error.getException().getMessage());
                System.out.println("         by calling: " + error.getMethod().getName());
                System.out.println("         on: " + error.getReceivingObject().toString());
            }
        } else {
            returnCode++;
            System.out.println("   No Errors");
        }

        System.out.println();

        return returnCode;
    }

    @EventAction(ACTION_ONE)
    public Double square(double x) {
        return x*x;
    }

    @EventAction(ACTION_TWO)
    public Double cube(double x) {
        return x*x*x;
    }

    @StaticEventAction(987)
    public static void testStatic() {
        System.out.println("Static method action");
    }
}
