package com.john.base;

import android.app.ProgressDialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.ActionBar;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.Toast;

import com.john.flux.actions.ActionsCreator;
import com.john.flux.stores.Store;
import com.john.utils.DialogHelp;
import com.john.utils.PermissionsChecker;

/**
 * Created by oceanzhang on 16/2/17.
 */
public abstract class BaseActivity<S extends Store, C extends ActionsCreator> extends BaseFluxActivity<S, C> implements View.OnClickListener {
    //对话框是否可见
    protected boolean _isVisiable;
    //进度对话框
    private ProgressDialog _waitDialog;
    protected LayoutInflater _infalter;
    protected ActionBar _actionbar;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        onBeforeSetContentLayout();
        if (getLayoutId() != 0) {
            setContentView(getLayoutId());
        }
        _actionbar = getSupportActionBar();
        _infalter = getLayoutInflater();
        if (hasActionBar()) {
            initActionBar(_actionbar);
        }
        init(savedInstanceState);
        initView();
        initData();
    }

    @Override
    protected void onStart() {
        super.onStart();
        _isVisiable = true;
    }

    @Override
    protected void onStop() {
        super.onStop();
        _isVisiable = false;
    }

    /**
     * 设置内容之前设置窗体大小
     */
    protected void onBeforeSetContentLayout() {
        if (fullScreen()) {
            getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                    WindowManager.LayoutParams.FLAG_FULLSCREEN);
        }
    }

    protected void init(Bundle savedInstanceState) {
    }

    public void initView() {
    }

    public void initData() {

    }

    protected int getLayoutId() {
        return 0;
    }

    protected boolean fullScreen() {
        return false;
    }

    protected boolean hasActionBar() {
        return getSupportActionBar() != null;
    }

    protected View inflateView(int resId) {
        return _infalter.inflate(resId, null);
    }

    protected boolean hasBackButton() {
        return false;
    }

    protected String getActionBarTitle() {
        return "";
    }

    protected void initActionBar(android.support.v7.app.ActionBar actionBar) {
        if (actionBar == null)
            return;
        if (hasBackButton()) {
            _actionbar.setDisplayHomeAsUpEnabled(true);
            _actionbar.setHomeButtonEnabled(true);
        } else {
            actionBar.setDisplayOptions(android.support.v7.app.ActionBar.DISPLAY_SHOW_TITLE);
            actionBar.setDisplayUseLogoEnabled(false);
            actionBar.setTitle(getActionBarTitle());
        }
    }

    public void setActionBarTitle(int resId) {
        if (resId != 0) {
            setActionBarTitle(getString(resId));
        }
    }

    public void setActionBarTitle(String title) {
        if (TextUtils.isEmpty(title)) {
            title = getString(R.string.app_name);
        }
        if (hasActionBar() && _actionbar != null) {
            _actionbar.setTitle(title);
        }
    }

    protected int getMenuId() {
        return 0;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        if (getMenuId() != 0) {
            getMenuInflater().inflate(getMenuId(), menu);
        }
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                onBackPressed();
                break;

            default:
                break;
        }
        return super.onOptionsItemSelected(item);
    }


    public ProgressDialog showWaitDialog(int resid) {
        return showWaitDialog(getString(resid));
    }

    public ProgressDialog showWaitDialog(String message) {
        if (_isVisiable) {
            if (_waitDialog == null) {
                _waitDialog = DialogHelp.getWaitDialog(this, message);
            }
            if (_waitDialog != null) {
                _waitDialog.setMessage(message);
                _waitDialog.show();
            }
            return _waitDialog;
        }
        return null;
    }

    public void hideWaitDialog() {
        if (_isVisiable && _waitDialog != null) {
            try {
                _waitDialog.dismiss();
                _waitDialog = null;
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        }
    }

    public void showToast(String text) {
        Toast.makeText(this, text, Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onClick(View v) {

    }

    /**
     * 是否所有权限都授权了
     * @param grantResults
     * @return
     */
    private boolean hasAllPermissionsGranted(@NonNull int[] grantResults) {
        for (int grantResult : grantResults) {
            if (grantResult == PackageManager.PERMISSION_DENIED) {
                return false;
            }
        }
        return true;
    }

    /**
     * 请求一组权限
      * @param requestCode
     * @param permissions
     */
    public void requestPermissions(int requestCode, String... permissions) {
        if (PermissionsChecker.lacksPermissions(this, permissions)) {
            ActivityCompat.requestPermissions(this, permissions, requestCode);
        } else {
            allPermissionsAllow(requestCode);
        }
    }


    /**
     * 动态验权回调方法
     * @param requestCode
     * @param permissions
     * @param grantResults
     */
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode,permissions,grantResults);
        if (hasAllPermissionsGranted(grantResults)) {
            allPermissionsAllow(requestCode);
        } else {
            permissionDeny(requestCode);
        }
    }

    /**
     * 授权成功时需要调用的方法
     *
     * @param requestCode
     */
    protected void allPermissionsAllow(int requestCode) {

    }

    /**
     * 授权拒绝,根据不同的requestCode进行不同的处理
     *
     * @param requestCode
     */
    protected void permissionDeny(int requestCode) {

    }

    /**
     * 通过URL跳转到activity scheme://activity?params1=xxx&params2=xxx
     *
     * @param urlSchema
     */
    public void startActivity(String urlSchema) {
        startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(urlSchema)));
    }

    public void startActivityForResult(String urlSchema, int requestCode) {
        startActivityForResult(new Intent(Intent.ACTION_VIEW, Uri.parse(urlSchema)), requestCode);
    }

    /**
     * 获取查询参数
     * milk://login?p1=xxxx&p2=xx getParams("p1") return 'xxxx'
     *
     * @param key
     * @return
     */
    public String getQueryParameter(String key) {
        Intent intent = getIntent();
        if (intent != null) {
            Uri data = intent.getData();
            if (data != null) {
                return data.getQueryParameter(key);
            }
        }
        return null;
    }
    public String getQueryParameter(Intent intent,String key) {
        if (intent != null) {
            Uri data = intent.getData();
            if (data != null) {
                return data.getQueryParameter(key);
            }
        }
        return null;
    }

    /**
     * milk://login?p1=xx&p2=xx return 'login'
     *
     * @return
     */
    public String getUrlHost() {
        Intent intent = getIntent();
        if (intent != null) {
            Uri data = intent.getData();
            if (data != null) {
                return data.getHost();
            }
        }
        return null;
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }
}
