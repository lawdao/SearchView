# SearchView
仿IOS的搜索控件
##闲话中心
天猫双十一的成交额为1207亿，说句实话，我在这之前预测的是最多不超过1200亿，看来我还是失算了，但是还好相差的不是特别的大，相对来说哈，我在说句闲话，如果不解决物流的问题，成交额的增长率不但不增长还会下降，并且现在人们的观念，已经快要完全的改变了，双十一，人们不再是为打折去的，而是真正的狂欢，这才是天猫最可怕的
##今天的任务
我们今天要完成的是ios的搜索框，人家做的控件的确不错，并且我也要用到了，就拿出来说一说，看效果吧
##IOS效果
![](http://ww4.sinaimg.cn/large/65e4f1e6gw1f9ry9sy99ag20ab0i642p.gif)
##Android效果
![](http://ww2.sinaimg.cn/large/65e4f1e6gw1f9rzgcekb3g20960fsdn5.gif)

##实现的功能
1. 搜索字居中，点击后输入框获取焦点，搜索字显示在输入框的左边
2. 点击屏幕的任何地方收起软件盘
3. 输入框加入删除输入内容功能

##实现思路
1. 继承EditText
2. 设置焦点监听，软键盘监听，输入框监听
3. 重写onDraw方法，根据输入框的状态绘制hit文字和搜索图片
4. 处理Touch事件

##代码展示
1. 重写onDraw方法
 

```
	@Override
    protected void onDraw(Canvas canvas) {

        if (isShowNormal) { // 如果是默认样式，直接绘制
            if (length() < 1) {
                drawableDel = null;
            }
            this.setCompoundDrawablesWithIntrinsicBounds(drawableLeft, null, drawableDel, null);
            super.onDraw(canvas);
        } else { // 如果不是默认样式，需要将图标绘制在中间
            drawableLeft = drawables[0];
            float textWidth = getPaint().measureText(getHint().toString());
            int drawablePadding = getCompoundDrawablePadding();
            int drawableWidth = drawableLeft.getIntrinsicWidth();
            float bodyWidth = textWidth + drawableWidth + drawablePadding;
            canvas.translate((getWidth() - bodyWidth - getPaddingLeft() - getPaddingRight()) / 2, 0);
            super.onDraw(canvas);
        }
    }
```


2. 监听软键盘

```
	@Override
    public boolean onKey(View v, int keyCode, KeyEvent event) {
        pressSearch = (keyCode == KeyEvent.KEYCODE_ENTER);
        if (pressSearch && listener != null) {
            /*隐藏软键盘*/
            InputMethodManager imm = (InputMethodManager) v.getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
            if (imm.isActive()) {
                imm.hideSoftInputFromWindow(v.getApplicationWindowToken(), 0);
            }
            if (event.getAction() == KeyEvent.ACTION_UP) {
                pressSearch = false;
                listener.onSearchClick(v);
            }
        }
        return false;
    }
```

3. 处理Touch事件

```
    /**
     * 当手指抬起的位置在clean的图标的区域 我们将此视为进行清除操作
     * getWidth():得到控件的宽度
     * event.getX():抬起时的坐标(改坐标是相对于控件本身而言的)
     * getTotalPaddingRight():clean的图标左边缘至控件右边缘的距离
     * getPaddingRight():clean的图标右边缘至控件右边缘的距离
     * 于是:
     * getWidth() - getTotalPaddingRight()表示: 控件左边到clean的图标左边缘的区域
     * getWidth() - getPaddingRight()表示: 控件左边到clean的图标右边缘的区域 所以这两者之间的区域刚好是clean的图标的区域
     */
    @Override
    public boolean onTouchEvent(MotionEvent event) {
        switch (event.getAction()) {
            case MotionEvent.ACTION_UP:
                boolean isClean = (event.getX() > (getWidth() - getTotalPaddingRight()))
                        && (event.getX() < (getWidth() - getPaddingRight()));
                if (isClean) {
                    setText("");
                }
                break;

            default:
                break;
        }
        return super.onTouchEvent(event);
    }
```

4. 点击屏幕任何地方隐藏软键盘

```
    /**
     * 根据EditText所在坐标和用户点击的坐标相对比，来判断是否隐藏键盘，因为当用户点击EditText时则不能隐藏
     *
     * @param v
     * @param event
     * @return
     */
    private boolean isShouldHideKeyboard(View v, MotionEvent event) {
        if (v != null && (v instanceof EditText)) {
            int[] l = {0, 0};
            v.getLocationInWindow(l);
            int left = l[0],
                    top = l[1],
                    bottom = top + v.getHeight(),
                    right = left + v.getWidth();
            if (event.getX() > left && event.getX() < right
                    && event.getY() > top && event.getY() < bottom) {
                // 点击EditText的事件，忽略它。
                return false;
            } else {
                v.clearFocus();
                return true;
            }
        }
        // 如果焦点不是EditText则忽略，这个发生在视图刚绘制完，第一个焦点不在EditText上，和用户用轨迹球选择其他的焦点
        return false;
    }
```


