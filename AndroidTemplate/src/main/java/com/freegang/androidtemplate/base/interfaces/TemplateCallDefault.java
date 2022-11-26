package com.freegang.androidtemplate.base.interfaces;


public interface TemplateCallDefault extends TemplateCall {

    @Override
    default <T> void call(T it, CallIt<T> callIt) {
        if (it == null) return;
        callIt.call(it);
    }

    @Override
    default <T> void call(T it, CallIt<T> callIt, CallNull callNull) {
        if (it == null) {
            callNull.call();
        } else {
            callIt.call(it);
        }
    }
}