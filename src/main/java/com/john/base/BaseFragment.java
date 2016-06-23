package com.john.base;

import android.app.ProgressDialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.LayoutRes;
import android.support.annotation.NonNull;
import android.view.InflateException;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.john.flux.actions.ActionsCreator;
import com.john.flux.stores.Store;
import com.john.utils.DialogHelp;
import com.john.utils.PermissionsChecker;

import butterknife.ButterKnife;

/**
 * Created by oceanzhang on 16/2/17.
 */
public abstract class BaseFragment<S extends Store,C extends ActionsCreator> extends BaseFluxFragment<S,C> implements View.OnClickListener{
    protected LayoutInflater _inflater;
    private ProgressDialog _waitDialog;
    protected View view;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        this._inflater = inflater;
        if (view != null) {
            ViewGroup parent = (ViewGroup) view.getParent();
            if (parent != null)
                parent.removeView(view);
        }
        try {
            view = inflater.inflate(getLayoutId(), container, false);
        } catch (InflateException ignored) {
        }
        return view;
    }
    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        ButterKnife.bind(this, view);
        initView(view);
        initData();
    }
    public void initView(View view){}
    public void initData(){

    }
    protected @LayoutRes int getLayoutId() {
        return 0;
    }

    protected View inflateView(@LayoutRes int resId) {
        return this._inflater.inflate(resId, null);
    }

    public boolean onBackPressed() {
        return false;
    }

    public ProgressDialog showWaitDialog(int resid) {
        return showWaitDialog(getString(resid));
    }

    public ProgressDialog showWaitDialog(String message) {
        if (_waitDialog == null) {
            _waitDialog = DialogHelp.getWaitDialog(getActivity(), message);
        }
        if (_waitDialog != null) {
            _waitDialog.setMessage(message);
            _waitDialog.show();
        }
        return _waitDialog;
    }

    public void hideWaitDialog() {
        if (_waitDialog != null) {
            try {
                _waitDialog.dismiss();
                _waitDialog = null;
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        }
    }
    public void showToast(String text) {
        Toast.makeText(getActivity(), text, Toast.LENGTH_SHORT).show();
    }
    @Override
    public void onClick(View v) {

    }

    // 含有全部的权限
    private boolean hasAllPermissionsGranted(@NonNull int[] grantResults) {
        for (int grantResult : grantResults) {
            if (grantResult == PackageManager.PERMISSION_DENIED) {
                return false;
            }
        }
        return true;
    }
    public void requestPermissions(int requestCode,String... permissions) {
        if (PermissionsChecker.lacksPermissions(getActivity(), permissions)) {
            requestPermissions(permissions,requestCode);
        }else{
            allPermissionsAllow(requestCode);
        }
    }
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (hasAllPermissionsGranted(grantResults)) {
            allPermissionsAllow(requestCode);
        } else {
            permissionDeny(requestCode);
        }
    }
    protected void allPermissionsAllow(int requestCode){

    }
    protected void permissionDeny(int requestCode){

    }

    /**
     * 通过URL跳转到activity scheme://activity?params1=xxx&params2=xxx
     * @param urlSchema
     */
    public void startActivity(String urlSchema) {
        startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(urlSchema)));
    }

    public void startActivityForResult(String urlSchema, int requestCode) {
        startActivityForResult(new Intent(Intent.ACTION_VIEW, Uri.parse(urlSchema)), requestCode);
    }

    /**
     * milk://login?p1=xxxx&p2=xx getParams("p1") return 'xxxx'
     * @param key
     * @return
     */
    public String getQueryParameter(String key){
        Intent intent = getActivity().getIntent();
        if(intent != null){
            Uri data = intent.getData();
            if(data != null){
                return data.getQueryParameter(key);
            }
        }
        return null;
    }

    /**
     * milk://login?p1=xx&p2=xx return 'login'
     * @return
     */
    public String getUrlHost(){
        Intent intent = getActivity().getIntent();
        if(intent != null){
            Uri data = intent.getData();
            if(data != null){
                return data.getHost();
            }
        }
        return null;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }
}
