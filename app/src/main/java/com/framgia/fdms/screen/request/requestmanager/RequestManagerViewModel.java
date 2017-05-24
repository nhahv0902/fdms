package com.framgia.fdms.screen.request.requestmanager;

import android.content.Context;
import android.content.DialogInterface;
import android.databinding.Bindable;
import android.support.v4.app.FragmentActivity;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.PopupMenu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Toast;
import com.framgia.fdms.BR;
import com.framgia.fdms.BaseFragmentContract;
import com.framgia.fdms.BaseFragmentModel;
import com.framgia.fdms.R;
import com.framgia.fdms.data.model.Request;
import com.framgia.fdms.data.model.Status;
import com.framgia.fdms.screen.request.OnMenuClickListenner;
import com.framgia.fdms.screen.request.userrequest.UserRequestAdapter;
import java.util.ArrayList;
import java.util.List;

import static com.framgia.fdms.utils.Constant.OUT_OF_INDEX;

/**
 * Exposes the data to be used in the RequestManager screen.
 */

public class RequestManagerViewModel extends BaseFragmentModel
        implements RequestManagerContract.ViewModel, OnMenuClickListenner {

    private Context mContext;
    private UserRequestAdapter mAdapter;

    private ArrayAdapter<Status> mAdapterStatus;
    private ArrayAdapter<Status> mAdapterRealtive;
    private Status mStatus;
    private FragmentActivity mActivity;
    private Status mRelative;

    public RequestManagerViewModel(FragmentActivity activity) {
        mContext = activity.getApplicationContext();
        mActivity = activity;
        mAdapter = new UserRequestAdapter(mContext, new ArrayList<Request>(), this);
        mAdapterStatus = new ArrayAdapter<>(mContext, R.layout.item_status_selection);
        mAdapterRealtive = new ArrayAdapter<>(mContext, R.layout.item_status_selection);
        setStatus(new Status(OUT_OF_INDEX, mContext.getString(R.string.title_request_status)));
        setRelative(new Status(OUT_OF_INDEX, mContext.getString(R.string.title_request_relative)));
    }

    @Override
    public void onStart() {
        if (mPresenter == null) return;
        mPresenter.onStart();
    }

    @Override
    public void onStop() {
        mPresenter.onStop();
    }

    public void setPresenter(BaseFragmentContract.Presenter presenter) {
        mPresenter = presenter;
    }

    public void setAdapter(UserRequestAdapter adapter) {
        mAdapter = adapter;
    }

    public UserRequestAdapter getAdapter() {
        return mAdapter;
    }

    @Override
    public void onGetRequestSuccess(List<Request> requests) {
        mAdapter.onUpdatePage(requests);
    }

    @Override
    public void onGetStatusSuccess(List<Status> statuses) {
        updateStatus(statuses);
    }

    @Override
    public void onGetRelativeSuccess(List<Status> relatives) {
        updateRelative(relatives);
    }

    @Override
    public void onLoadError(String msg) {
        Toast.makeText(mContext, msg, Toast.LENGTH_SHORT).show();
    }

    @Override
    public void getData() {
        mPresenter.getData(null, null);
    }

    public void onSelectStatusClick() {
        if (mAdapterStatus == null) {
            return;
        }
        AlertDialog.Builder builder = new AlertDialog.Builder(mActivity).setTitle(
                mContext.getString(R.string.title_request_status))
                .setNegativeButton(mContext.getString(R.string.action_cancel), null)
                .setPositiveButton(mContext.getString(R.string.action_clear),
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                setStatus(new Status(OUT_OF_INDEX,
                                        mContext.getString(R.string.title_request_status)));
                                mPresenter.getData(mRelative, mStatus);
                            }
                        })
                .setAdapter(mAdapterStatus, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        setStatus(mAdapterStatus.getItem(which));
                        mAdapter.clear();
                        mPresenter.getData(mRelative, mStatus);
                    }
                });
        builder.show();
    }

    public void onSelectRelativeClick() {
        if (mAdapterRealtive == null) {
            return;
        }
        AlertDialog.Builder builder = new AlertDialog.Builder(mActivity).setTitle(
                mContext.getString(R.string.title_request_relative))
                .setNegativeButton(mContext.getString(R.string.action_cancel), null)
                .setPositiveButton(mContext.getString(R.string.action_clear),
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                setRelative(new Status(OUT_OF_INDEX,
                                        mContext.getString(R.string.title_request_relative)));
                                mPresenter.getData(mRelative, mStatus);
                            }
                        })
                .setAdapter(mAdapterRealtive, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        setRelative(mAdapterRealtive.getItem(which));
                        mAdapter.clear();
                        mPresenter.getData(mRelative, mStatus);
                    }
                });
        builder.show();
    }

    public void updateStatus(List<Status> list) {
        if (list == null) {
            return;
        }
        mAdapterStatus.clear();
        mAdapterStatus.addAll(list);
        mAdapterStatus.notifyDataSetChanged();
    }

    public void updateRelative(List<Status> relatives) {
        if (relatives == null) {
            return;
        }
        mAdapterRealtive.clear();
        mAdapterRealtive.addAll(relatives);
        mAdapterRealtive.notifyDataSetChanged();
    }

    @Bindable
    public Status getStatus() {
        return mStatus;
    }

    public void setStatus(Status status) {
        mStatus = status;
        notifyPropertyChanged(BR.status);
    }

    @Bindable
    public Status getRelative() {
        return mRelative;
    }

    public void setRelative(Status relative) {
        mRelative = relative;
        notifyPropertyChanged(BR.relative);
    }

    public ArrayAdapter<Status> getAdapterRealtive() {
        return mAdapterRealtive;
    }

    public void setAdapterRealtive(ArrayAdapter<Status> adapterRealtive) {
        mAdapterRealtive = adapterRealtive;
    }

    @Override
    public void onMenuClick(View v, UserRequestAdapter.RequestModel request) {
        PopupMenu popupMenu = new PopupMenu(v.getContext(), v);
        popupMenu.getMenuInflater().inflate(R.menu.menu_request, popupMenu.getMenu());
        popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                //// TODO: 22/05/2017 update request status 
                return false;
            }
        });
        popupMenu.show();
    }
}
