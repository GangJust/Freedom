package com.freegang.androidtemplate.base.interfaces;


public interface TemplateCall {
    //如果某一个对象不为空, 则回调CallIt
    <T> void call(T it, CallIt<T> callIt);

    //如果某一个对象不为空, 则回调CallIt, 否则回调CallNull
    <T> void call(T it, CallIt<T> callIt, CallNull callNull);

    interface CallIt<T> {
        void call(T it);
    }

    interface CallNull {
        void call();
    }
}