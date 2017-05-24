package com.framgia.fdms.screen.requestcreation;

import android.content.Context;
import android.content.DialogInterface;
import android.databinding.BaseObservable;
import android.databinding.Bindable;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.widget.ArrayAdapter;
import android.widget.Toast;
import com.framgia.fdms.BR;
import com.framgia.fdms.R;
import com.framgia.fdms.data.model.Category;
import com.framgia.fdms.data.model.Request;
import com.framgia.fdms.data.model.Status;
import com.framgia.fdms.data.source.api.request.RequestCreatorRequest;
import java.util.List;

/**
 * Exposes the data to be used in the Requestcreation screen.
 */

public class RequestCreationViewModel extends BaseObservable
        implements RequestCreationContract.ViewModel {

    private RequestCreationContract.Presenter mPresenter;
    private AppCompatActivity mActivity;
    private String mRequestTitle;
    private String mRequestDescription;
    private Status mAssignee;
    private Status mRelative;
    private RequestCreatorRequest mRequest;
    private ArrayAdapter<Status> mAdapterAssignee;
    private ArrayAdapter<Status> mAdapterRelative;
    private ArrayAdapter<Category> mAdapterCategory;
    private RequestCreationAdapter mAdapter;
    private Context mContext;
    private String mTitleError;
    private String mDescriptionError;
    private String mRelativeError;

    public RequestCreationViewModel(AppCompatActivity activity) {
        mActivity = activity;
        mContext = activity.getApplicationContext();
        mRequest = new RequestCreatorRequest();
        mAdapterAssignee = new ArrayAdapter<Status>(mActivity, R.layout.item_status_selection);
        mAdapterRelative = new ArrayAdapter<Status>(mActivity, R.layout.item_status_selection);
        mAdapterCategory = new ArrayAdapter<Category>(mActivity,
                R.layout.support_simple_spinner_dropdown_item);
        mAdapter = new RequestCreationAdapter(mContext, this);
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
    public void setPresenter(RequestCreationContract.Presenter presenter) {
        mPresenter = presenter;
    }

    public AppCompatActivity getActivity() {
        return mActivity;
    }

    @Override
    public void onRelativeClick() {
        if (mAdapterRelative == null) {
            return;
        }

        AlertDialog.Builder builder =
                new AlertDialog.Builder(mActivity).setTitle(R.string.title_request_for)
                        .setNegativeButton(R.string.action_cancel, null)
                        .setAdapter(mAdapterRelative, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                setRelative(mAdapterRelative.getItem(which));
                            }
                        });
        builder.show();
    }

    @Override
    public void onAssigneeClick() {
        if (mAdapterAssignee == null) {
            return;
        }

        AlertDialog.Builder builder =
                new AlertDialog.Builder(mActivity).setTitle(R.string.title_assign_to)
                        .setNegativeButton(R.string.action_cancel, null)
                        .setAdapter(mAdapterAssignee, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                setAssignee(mAdapterAssignee.getItem(which));
                            }
                        });
        builder.show();
    }

    @Override
    public void onCreateRequestClick() {
        mRequest.setDeviceRequests(mAdapter.getData());
        mPresenter.registerRequest(mRequest);
    }

    @Override
    public void onGetAssignedSuccess(List<Status> assignees) {
        updateAssignee(assignees);
    }

    private void updateAssignee(List<Status> assignees) {
        if (assignees == null) {
            return;
        }
        mAdapterAssignee.clear();
        mAdapterAssignee.addAll(assignees);
        mAdapterAssignee.notifyDataSetChanged();
    }

    @Override
    public void onGetRelativeSuccess(List<Status> relatives) {
        updateRelative(relatives);
    }

    @Override
    public void onGetCategorySuccess(List<Category> categories) {
        if (categories == null) {
            return;
        }
        mAdapterCategory.clear();
        mAdapterCategory.addAll(categories);
        mAdapterCategory.notifyDataSetChanged();
    }

    @Override
    public void onLoadError(String message) {
        Toast.makeText(mContext, message, Toast.LENGTH_SHORT).show();
    }

    @Override
    public void hideProgressbar() {
        // TODO: later
    }

    @Override
    public void showProgressbar() {
        // TODO: later
    }

    @Override
    public void onAddRequestDetailClick(int position) {
        if (position == (mAdapter.getItemCount() - 1)) {
            mAdapter.addItem();
        }
    }

    @Override
    public void onGetRequestSuccess(Request request) {
        mActivity.finish();
    }

    @Override
    public void onInputTitleError() {
        mTitleError = mContext.getString(R.string.msg_error_user_name);
        notifyPropertyChanged(BR.titleError);
    }

    @Override
    public void onInputDescriptionError() {
        mDescriptionError = mContext.getString(R.string.msg_error_user_name);
        notifyPropertyChanged(BR.descriptionError);
    }

    @Override
    public void onInputRelativeError() {
        mRelativeError = mContext.getString(R.string.msg_error_user_name);
        notifyPropertyChanged(BR.relativeError);
    }

    private void updateRelative(List<Status> relatives) {
        if (relatives == null) {
            return;
        }
        mAdapterRelative.clear();
        mAdapterRelative.addAll(relatives);
        mAdapterRelative.notifyDataSetChanged();
    }

    @Bindable
    public String getRequestTitle() {
        return mRequestTitle;
    }

    public void setRequestTitle(String requestTitle) {
        mRequest.setTitle(requestTitle);
        mRequestTitle = requestTitle;
    }

    @Bindable
    public String getRequestDescription() {
        return mRequestDescription;
    }

    public void setRequestDescription(String requestDescription) {
        mRequest.setDescription(requestDescription);
        mRequestDescription = requestDescription;
    }

    @Bindable
    public Status getAssignee() {
        return mAssignee;
    }

    public void setAssignee(Status assignee) {
        mRequest.setAssigneeId(assignee.getId());
        mAssignee = assignee;
        notifyPropertyChanged(BR.assignee);
    }

    @Bindable
    public Status getRelative() {
        return mRelative;
    }

    public void setRelative(Status relative) {
        mRequest.setForUserId(relative.getId());
        mRelative = relative;
        notifyPropertyChanged(BR.relative);
    }

    public ArrayAdapter<Category> getAdapterCategory() {
        return mAdapterCategory;
    }

    public RequestCreationAdapter getAdapter() {
        return mAdapter;
    }

    @Bindable
    public String getTitleError() {
        return mTitleError;
    }

    @Bindable
    public String getDescriptionError() {
        return mDescriptionError;
    }

    @Bindable
    public String getRelativeError() {
        return mRelativeError;
    }
}
