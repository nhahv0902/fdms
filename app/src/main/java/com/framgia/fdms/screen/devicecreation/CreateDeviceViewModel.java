package com.framgia.fdms.screen.devicecreation;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.databinding.BaseObservable;
import android.databinding.Bindable;
import android.net.Uri;
import android.provider.MediaStore;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.widget.ArrayAdapter;
import android.widget.Toast;
import com.framgia.fdms.BR;
import com.framgia.fdms.R;
import com.framgia.fdms.data.model.Category;
import com.framgia.fdms.data.model.Status;
import com.framgia.fdms.data.source.api.request.RegisterDeviceRequest;
import com.framgia.fdms.screen.main.MainActivity;
import java.util.List;

import static android.app.Activity.RESULT_OK;
import static com.framgia.fdms.utils.Constant.PICK_IMAGE_REQUEST;

/**
 * Exposes the data to be used in the Createdevice screen.
 */

public class CreateDeviceViewModel extends BaseObservable
        implements CreateDeviceContract.ViewModel {

    private Context mContext;
    private AppCompatActivity mActivity;
    private CreateDeviceContract.Presenter mPresenter;
    private String mDeviceCodeError;
    private String mNameDeviceError;
    private String mSerialNumberError;
    private String mModelNumberError;
    private RegisterDeviceRequest mRequest;
    private ArrayAdapter<Category> mAdapterCategory;
    private ArrayAdapter<Status> mAdapterStatus;
    private Category mCategory;
    private Status mStatus;

    public CreateDeviceViewModel(CreateDeviceActivity activity) {
        mContext = activity.getApplicationContext();
        mActivity = activity;
        mRequest = new RegisterDeviceRequest();

        mAdapterCategory = new ArrayAdapter<Category>(mContext, R.layout.item_status_selection);
        mAdapterStatus = new ArrayAdapter<Status>(mContext, R.layout.item_status_selection);
    }

    @Override
    public void onCreateDeviceClick() {
        mPresenter.registerDevice(mRequest);
    }

    public void onChooseCategory() {
        if (mAdapterCategory == null) {
            return;
        }

        AlertDialog.Builder builder = new AlertDialog.Builder(mActivity);

        builder.setTitle(R.string.title_category);

        builder.setNegativeButton(R.string.action_cancel, null);

        builder.setAdapter(mAdapterCategory, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                setCategory(mAdapterCategory.getItem(which));
            }
        });
        builder.show();
    }

    public void onChooseStatus() {
        if (mAdapterStatus == null) {
            return;
        }

        AlertDialog.Builder builder = new AlertDialog.Builder(mActivity);

        builder.setTitle(R.string.title_status_device);

        builder.setNegativeButton(R.string.action_cancel, null);

        builder.setAdapter(mAdapterStatus, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                setStatus(mAdapterStatus.getItem(which));
            }
        });
        builder.show();
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
    public void setPresenter(CreateDeviceContract.Presenter presenter) {
        mPresenter = presenter;
    }

    @Override
    public void onDeviceCategoryLoaded(List<Category> categories) {
        updateCategory(categories);
    }

    public void updateCategory(List<Category> list) {
        if (list == null) {
            return;
        }
        mAdapterCategory.clear();
        mAdapterCategory.addAll(list);
        mAdapterCategory.notifyDataSetChanged();
    }

    public void updateStatus(List<Status> list) {
        if (list == null) {
            return;
        }
        mAdapterStatus.clear();
        mAdapterStatus.addAll(list);
        mAdapterStatus.notifyDataSetChanged();
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
    public void onRegisterError() {
        Toast.makeText(mContext, R.string.msg_create_device_error, Toast.LENGTH_LONG).show();
    }

    @Override
    public void onRegisterSuccess() {
        mContext.startActivity(MainActivity.getInstance(mContext));
    }

    @Override
    public void onInputProductionNameError() {
        mNameDeviceError = mContext.getString(R.string.msg_error_user_name);
        notifyPropertyChanged(BR.nameDeviceError);
    }

    @Override
    public void onInputSerialNumberError() {
        mSerialNumberError = mContext.getString(R.string.msg_error_user_name);
        notifyPropertyChanged(BR.serialNumberError);
    }

    @Override
    public void onInputModellNumberError() {
        mModelNumberError = mContext.getString(R.string.msg_error_user_name);
        notifyPropertyChanged(BR.modelNumberError);
    }

    @Override
    public void onInputDeviceCodeError() {
        mDeviceCodeError = mContext.getString(R.string.msg_error_user_name);
        notifyPropertyChanged(BR.deviceCodeError);
    }

    @Override
    public void onInputCategoryError() {
        // TODO: later
    }

    @Override
    public void onInputStatusError() {
        // TODO: later
    }

    @Override
    public void onAddImageClick() {
        pickImage();
    }

    public void pickImage() {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        mActivity.startActivityForResult(Intent.createChooser(intent,
                mContext.getString(R.string.title_select_file_upload)),
                PICK_IMAGE_REQUEST);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == PICK_IMAGE_REQUEST
                && resultCode == RESULT_OK
                && data != null
                && data.getData() != null) {
            Uri uri = data.getData();
            setImageUrl(getRealPathFromURI(uri));
        }
    }

    public String getRealPathFromURI(Uri contentUri) {
        String path = null;
        String[] proj = { MediaStore.MediaColumns.DATA };
        Cursor cursor = mContext.getContentResolver().query(contentUri, proj, null, null, null);
        if (cursor.moveToFirst()) {
            int colum_index = cursor.getColumnIndexOrThrow(MediaStore.MediaColumns.DATA);
            path = cursor.getString(colum_index);
        }
        cursor.close();
        return path;
    }

    @Override
    public void onDeviceStatusLoaded(List<Status> statuses) {
        updateStatus(statuses);
    }

    @Bindable
    public String getDeviceCodeError() {
        return mDeviceCodeError;
    }

    @Bindable
    public String getNameDeviceError() {
        return mNameDeviceError;
    }

    @Bindable
    public String getSerialNumberError() {
        return mSerialNumberError;
    }

    @Bindable
    public String getModelNumberError() {
        return mModelNumberError;
    }

    @Bindable
    public Category getCategory() {
        return mCategory;
    }

    public void setCategory(Category category) {
        mRequest.setDeviceCategoryId(category.getId());
        mCategory = category;
        notifyPropertyChanged(BR.category);
    }

    @Bindable
    public Status getStatus() {
        return mStatus;
    }

    public void setStatus(Status status) {
        mRequest.setDeviceStatusId(status.getId());
        mStatus = status;
        notifyPropertyChanged(BR.status);
    }

    @Bindable
    public String getModelNumber() {
        return mRequest.getModellNumber();
    }

    public void setModelNumber(String modelNumber) {
        mRequest.setModellNumber(modelNumber);
    }

    @Bindable
    public String getSerialNumber() {
        return mRequest.getSerialNumber();
    }

    public void setSerialNumber(String serialNumber) {
        mRequest.setSerialNumber(serialNumber);
    }

    @Bindable
    public String getNameDevice() {
        return mRequest.getProductionName();
    }

    public void setNameDevice(String nameDevice) {
        mRequest.setProductionName(nameDevice);
    }

    @Bindable
    public String getDeviceCode() {
        return mRequest.getDeviceCode();
    }

    public void setDeviceCode(String deviceCode) {
        mRequest.setDeviceCode(deviceCode);
    }

    public AppCompatActivity getActivity() {
        return mActivity;
    }

    @Bindable
    public String getImageUrl() {
        return mRequest != null ? mRequest.getFilePath() : null;
    }

    public void setImageUrl(String imageUrl) {
        mRequest.setFilePath(imageUrl);
        notifyPropertyChanged(BR.imageUrl);
    }
}
