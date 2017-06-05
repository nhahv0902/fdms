package com.framgia.fdms.data.model;

import android.databinding.BaseObservable;
import android.databinding.Bindable;
import com.framgia.fdms.BR;

/**
 * Created by Nhahv0902 on 6/2/2017.
 * <></>
 */

public class DeviceReturn extends BaseObservable {
    private Device mDevice;
    private boolean mIsChecked;

    public DeviceReturn(Device device) {
        mDevice = device;
        mIsChecked = false;
    }

    @Bindable
    public Device getDevice() {
        return mDevice;
    }

    public void setDevice(Device device) {
        mDevice = device;
        notifyPropertyChanged(BR.device);
    }

    @Bindable
    public boolean isChecked() {
        return mIsChecked;
    }

    public void setChecked(boolean checked) {
        mIsChecked = checked;
        notifyPropertyChanged(BR.checked);
    }
}
