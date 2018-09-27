package au.com.infotrak.infotrakmobile.controls;

import android.annotation.TargetApi;
import android.content.Context;
import android.os.Build;
import android.util.AttributeSet;

/**
 * Created by SamuelC on 20/03/2015.
 */
public class CustomCheckBox extends android.widget.CheckBox {
    private OnCheckedChangeListener _listener;

    public CustomCheckBox(final Context context) {
        super(context);
    }

    public CustomCheckBox(final Context context, final AttributeSet attrs) {
        super(context, attrs);
    }

    public CustomCheckBox(final Context context, final AttributeSet attrs, final int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    public CustomCheckBox(final Context context, final AttributeSet attrs, final int defStyleAttr, final int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
    }

    @Override
    public void setOnCheckedChangeListener(final OnCheckedChangeListener listener) {
        _listener = listener;
        super.setOnCheckedChangeListener(listener);
    }

    public void setChecked(final boolean checked, final boolean alsoNotify) {
        if (!alsoNotify) {
            super.setOnCheckedChangeListener(null);
            super.setChecked(checked);
            super.setOnCheckedChangeListener(_listener);
            return;
        }
        super.setChecked(checked);
    }
}
