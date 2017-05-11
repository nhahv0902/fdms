package com.framgia.fdms.screen.login;

import android.content.SharedPreferences;
import android.databinding.BaseObservable;
import android.text.TextUtils;
import com.framgia.fdms.data.model.Respone;
import com.framgia.fdms.data.model.User;
import com.framgia.fdms.data.source.UserRepository;
import com.framgia.fdms.data.source.local.sharepref.SharePreferenceApi;
import com.google.gson.Gson;
import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action0;
import rx.functions.Action1;
import rx.schedulers.Schedulers;
import rx.subscriptions.CompositeSubscription;

import static com.framgia.fdms.data.source.local.sharepref.SharePreferenceKey.USER_PREFS;

/**
 * Listens to user actions from the UI ({@link LoginActivity}), retrieves the data and updates
 * the UI as required.
 */
final class LoginPresenter extends BaseObservable implements LoginContract.Presenter {

    private LoginContract.ViewModel mView;
    private UserRepository mUserRepository;
    private CompositeSubscription mCompositeSubscriptions = new CompositeSubscription();
    private SharePreferenceApi mSharedPreferences;

    public LoginPresenter(LoginContract.ViewModel view, UserRepository userRepository,
            SharePreferenceApi sharedPreferences) {
        this.mView = view;
        mUserRepository = userRepository;
        mSharedPreferences = sharedPreferences;
    }

    @Override
    public void onStart() {
        if (mSharedPreferences.get(USER_PREFS, String.class) != null) {
            mView.onLoginSuccess();
        }
    }

    @Override
    public void onStop() {
        mCompositeSubscriptions.clear();
    }

    @Override
    public void login(String userName, String passWord) {
        Subscription subscription = mUserRepository.login(userName, passWord)
                .subscribeOn(Schedulers.io())
                .doOnSubscribe(new Action0() {
                    @Override
                    public void call() {
                        mView.showProgressbar();
                    }
                })
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Action1<Respone<User>>() {
                    @Override
                    public void call(Respone<User> userRespone) {
                        User user = userRespone.getData();
                        user.setToken(userRespone.getToken());
                        saveUser(user);
                        mView.onLoginSuccess();
                    }
                }, new Action1<Throwable>() {
                    @Override
                    public void call(Throwable throwable) {
                        mView.onLoginError();
                    }
                }, new Action0() {
                    @Override
                    public void call() {
                        mView.hideProgressbar();
                    }
                });
        mCompositeSubscriptions.add(subscription);
    }

    @Override
    public boolean validateDataInput(String username, String password) {
        boolean isValid = true;
        if (TextUtils.isEmpty(username)) {
            isValid = false;
            mView.onInputUserNameError();
        }
        if (TextUtils.isEmpty(password)) {
            isValid = false;
            mView.onInputPasswordError();
        }
        return isValid;
    }

    private void saveUser(User user) {
        Gson gson = new Gson();
        String json = gson.toJson(user);
        mSharedPreferences.put(USER_PREFS, json);
    }
}
