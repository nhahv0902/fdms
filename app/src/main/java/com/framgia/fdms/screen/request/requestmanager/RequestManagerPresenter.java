package com.framgia.fdms.screen.request.requestmanager;

import com.framgia.fdms.data.model.Request;
import com.framgia.fdms.data.model.Status;
import com.framgia.fdms.data.source.RequestRepositoryContract;
import com.framgia.fdms.data.source.StatusRepository;
import java.util.List;
import rx.Subscriber;
import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action0;
import rx.schedulers.Schedulers;
import rx.subscriptions.CompositeSubscription;

import static com.framgia.fdms.screen.request.RequestPagerAdapter.RequestPage.MANAGER_REQUEST;
import static com.framgia.fdms.utils.Constant.ALL_RELATIVE_ID;
import static com.framgia.fdms.utils.Constant.ALL_REQUEST_STATUS_ID;
import static com.framgia.fdms.utils.Constant.FIRST_PAGE;
import static com.framgia.fdms.utils.Constant.PER_PAGE;

/**
 * Listens to user actions from the UI ({@link RequestManagerFragment}), retrieves the data and
 * updates
 * the UI as required.
 */
final class RequestManagerPresenter implements RequestManagerContract.Presenter {
    private int mPage = FIRST_PAGE;
    private final RequestManagerContract.ViewModel mViewModel;
    private CompositeSubscription mSubscription;
    private RequestRepositoryContract mRequestRepository;
    private StatusRepository mRepository;
    private int mRelativeId = ALL_RELATIVE_ID;
    private int mStatusId = ALL_REQUEST_STATUS_ID;

    public RequestManagerPresenter(RequestManagerContract.ViewModel viewModel,
            RequestRepositoryContract deviceRepository, StatusRepository statusRepository) {
        mViewModel = viewModel;
        mSubscription = new CompositeSubscription();
        mRequestRepository = deviceRepository;
        mRepository = statusRepository;
        getStatusDevice();
        getListRelative();
    }

    @Override
    public void onLoadMore() {
        mPage++;
        getRequest(mStatusId, mRelativeId, mPage, PER_PAGE);
    }

    @Override
    public void getData(Status relative, Status status) {
        mPage = FIRST_PAGE;
        if (relative != null) {
            mRelativeId = relative.getId();
        }
        if (status != null) {
            mStatusId = status.getId();
        }

        getRequest(mStatusId, mRelativeId, mPage, PER_PAGE);
    }

    @Override
    public void onStart() {
        getRequest(ALL_REQUEST_STATUS_ID, ALL_RELATIVE_ID, mPage, PER_PAGE);
    }

    @Override
    public void onStop() {
        mSubscription.clear();
    }

    @Override
    public void getRequest(int requestStatusId, int relativeId, int perPage, int page) {
        Subscription subscription =
                mRequestRepository.getRequests(MANAGER_REQUEST, requestStatusId, relativeId, page,
                        perPage)
                        .subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread())
                        .doOnSubscribe(new Action0() {
                            @Override
                            public void call() {
                                mViewModel.showProgressbar();
                            }
                        })
                        .subscribe(new Subscriber<List<Request>>() {
                            @Override
                            public void onCompleted() {
                                mViewModel.hideProgressbar();
                            }

                            @Override
                            public void onError(Throwable e) {
                                mViewModel.hideProgressbar();
                            }

                            @Override
                            public void onNext(List<Request> requests) {
                                mViewModel.onGetRequestSuccess(requests);
                            }
                        });

        mSubscription.add(subscription);
    }

    @Override
    public void getStatusDevice() {
        Subscription subscription = mRepository.getListStatusRequest()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Subscriber<List<Status>>() {
                    @Override
                    public void onCompleted() {
                        mViewModel.hideProgressbar();
                    }

                    @Override
                    public void onError(Throwable e) {
                        mViewModel.hideProgressbar();
                        mViewModel.onLoadError(e.getMessage());
                    }

                    @Override
                    public void onNext(List<Status> statuses) {
                        mViewModel.onGetStatusSuccess(statuses);
                    }
                });

        mSubscription.add(subscription);
    }

    @Override
    public void getListRelative() {
        Subscription subscription = mRepository.getListRelative()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Subscriber<List<Status>>() {
                    @Override
                    public void onCompleted() {
                        mViewModel.hideProgressbar();
                    }

                    @Override
                    public void onError(Throwable e) {
                        mViewModel.onLoadError(e.getMessage());
                        mViewModel.hideProgressbar();
                    }

                    @Override
                    public void onNext(List<Status> statuses) {
                        mViewModel.onGetRelativeSuccess(statuses);
                    }
                });

        mSubscription.add(subscription);
    }
}
