package com.framgia.fdms.screen.returndevice;

import android.app.Activity;
import android.content.Intent;
import android.databinding.ObservableArrayList;
import android.databinding.ObservableField;
import android.databinding.ObservableList;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.Toast;
import com.framgia.fdms.data.model.Device;
import com.framgia.fdms.data.model.Status;
import com.framgia.fdms.screen.scanner.ScannerActivity;
import com.framgia.fdms.screen.selection.StatusSelectionActivity;
import com.framgia.fdms.screen.selection.StatusSelectionType;
import java.util.ArrayList;
import java.util.List;

import static com.framgia.fdms.utils.Constant.BundleConstant.BUNDLE_STATUE;
import static com.framgia.fdms.utils.Constant.RequestConstant.REQUEST_SCANNER;
import static com.framgia.fdms.utils.Constant.RequestConstant.REQUEST_SELECTION;

/**
 * Exposes the data to be used in the ReturnDevice screen.
 */

public class ReturnDeviceViewModel implements ReturnDeviceContract.ViewModel {

    private AppCompatActivity mActivity;
    private ReturnDeviceContract.Presenter mPresenter;
    private ObservableList<Device> mDevices = new ObservableArrayList<>();
    private ObservableField<DeviceReturnAdapter> mAdapter = new ObservableField<>();

    private List<Status> mAssigners = new ArrayList<>();
    private ObservableField<Status> mNameUserReturn = new ObservableField<>();

    public ReturnDeviceViewModel(AppCompatActivity activity) {
        mActivity = activity;
        mAdapter.set(new DeviceReturnAdapter(this, mDevices));
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
    public void setPresenter(ReturnDeviceContract.Presenter presenter) {
        mPresenter = presenter;
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode != REQUEST_SELECTION
                || data == null
                || data.getExtras() == null
                || resultCode != Activity.RESULT_OK) {
            return;
        }
        Bundle bundle = data.getExtras();
        switch (requestCode) {
            case REQUEST_SELECTION:
                Status status = bundle.getParcelable(BUNDLE_STATUE);

                if (status != null) {
                    mNameUserReturn.set(status);
                    getAllDeviceBorrowOfUser(status);
                }
                break;
            case REQUEST_SCANNER:
                // TODO: 6/2/2017 handle when qrcode
                break;
            default:
                break;
        }
    }

    @Override
    public void onCheckedChanged(boolean checked, Device device, int position) {
        // TODO: 5/22/2017 work when checkbox item of device return
    }

    @Override
    public void onSelectedUserReturn() {
        mActivity.startActivityForResult(
                StatusSelectionActivity.getInstance(mActivity.getApplicationContext(), null,
                        mAssigners, StatusSelectionType.STATUS), REQUEST_SELECTION);
    }

    @Override
    public void onStartScannerDevice() {
        mActivity.startActivityForResult(
                ScannerActivity.newIntent(mActivity.getApplicationContext()), REQUEST_SCANNER);
    }

    @Override
    public void onReturnDevice() {
        // TODO: 5/23/2017 work save return device
    }

    @Override
    public void showProgressbar() {
        // TODO: later
    }

    @Override
    public void hideProgressbar() {
        // TODO: later
    }

    @Override
    public void onLoadError(String message) {
        Toast.makeText(mActivity, message, Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onGetAssignedSuccess(List<Status> assignees) {
        if (assignees == null) {
            return;
        }
        mAssigners.clear();
        mAssigners.addAll(assignees);
    }

    private void getAllDeviceBorrowOfUser(Status assigner) {
        mPresenter.getDevicesOfBorrower();
    }

    @Override
    public void onError(String message) {
        Toast.makeText(mActivity, message, Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onDeviceLoaded(List<Device> devices) {
        mDevices.clear();
        mDevices.addAll(devices);
        mAdapter.get().update(mDevices);
    }

    public AppCompatActivity getActivity() {
        return mActivity;
    }

    public ObservableField<DeviceReturnAdapter> getAdapter() {
        return mAdapter;
    }

    public ObservableField<Status> getNameUserReturn() {
        return mNameUserReturn;
    }

    public ObservableList<Device> getDevices() {
        return mDevices;
    }
}
