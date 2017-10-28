package com.a99live.zhibo.live.protocol.base;

import com.a99live.zhibo.live.net.ErrorThrowable;
import com.a99live.zhibo.live.net.LiveHttpClient;
import com.a99live.zhibo.live.net.LiveRequestParams;
import com.squareup.okhttp.Request;
import com.squareup.okhttp.Response;

import rx.Observable;
import rx.Subscriber;
import rx.schedulers.Schedulers;


/**
 * BaseProtocol
 */
public abstract class BaseProtocol {

    protected abstract String getPath();

    /**
     * 创建一个工作在IO线程的被观察者(被订阅者)对象
     *
     * @param method
     * @param params
     */

    protected Observable<String> createObservable(final String path, final String method, final LiveRequestParams params ) {

        return Observable.create(new Observable.OnSubscribe<String>() {
            @Override
            public void call(Subscriber<? super String> subscriber) {
                Request request = LiveHttpClient.getClient().getRequest(path, method, params);

                try {
                    Response response = LiveHttpClient.getClient().execute(request);
                    if (response.isSuccessful()) {
                        setData(subscriber, response.body().string(), true);
                    } else {
                        setData(subscriber, response.body().string(), false);
                    }
                } catch (Exception e) {
                    subscriber.onError(e);
                    subscriber.onCompleted();
                }
            }
        }).subscribeOn(Schedulers.io());
    }


    /**
     * 为观察者（订阅者）设置返回数据
     */
    protected void setData(Subscriber<? super String> subscriber, String json, boolean isSuccess) {
//        if (TextUtils.isEmpty(json)) {
//            subscriber.onError(new Throwable("not data"));
//            subscriber.onCompleted();
//            return;
//        }
        if (isSuccess) {
            subscriber.onNext(json);
            subscriber.onCompleted();
        } else {
            subscriber.onError(new ErrorThrowable(json)); //**** An exception is thrown here ***
            subscriber.onCompleted();
        }
    }
}
