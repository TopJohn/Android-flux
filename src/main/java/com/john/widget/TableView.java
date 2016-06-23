package com.john.widget;

import android.content.Context;
import android.content.res.TypedArray;
import android.database.DataSetObserver;
import android.graphics.Canvas;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.os.Handler;
import android.os.Message;
import android.util.AttributeSet;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Adapter;
import android.widget.AdapterView;
import android.widget.LinearLayout;

import com.john.base.R;


public class TableView extends LinearLayout implements OnClickListener {

    private final DataSetObserver observer = new DataSetObserver() {
        @Override
        public void onChanged() {
            handler.removeMessages(1);
            handler.sendEmptyMessageDelayed(1, 100);
        }

        @Override
        public void onInvalidated() {
            onChanged();
        }
    };
    Adapter adapter;
    Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            if (msg.what != 1)
                return;
            if (adapter == null || adapter.isEmpty()) {
                removeAllViews();
                return;
            }
            removeAllViews();
            try {
                for (int i = 0; i < adapter.getCount(); i++) {
                    View view = adapter.getView(i, null, TableView.this);
                    addView(view);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    };
    private OnItemClickListener mClickListener;
    private Drawable divider = getResources().getDrawable(R.drawable.gray_horizontal_line);
    private Drawable dividerOfGroupEnd;
    private Drawable dividerOfGroupHeader;
    private int dividerPadding;

    public TableView(Context context) {
        this(context, null);
    }

    public TableView(Context context, AttributeSet attrs) {
        super(context, attrs);
        TypedArray ta = context.obtainStyledAttributes(attrs, R.styleable.TableView);
        dividerPadding = ta.getDimensionPixelSize(R.styleable.TableView_divider_padding,
                getResources().getDimensionPixelOffset(R.dimen.table_item_padding));
        ta.recycle();
        setOrientation(VERTICAL);
    }

    public Drawable getDivider() {
        return divider;
    }

    public void setDivider(Drawable divider) {
        if (divider == this.divider) {
            return;
        }
        this.divider = divider;
        requestLayout();
    }

    public void setDividerOfGroupEnd(Drawable divider) {
        this.dividerOfGroupEnd = divider;
        requestLayout();
    }

    public void setDividerOfGroupEnd(int resid) {
        if (resid > 0) {
            this.dividerOfGroupEnd = getResources().getDrawable(resid);
            requestLayout();
        }
    }

    public void setDividerOfGroupHeader(Drawable divider) {
        this.dividerOfGroupHeader = divider;
        requestLayout();
    }

    public void setDividerOfGroupHeader(int resid) {
        if (resid > 0) {
            this.dividerOfGroupHeader = getResources().getDrawable(resid);
            requestLayout();
        }
    }

    @Override
    public void childDrawableStateChanged(View child) {
        super.childDrawableStateChanged(child);
        if (divider != null) {
            invalidate(child.getLeft(), child.getTop(), child.getRight(), child.getBottom());
        }
    }

    @Override
    protected void dispatchDraw(Canvas canvas) {
        super.dispatchDraw(canvas);
        if (divider != null) {
            for (int i = 0; i < getChildCount(); i++) {
                View child = getChildAt(i);
                if (child != null && child.getVisibility() == View.VISIBLE
                        && !(child instanceof TableHeader) && child.getHeight() > 0) {
                    if (isGroupStart(i)) {
                        drawDividerOfGroupheader(canvas, child);
                    }
                    if (isGroupEnd(i)) {
                        drawDividerOfGroupEnd(canvas, child);
                    } else {
                        drawDivider(canvas, child);
                    }
                }
            }
        }
    }

    private boolean isGroupEnd(int pos) {
        for (int i = pos + 1; i < getChildCount(); i++) {
            View v = getChildAt(i);
            if (v != null && v.getVisibility() == VISIBLE) {
                return v instanceof TableHeader;
            }
        }
        return true;
    }

    private boolean isGroupStart(int pos) {
        for (int i = pos - 1; i >= 0; i--) {
            View v = getChildAt(i);
            if (v != null && v.getVisibility() == VISIBLE) {
                return v instanceof TableHeader;
            }
        }
        return true;
    }

    private void drawDivider(Canvas canvas, View view) {
        if (divider != null) {
            int[] state = view.getDrawableState();
            divider.setState(state);
            int height = divider.getCurrent().getIntrinsicHeight();
            if (height <= 0) {
                return;
            }
            final Rect bounds = new Rect();
            bounds.left = getPaddingLeft() + dividerPadding;
            bounds.top = view.getBottom() - height;
            bounds.right = getRight() - getLeft() - getPaddingRight();
            bounds.bottom = view.getBottom();
            divider.setBounds(bounds);
            divider.draw(canvas);
        }
    }

    private void drawDividerOfGroupEnd(Canvas canvas, View view) {
        Drawable divider = this.dividerOfGroupEnd == null ? this.divider : this.dividerOfGroupEnd;
        if (divider != null) {
            int[] state = view.getDrawableState();
            divider.setState(state);
            int height = divider.getCurrent().getIntrinsicHeight();
            if (height <= 0) {
                return;
            }
            final Rect bounds = new Rect();
            bounds.left = getPaddingLeft();
            bounds.top = view.getBottom() - height;
            bounds.right = getRight() - getLeft() - getPaddingRight();
            bounds.bottom = view.getBottom();
            divider.setBounds(bounds);
            divider.draw(canvas);
        }
    }

    private void drawDividerOfGroupheader(Canvas canvas, View view) {
        Drawable divider = this.dividerOfGroupHeader == null ? this.divider
                : this.dividerOfGroupHeader;
        if (divider != null) {
            int[] state = view.getDrawableState();
            divider.setState(state);
            int height = divider.getCurrent().getIntrinsicHeight();
            if (height <= 0) {
                return;
            }
            final Rect bounds = new Rect();
            bounds.left = getPaddingLeft();
            bounds.top = view.getTop() - height;
            bounds.right = getRight() - getLeft() - getPaddingRight();
            bounds.bottom = view.getTop();
            divider.setBounds(bounds);
            divider.draw(canvas);
        }
    }

    public void setOnItemClickListener(OnItemClickListener listener) {
        mClickListener = listener;
    }

    public Adapter getAdapter() {
        return adapter;
    }

    public void setAdapter(Adapter adapter) {
        if (this.adapter != null)
            this.adapter.unregisterDataSetObserver(observer);
        this.adapter = adapter;
        if (this.adapter != null)
            this.adapter.registerDataSetObserver(observer);
        removeAllViews();
        observer.onChanged();
    }

    @Override
    public void onClick(View v) {
        if (mClickListener != null) {
            int position = -1;
            long id = -1;
            for (int i = 0; i < getChildCount(); i++) {
                View view = getChildAt(i);
                if (v == view) {
                    position = i;
                    break;
                }
            }
            if (position < 0)
                return;
            if (adapter != null)
                id = adapter.getItemId(position);
            if (id == -1) {
                id = v.getId();
            }
            mClickListener.onItemClick(this, v, position, id);
        }
    }

    @Override
    protected void onLayout(boolean changed, int l, int t, int r, int b) {
        super.onLayout(changed, l, t, r, b);
        for (int i = 0; i < getChildCount(); i++) {
            View child = getChildAt(i);
            if (child != null && child.getVisibility() == View.VISIBLE
                    && !(child instanceof AdapterView)) {
                if (mClickListener != null) {
                    boolean isClickable = child.isClickable();
                    child.setOnClickListener(this);
                    if (!isClickable)
                        child.setClickable(false);
                }
            }
        }
    }

    public interface OnItemClickListener {
        void onItemClick(TableView parent, View view, int position, long id);
    }

}
