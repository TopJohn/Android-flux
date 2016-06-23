package com.john.flux.actions;


import com.john.flux.dispatcher.Dispatcher;

/**
 * Created by lgvalle on 02/08/15.
 */
public class ActionsCreator {

    protected final Dispatcher dispatcher;
    public ActionsCreator(Dispatcher dispatcher) {
        this.dispatcher = dispatcher;
    }

    public interface Actions{
        String ACTION_SUCCESS = "action_success";
        String ACTION_FAILED = "action_failed";
    }
    public interface Key{
        String KEY_DATA = "key_data";
        String KEY_ERROR = "key_error";
    }

}
