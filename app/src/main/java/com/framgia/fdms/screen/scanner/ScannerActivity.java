package com.framgia.fdms.screen.scanner;

import android.content.Context;
import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import com.framgia.fdms.R;
import com.framgia.fdms.databinding.ActivityScannerBinding;
import me.dm7.barcodescanner.zbar.Result;
import me.dm7.barcodescanner.zbar.ZBarScannerView;

import static com.framgia.fdms.screen.scanner.ScannerViewModel.ScannerType.SCANNER_DEVICE;
import static com.framgia.fdms.screen.scanner.ScannerViewModel.ScannerType.SCANNER_IN_MAIN;
import static com.framgia.fdms.utils.Constant.BundleConstant.BUNDLE_CONTENT;
import static com.framgia.fdms.utils.Constant.BundleConstant.BUNDLE_TYPE;

/**
 * Scanner Screen.
 */
public class ScannerActivity extends AppCompatActivity implements ZBarScannerView.ResultHandler {

    private ScannerContract.ViewModel mViewModel;
    private ZBarScannerView mScannerView;
    private int mTypeScanner = SCANNER_IN_MAIN;

    public static Intent newIntent(Context context, int typeScanner) {
        Intent intent = new Intent(context, ScannerActivity.class);
        Bundle bundle = new Bundle();
        bundle.putInt(BUNDLE_TYPE, typeScanner);
        intent.putExtras(bundle);
        return intent;
    }

    private int getTypeScanner() {
        if (getIntent().getExtras() == null) return SCANNER_IN_MAIN;
        return getIntent().getExtras().getInt(BUNDLE_TYPE);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mTypeScanner = getTypeScanner();
        ActivityScannerBinding binding =
                DataBindingUtil.setContentView(this, R.layout.activity_scanner);
        mViewModel = new ScannerViewModel(this, mTypeScanner);

        ScannerContract.Presenter presenter = new ScannerPresenter(mViewModel);
        mViewModel.setPresenter(presenter);

        binding.setViewModel((ScannerViewModel) mViewModel);

        mScannerView = new ZBarScannerView(this);
        binding.frameScanner.addView(mScannerView);
    }

    @Override
    protected void onStart() {
        super.onStart();
        mViewModel.onStart();
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (mScannerView != null) {
            mScannerView.setResultHandler(this);
            mScannerView.startCamera();
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (mScannerView != null) {
            mScannerView.stopCamera();
        }
    }

    @Override
    protected void onStop() {
        mViewModel.onStop();
        super.onStop();
    }

    @Override
    public void handleResult(Result result) {
        mScannerView.stopCamera();
        switch (mTypeScanner) {
            case SCANNER_IN_MAIN:
                // TODO: 6/2/2017 Work when from Main
                break;
            case SCANNER_DEVICE:
                Intent intent = new Intent();
                Bundle bundle = new Bundle();
                bundle.putString(BUNDLE_CONTENT, result.getContents());
                intent.putExtras(bundle);
                setResult(RESULT_OK, intent);
                finish();
                break;
            default:
                break;
        }
    }
}
