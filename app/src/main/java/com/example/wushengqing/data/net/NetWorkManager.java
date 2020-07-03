package com.example.wushengqing.data.net;

import com.example.wushengqing.app.App;
import com.example.wushengqing.bus.event.SingleLiveEvent;
import com.example.wushengqing.data.entity.BaseBean;
import com.example.wushengqing.utils.RxUtils;
import com.trello.rxlifecycle2.LifecycleProvider;


import java.lang.ref.WeakReference;
import java.util.HashMap;
import java.util.Map;

import io.reactivex.Observable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.disposables.Disposable;
import io.reactivex.observers.DisposableObserver;
import io.reactivex.schedulers.Schedulers;


public class NetWorkManager {
    private WeakReference<LifecycleProvider> lifecycle;
    private static HashMap<String,CompositeDisposable> disposableHashMap;
    private UIChangeLiveData uc;
    private static class NetWorkManagerHolder {
        private static final NetWorkManager INSTANCE = new NetWorkManager();
    }

    public static final NetWorkManager getInstance() {
        if(disposableHashMap==null){
            disposableHashMap=new HashMap<>();
        }
        return NetWorkManagerHolder.INSTANCE;
    }



    public <T extends BaseBean> void execute(Observable<T> observable,DisposableObserver<T> subscriber) {
        observable.subscribeOn(Schedulers.io())
                .unsubscribeOn(Schedulers.io())
                .compose(RxUtils.handleGlobalError(App.getContext()))
                .observeOn(AndroidSchedulers.mainThread())
                //绑定生命周期，生命周期结束的时候解除网络订阅
                .compose(RxUtils.bindToLifecycle(lifecycle.get()))
                .subscribe(subscriber);
    }

    /**
     * 手动添加 disposable 绑定tag
     * @param disposable
     * @param tag
     */
    private void addSubscribe(Disposable disposable,String tag) {
        CompositeDisposable mCompositeDisposable = getCompositeDisposable(tag);
        mCompositeDisposable.add(disposable);
    }


    private CompositeDisposable getCompositeDisposable(String tag){
        CompositeDisposable mCompositeDisposable = disposableHashMap.get(tag);
        if (mCompositeDisposable == null) {
            mCompositeDisposable = new CompositeDisposable();
            disposableHashMap.put(tag,mCompositeDisposable);
        }
        return mCompositeDisposable;
    }


    /**
     * 取消请求
     *
     * @param tag
     */
    public void reMoveRequest(Object tag) {
        if(disposableHashMap.get(tag)!=null){
            disposableHashMap.get(tag).clear();
        }
    }


    public final class UIChangeLiveData  {
        private SingleLiveEvent<String> showDialogEvent;
        private SingleLiveEvent<Void> dismissDialogEvent;
        private SingleLiveEvent<Map<String, Object>> startActivityEvent;
        private SingleLiveEvent<Map<String, Object>> startContainerActivityEvent;
        private SingleLiveEvent<Void> finishEvent;
        private SingleLiveEvent<Void> onBackPressedEvent;

        public SingleLiveEvent<String> getShowDialogEvent() {
            return showDialogEvent = createLiveData(showDialogEvent);
        }

        public SingleLiveEvent<Void> getDismissDialogEvent() {
            return dismissDialogEvent = createLiveData(dismissDialogEvent);
        }

        public SingleLiveEvent<Map<String, Object>> getStartActivityEvent() {
            return startActivityEvent = createLiveData(startActivityEvent);
        }

        public SingleLiveEvent<Map<String, Object>> getStartContainerActivityEvent() {
            return startContainerActivityEvent = createLiveData(startContainerActivityEvent);
        }

        public SingleLiveEvent<Void> getFinishEvent() {
            return finishEvent = createLiveData(finishEvent);
        }

        public SingleLiveEvent<Void> getOnBackPressedEvent() {
            return onBackPressedEvent = createLiveData(onBackPressedEvent);
        }

        private <T> SingleLiveEvent<T> createLiveData(SingleLiveEvent<T> liveData) {
            if (liveData == null) {
                liveData = new SingleLiveEvent<>();
            }
            return liveData;
        }
    }

    public UIChangeLiveData getUC() {
        if (uc == null) {
            uc = new UIChangeLiveData();
        }
        return uc;
    }


    public void showDialog() {
        showDialog("请稍后...");
    }

    public void showDialog(String title) {
        uc.showDialogEvent.postValue(title);
    }

    public void dismissDialog() {
        uc.dismissDialogEvent.call();
    }


    /**
     * 注入RxLifecycle生命周期
     *
     * @param lifecycle
     */
    public void injectLifecycleProvider(LifecycleProvider lifecycle) {
        this.lifecycle = new WeakReference<>(lifecycle);
    }

    public LifecycleProvider getLifecycleProvider() {
        return lifecycle.get();
    }

}
