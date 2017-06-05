package com.framgia.fdms.screen.main;

import android.databinding.BaseObservable;
import android.databinding.Bindable;
import android.support.annotation.IntDef;
import android.support.design.widget.Snackbar;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import com.framgia.fdms.BR;
import com.framgia.fdms.data.model.Device;
import com.framgia.fdms.screen.scanner.ScannerActivity;

import static com.framgia.fdms.screen.main.MainViewModel.Tab.TAB_DASH_BOARD;
import static com.framgia.fdms.screen.main.MainViewModel.Tab.TAB_DEVICE_MANAGER;
import static com.framgia.fdms.screen.main.MainViewModel.Tab.TAB_REQUEST_MANAGER;
import static com.framgia.fdms.screen.scanner.ScannerViewModel.ScannerType.SCANNER_IN_MAIN;

/**
 * Exposes the data to be used in the Newmain screen.
 */
public class MainViewModel extends BaseObservable implements MainContract.ViewModel {
    private MainContract.Presenter mPresenter;
    private ViewPagerAdapter mPagerAdapter;
    private int mTab = TAB_DASH_BOARD;
    private AppCompatActivity mActivity;

    public MainViewModel(ViewPagerAdapter adapter, AppCompatActivity activity) {
        mPagerAdapter = adapter;
        mActivity = activity;
    }

    @Override
    public void onStart() {
        mPresenter.onStart();
    }

    @Override
    public void onStop() {
        mPresenter.onStop();
    }

    @Override
    public void setPresenter(MainContract.Presenter presenter) {
        mPresenter = presenter;
    }

    public ViewPagerAdapter getPagerAdapter() {
        return mPagerAdapter;
    }

    @Bindable
    public int getTab() {
        return mTab;
    }

    public void setTab(int tab) {
        mTab = tab;
        notifyPropertyChanged(BR.tab);
    }

    public void onDirectChildTab(@Tab int tab, ViewPager viewPager) {
        if (mPagerAdapter == null) return;
        viewPager.setCurrentItem(tab);
    }

    public void onStartScannerQrCode() {
        mActivity.startActivity(ScannerActivity.newIntent(mActivity, SCANNER_IN_MAIN));
    }

    @Override
    public void onGetDecodeSuccess(Device device) {
        // todo direct to detail device screen
        Snackbar.make(mActivity.findViewById(android.R.id.content), device.getDeviceCategoryName(),
                Snackbar.LENGTH_LONG).show();
    }

    @Override
    public void onGetDeviceError(String error) {
        Snackbar.make(mActivity.findViewById(android.R.id.content), error, Snackbar.LENGTH_LONG)
                .show();
    }

    @Override
    public void getResult(String resultQrCode) {
        if (mPresenter == null) return;
        mPresenter.getDevice(resultQrCode);
    }

    @IntDef({
            TAB_DASH_BOARD, TAB_REQUEST_MANAGER, TAB_DEVICE_MANAGER, Tab.TAB_PROFILE
    })
    public @interface Tab {
        int TAB_DASH_BOARD = 0;
        int TAB_REQUEST_MANAGER = 1;
        int TAB_DEVICE_MANAGER = 2;
        int TAB_PROFILE = 3;
    }
}
