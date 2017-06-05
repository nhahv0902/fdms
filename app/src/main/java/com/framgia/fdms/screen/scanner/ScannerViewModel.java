package com.framgia.fdms.screen.scanner;

import android.support.annotation.IntDef;

import static com.framgia.fdms.screen.scanner.ScannerViewModel.ScannerType.SCANNER_DEVICE;
import static com.framgia.fdms.screen.scanner.ScannerViewModel.ScannerType.SCANNER_IN_MAIN;

/**
 * Exposes the data to be used in the Scanner screen.
 */

public class ScannerViewModel implements ScannerContract.ViewModel {

    private final ScannerActivity mActivity;
    private ScannerContract.Presenter mPresenter;
    private int mTypeScanner = SCANNER_IN_MAIN;

    public ScannerViewModel(ScannerActivity activity, int typeScanner) {
        mActivity = activity;
        mTypeScanner = typeScanner;
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
    public void setPresenter(ScannerContract.Presenter presenter) {
        mPresenter = presenter;
    }

    @IntDef({ SCANNER_DEVICE, SCANNER_IN_MAIN })
    public @interface ScannerType {
        int SCANNER_DEVICE = 1;
        int SCANNER_IN_MAIN = 2;
    }
}
