package example.fussen.searchview;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.AttributeSet;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;

public class SearchEditText extends EditText implements View.OnKeyListener, View.OnFocusChangeListener, TextWatcher {


    /**
     * 是否显示在左边
     */
    private boolean isShowNormal = false;
    /**
     * 是否点击软键盘搜索
     */
    private boolean pressSearch = false;
    /**
     * 软键盘搜索键监听
     */
    private OnSearchClickListener listener;

    private Drawable[] drawables; // 控件的图片资源
    private Drawable drawableLeft, drawableDel; // 搜索图标和删除按钮图标
    private int eventX, eventY; // 记录点击坐标
    private Rect rect; // 控件区域

    public void setOnSearchClickListener(OnSearchClickListener listener) {
        this.listener = listener;
    }

    public interface OnSearchClickListener {
        void onSearchClick(View view);
    }

    public SearchEditText(Context context) {
        this(context, null);
        init();
    }

    public SearchEditText(Context context, AttributeSet attrs) {
        this(context, attrs, android.R.attr.editTextStyle);
        init();
    }

    public SearchEditText(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    private void init() {
        setOnFocusChangeListener(this);
        setOnKeyListener(this);
        addTextChangedListener(this);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        if (isShowNormal) { // 如果是默认样式，直接绘制
            if (length() < 1) {
                drawableDel = null;
            }
            this.setCompoundDrawablesWithIntrinsicBounds(drawableLeft, null, drawableDel, null);
            super.onDraw(canvas);
        } else { // 如果不是默认样式，需要将图标绘制在中间
            if (drawables == null) drawables = getCompoundDrawables();
            if (drawableLeft == null) drawableLeft = drawables[0];
            float textWidth = getPaint().measureText(getHint().toString());
            int drawablePadding = getCompoundDrawablePadding();
            int drawableWidth = drawableLeft.getIntrinsicWidth();
            float bodyWidth = textWidth + drawableWidth + drawablePadding;
            canvas.translate((getWidth() - bodyWidth - getPaddingLeft() - getPaddingRight()) / 2, 0);
            super.onDraw(canvas);
        }
    }

    @Override
    public void onFocusChange(View v, boolean hasFocus) {
        // 被点击时，恢复默认样式
        if (!pressSearch && TextUtils.isEmpty(getText().toString())) {
            isShowNormal = hasFocus;
        }
    }

    @Override
    public boolean onKey(View v, int keyCode, KeyEvent event) {
        pressSearch = (keyCode == KeyEvent.KEYCODE_ENTER);
        if (pressSearch && listener != null) {
            /*隐藏软键盘*/
            InputMethodManager imm = (InputMethodManager) v.getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
            if (imm.isActive()) {
                imm.hideSoftInputFromWindow(v.getApplicationWindowToken(), 0);
            }
            listener.onSearchClick(v);
        }
        return false;
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        // 清空edit内容
        if (drawableDel != null && event.getAction() == MotionEvent.ACTION_UP) {
            eventX = (int) event.getRawX();
            eventY = (int) event.getRawY();
            if (rect == null) rect = new Rect();
            getGlobalVisibleRect(rect);
            rect.left = rect.right - drawableDel.getIntrinsicWidth();
            if (rect.contains(eventX, eventY)) {
                setText("");
            }
        }
        // 删除按钮被按下时改变图标样式
        if (drawableDel != null && event.getAction() == MotionEvent.ACTION_DOWN) {
            eventX = (int) event.getRawX();
            eventY = (int) event.getRawY();
            if (rect == null) rect = new Rect();
            getGlobalVisibleRect(rect);
            rect.left = rect.right - drawableDel.getIntrinsicWidth();
            if (rect.contains(eventX, eventY))
                drawableDel = this.getResources().getDrawable(R.drawable.ic_edit_input_clear);
        } else {
            drawableDel = this.getResources().getDrawable(R.drawable.ic_edit_input_clear);
        }
        return super.onTouchEvent(event);
    }

    @Override
    public void afterTextChanged(Editable arg0) {
        if (this.length() < 1) {
            drawableDel = null;
        } else {
            drawableDel = this.getResources().getDrawable(R.drawable.ic_edit_input_clear);
        }
    }

    @Override
    public void beforeTextChanged(CharSequence arg0, int arg1, int arg2,
                                  int arg3) {
    }

    @Override
    public void onTextChanged(CharSequence arg0, int arg1, int arg2,
                              int arg3) {
    }

}
