package com.john.widget;

import android.content.Context;
import android.content.res.ColorStateList;
import android.content.res.Resources;
import android.content.res.TypedArray;
import android.graphics.Typeface;
import android.os.Parcelable;
import android.text.InputType;
import android.text.Spanned;
import android.text.TextUtils.TruncateAt;
import android.util.AttributeSet;
import android.util.SparseArray;
import android.view.Gravity;
import android.view.View;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.john.base.R;


public class BasicItem extends LinearLayout {
    public static final int TEXT_TYPE_SMALL = 0x01;
    public static final int TEXT_TYPE_YELLOW_COLOR = TEXT_TYPE_SMALL << 1;
    public static final int TEXT_TYPE_GRAY_COLOR = TEXT_TYPE_SMALL << 2;
    public static final int TEXT_TYPE_BLACK_COLOR = TEXT_TYPE_SMALL << 3;
    public static final int TEXT_TYPE_BOLD = TEXT_TYPE_SMALL << 4;
    Spanned titleSpan;
    private Context mContext;
    private LinearLayout itemTitleLay;
    private TextView itemTitle;
    private TextView itemSubtitle;
    private EditText itemInput;
    private TextView itemCount;
    private CheckBox itemCheckBox;
    private ImageView itemLeft1stPic;
    private ImageView itemLeft2ndPic;
    private ImageView itemRight1stPic;
    private ImageView itemRight2ndPic;
    private ImageView itemArrow;
    private String title;
    private String subTitle;
    private String input;
    private String input_hint;
    private int input_type;
    private String count;
    private int checked;
    private boolean clickable;
    private boolean show1stPic;
    private boolean show2ndPic;
    private int title_textType;
    private int subTitle_textType;
    private int count_textType;
    private int input_textType;
    private int arrowImageResID;
    private int right1stImageResID;
    private int right2ndImageResID;

    public BasicItem(Context context) {
        this(context, null);
        mContext = context;

        this.setLayoutParams(new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT));
    }

    public BasicItem(Context context, AttributeSet attrs) {
        super(context, attrs);
        mContext = context;

        TypedArray a = context.obtainStyledAttributes(attrs, R.styleable.BasicItem);

        title = a.getString(R.styleable.BasicItem_dptitle);
        subTitle = a.getString(R.styleable.BasicItem_subTitle);
        input = a.getString(R.styleable.BasicItem_input);
        input_hint = a.getString(R.styleable.BasicItem_input_hint);
        input_type = a.getInt(R.styleable.BasicItem_input_type, InputType.TYPE_CLASS_TEXT);
        count = a.getString(R.styleable.BasicItem_count);
        checked = a.getInt(R.styleable.BasicItem_checked, 0);// 1 true, 2
        // false, 0 gone
        title_textType = a.getInt(R.styleable.BasicItem_title_textType, 0);
        subTitle_textType = a.getInt(R.styleable.BasicItem_subTitle_textType, 0);
        count_textType = a.getInt(R.styleable.BasicItem_count_textType, 0);
        input_textType = a.getInt(R.styleable.BasicItem_input_textType, 0);
        clickable = a.getBoolean(R.styleable.BasicItem_clickable, false);
        arrowImageResID = a.getResourceId(R.styleable.BasicItem_arrowImage, 0);
        right1stImageResID = a.getResourceId(R.styleable.BasicItem_right1stPic, 0);
        show1stPic = a.getBoolean(R.styleable.BasicItem_show1stPic, false);
        right2ndImageResID = a.getResourceId(R.styleable.BasicItem_right2ndPic, 0);
        show2ndPic = a.getBoolean(R.styleable.BasicItem_show2ndPic, false);
        a.recycle();

        if (isInEditMode()) {
            return;
        }
        setupView(context);
    }

    private void setupView(Context context) {
        Resources resource = context.getResources();
        ColorStateList csl = resource.getColorStateList(R.color.text_color_default);

        // title lay
        itemTitleLay = new LinearLayout(context);
        itemTitleLay.setDuplicateParentStateEnabled(true);
        itemTitleLay.setGravity(Gravity.CENTER_VERTICAL);
        // title
        itemTitle = new TextView(context);
        itemTitle.setId(R.id.itemTitle);
        itemTitle.setLayoutParams(new LayoutParams(LayoutParams.WRAP_CONTENT,
                LayoutParams.WRAP_CONTENT));
        itemTitle.setText(title);
        itemTitle.setDuplicateParentStateEnabled(true);
        itemTitle.setTextAppearance(context, android.R.style.TextAppearance_Medium);
        itemTitle.setTextColor(csl);
        itemTitle.setSingleLine(true);
        itemTitle.setEllipsize(TruncateAt.END);
        itemTitle.setPadding(0, 0, dip2px(10), 0);
        itemTitle.setTypeface(Typeface.create(Typeface.DEFAULT, Typeface.NORMAL));
        setTextType(itemTitle, title_textType);
        itemTitleLay.addView(itemTitle);

        LayoutParams paramLeft = new LayoutParams(dip2px(30), dip2px(30));
        paramLeft.setMargins(0, 0, dip2px(10), 0);

        //itemLeftFirstImage
        itemLeft1stPic = new ImageView(context);
        itemLeft1stPic.setId(R.id.itemRight1stPic);
        itemLeft1stPic.setLayoutParams(paramLeft);
        itemLeft1stPic.setDuplicateParentStateEnabled(true);
        itemTitleLay.addView(itemLeft1stPic);

        //itemLeftsencondImage
        itemLeft2ndPic = new ImageView(context);
        itemLeft2ndPic.setId(R.id.itemRight2ndPic);
        itemLeft2ndPic.setLayoutParams(paramLeft);
        itemLeft2ndPic.setDuplicateParentStateEnabled(true);
        itemTitleLay.addView(itemLeft2ndPic);

        // subTitle
        itemSubtitle = new TextView(context);
        itemSubtitle.setId(R.id.itemSubTitle);
        itemSubtitle.setLayoutParams(new LayoutParams(LayoutParams.WRAP_CONTENT,
                LayoutParams.WRAP_CONTENT));
        itemSubtitle.setText(subTitle);
        itemSubtitle.setDuplicateParentStateEnabled(true);
        itemSubtitle.setTextAppearance(context, android.R.style.TextAppearance_Medium);
        itemSubtitle.setTextColor(csl);
        itemSubtitle.setSingleLine(true);
        itemSubtitle.setEllipsize(TruncateAt.END);
        setTextType(itemSubtitle, subTitle_textType);
        itemTitleLay.addView(itemSubtitle);
        addView(itemTitleLay);

        // itemInput
        itemInput = new EditText(context);
        itemInput.setId(R.id.itemInput);
        LayoutParams inputLayoutParams = new LayoutParams(0, LayoutParams.WRAP_CONTENT, 1);
        itemInput.setLayoutParams(inputLayoutParams);
        itemInput.setGravity(Gravity.CENTER_VERTICAL);
        itemInput.setText(input);
        itemInput.setDuplicateParentStateEnabled(true);
        itemInput.setTextAppearance(context, android.R.style.TextAppearance_Medium);
        itemInput.setTextColor(csl);
        itemInput.setSingleLine(true);
        itemInput.setEllipsize(TruncateAt.END);
        itemInput.setHint(input_hint);
        itemInput.setInputType(input_type);
        itemInput.setBackgroundDrawable(null);
        itemInput.setPadding(0, 0, 0, 0);
        setTextType(itemInput, input_textType);
        addView(itemInput);

        // itemCount
        itemCount = new TextView(context);
        itemCount.setId(R.id.itemCount);
        itemCount.setLayoutParams(new LayoutParams(LayoutParams.WRAP_CONTENT,
                LayoutParams.WRAP_CONTENT));
        itemCount.setText(count);
        itemCount.setMaxWidth(dip2px(180));
        itemCount.setDuplicateParentStateEnabled(true);
        itemCount.setTextAppearance(context, android.R.style.TextAppearance_Medium);
        itemCount.setTextColor(resource.getColorStateList(R.color.text_gray_color_selector));
        itemCount.setPadding(0, 0, 0, 0);
        setTextType(itemCount, count_textType);
        addView(itemCount);

        // itemCheckBox
        itemCheckBox = new CheckBox(context);
        itemCheckBox.setId(R.id.itemCheckBox);
        itemCheckBox.setLayoutParams(new LayoutParams(dip2px(26), dip2px(25)));
        itemCheckBox.setChecked(checked == 1 ? true : false);
        itemCheckBox.setPadding(0, 0, 0, 0);
        addView(itemCheckBox);

        LayoutParams paramRight = new LayoutParams(dip2px(30), dip2px(30));
        paramRight.setMargins(dip2px(10), 0, 0, 0);
        //itemRightFirstImage
        itemRight1stPic = new ImageView(context);
        itemRight1stPic.setId(R.id.itemRight1stPic);
        itemRight1stPic.setLayoutParams(paramRight);
        itemRight1stPic.setDuplicateParentStateEnabled(true);
        if (right1stImageResID != 0) {
            itemRight1stPic.setImageResource(right1stImageResID);
        } else {
            itemRight1stPic.setImageResource(0);
        }
        addView(itemRight1stPic);

        //itemRightsencondImage
        itemRight2ndPic = new ImageView(context);
        itemRight2ndPic.setId(R.id.itemRight2ndPic);
        itemRight2ndPic.setLayoutParams(paramRight);
        itemRight2ndPic.setDuplicateParentStateEnabled(true);
        if (right2ndImageResID != 0) {
            itemRight2ndPic.setImageResource(right2ndImageResID);
        } else {
            itemRight2ndPic.setImageResource(0);
        }
        addView(itemRight2ndPic);

        // itemArrow
        itemArrow = new ImageView(context);
        itemArrow.setId(R.id.itemArrow);
        itemArrow.setLayoutParams(new LayoutParams(LayoutParams.WRAP_CONTENT,
                LayoutParams.WRAP_CONTENT));
        itemArrow.setPadding(dip2px(10), 0, 0, 0);
        itemArrow.setDuplicateParentStateEnabled(true);
        if (arrowImageResID != 0) {
            itemArrow.setImageResource(arrowImageResID);
        } else {
            itemArrow.setImageResource(R.drawable.arrow);
        }
        addView(itemArrow);

        build();

        setGravity(Gravity.CENTER_VERTICAL);
        setMinimumHeight(dip2px(45));
    }

    /**
     * set the view's visibility
     */
    public void build() {
        itemTitle.setVisibility(title == null ? View.GONE : View.VISIBLE);
        itemSubtitle.setVisibility(subTitle == null ? View.GONE : View.VISIBLE);
        itemInput.setVisibility(input_hint != null || input != null ? View.VISIBLE : View.GONE);
        itemCount.setVisibility(count != null ? View.VISIBLE : View.GONE);
        itemCheckBox.setVisibility(checked == 0 ? View.GONE : View.VISIBLE);
        itemRight1stPic.setVisibility(isShow1stPic() ? View.VISIBLE : View.GONE);
        itemRight2ndPic.setVisibility(isShow2ndPic() ? View.VISIBLE : View.GONE);
        itemLeft1stPic.setVisibility(View.GONE);
        itemLeft2ndPic.setVisibility(View.GONE);
        itemArrow.setVisibility(isClickable() ? View.VISIBLE : View.GONE);

        if (input_hint != null || input != null) {
            itemTitleLay.setLayoutParams(new LayoutParams(LayoutParams.WRAP_CONTENT,
                    LayoutParams.WRAP_CONTENT, 0));
        } else {
            itemTitleLay.setLayoutParams(new LayoutParams(0, LayoutParams.WRAP_CONTENT, 1));
        }

        if (input_hint != null || subTitle != null)
            title_textType |= BasicItem.TEXT_TYPE_GRAY_COLOR;
        setTextType(itemTitle, title_textType);

        setClickable(clickable);
    }

    private int dip2px(float dipValue) {
        float scale = mContext.getResources().getDisplayMetrics().density;
        return (int) (dipValue * scale + 0.5f);
    }

    private void setTextType(TextView tv, int textType) {
        Resources resource = mContext.getResources();
        if (textType == 0) {
            return;
        }

        if ((textType & 0x01) == TEXT_TYPE_SMALL)
            tv.setTextAppearance(mContext, R.style.content_page_small_text);

        if ((textType & 0x02) == TEXT_TYPE_YELLOW_COLOR) {
            ColorStateList csl = resource.getColorStateList(R.color.text_yellow_color_selector);
            tv.setTextColor(csl);
        }

        if ((textType & 0x04) == TEXT_TYPE_GRAY_COLOR) {
            ColorStateList csl = resource.getColorStateList(R.color.text_gray_color_selector);
            tv.setTextColor(csl);
        }

        if ((textType & 0x08) == TEXT_TYPE_BLACK_COLOR) {
            ColorStateList csl = resource.getColorStateList(R.color.black);
            tv.setTextColor(csl);
        }

        if ((textType & 0x10) == TEXT_TYPE_BOLD)
            tv.getPaint().setFakeBoldText(true);
        else
            tv.getPaint().setFakeBoldText(false);
    }

    @Override
    protected void dispatchSaveInstanceState(SparseArray<Parcelable> container) {
        int inputId = R.id.itemInput - getId();
        EditText inputChild = (EditText) findViewById(R.id.itemInput);
        if (inputChild == null) {
            super.dispatchSaveInstanceState(container);
            return;
        } else {
            Parcelable state = inputChild.onSaveInstanceState();
            if (state != null) {
                container.put(inputId, state);
            }
        }

        int checkboxId = R.id.itemCheckBox ^ getId();
        CheckBox checkboxChild = (CheckBox) findViewById(R.id.itemCheckBox);
        Parcelable state = checkboxChild.onSaveInstanceState();
        if (state != null) {
            container.put(checkboxId, state);
        }
    }

    @Override
    protected void dispatchRestoreInstanceState(SparseArray<Parcelable> container) {
        int inputId = R.id.itemInput - getId();
        EditText inputChild = (EditText) findViewById(R.id.itemInput);
        if (inputChild == null) {
            super.dispatchRestoreInstanceState(container);
            return;
        } else {
            Parcelable state = container.get(inputId);
            if (state != null) {
                inputChild.onRestoreInstanceState(state);
            }
        }

        int checkboxId = R.id.itemCheckBox ^ getId();
        CheckBox checkboxChild = (CheckBox) findViewById(R.id.itemCheckBox);
        Parcelable state = container.get(checkboxId);
        if (state != null) {
            checkboxChild.onRestoreInstanceState(state);
        }
    }

    @Override
    public boolean isClickable() {
        return clickable;
    }

    @Override
    public void setClickable(boolean clickable) {
        super.setClickable(clickable);
        this.clickable = clickable;
    }

    public boolean isShow1stPic() {
        return show1stPic;
    }

    public boolean isShow2ndPic() {
        return show2ndPic;
    }

    public String getInputHint() {
        return input_hint;
    }

    public void setHint(String text) {
        this.input_hint = text;
        itemInput.setHint(text);
    }

    public String getInputText() {
        return input;
    }

    public void setInputText(String inputText) {
        this.input = inputText;
        itemInput.setText(inputText);
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(Spanned spanText) {
        this.titleSpan = spanText;
        itemTitle.setText(spanText);
    }

    public void setTitle(String mTitle) {
        this.title = mTitle;
        itemTitle.setText(mTitle);
    }

    public String getSubTitle() {
        return subTitle;
    }

    public void setSubTitle(String mSubtitle) {
        this.subTitle = mSubtitle;
        itemSubtitle.setText(mSubtitle);
    }

    public String getCount() {
        return count;
    }

    public void setCount(String countText) {
        this.count = countText;
        itemCount.setText(countText);
    }

    public int getInputType() {
        return input_type;
    }

    public void setInputType(int inputType) {
        this.input_type = inputType;
        itemInput.setInputType(inputType);
    }

    public int getTitleTextType() {
        return title_textType;
    }

    public void setTitleTextType(int textType) {
        this.title_textType = textType;
        setTextType(itemTitle, textType);
    }

    public int getSubTitleTextType() {
        return subTitle_textType;
    }

    public void setSubTitleTextType(int textType) {
        this.subTitle_textType = textType;
        setTextType(itemSubtitle, textType);
    }

    public int getCountTextType() {
        return count_textType;
    }

    public void setCountTextType(int textType) {
        this.count_textType = textType;
        setTextType(itemCount, textType);
    }

    public int getInputTextType() {
        return input_textType;
    }

    public void setInputTextType(int textType) {
        this.input_textType = textType;
        setTextType(itemInput, textType);
    }


    public TextView itemTitle() {
        return itemTitle;
    }

    public LinearLayout getItemTitleLay() {
        return itemTitleLay;
    }

    public TextView getItemTitle() {
        return itemTitle;
    }

    public TextView getItemSubtitle() {
        return itemSubtitle;
    }

    public EditText getItemInput() {
        return itemInput;
    }

    public TextView getItemCount() {
        return itemCount;
    }

    public CheckBox getItemCheckBox() {
        return itemCheckBox;
    }

    public ImageView getItemArrow() {
        return itemArrow;
    }

    public ImageView getItemLeft1stPic() {
        return itemLeft1stPic;
    }

    public ImageView getItemLeft2ndPic() {
        return itemLeft2ndPic;
    }

    public ImageView getItemRight1stPic() {
        return itemRight1stPic;
    }

    public ImageView getItemRight2ndPic() {
        return itemRight2ndPic;
    }

    public void setArrowImage(int resId) {
        itemArrow.setImageResource(resId);
    }

}
