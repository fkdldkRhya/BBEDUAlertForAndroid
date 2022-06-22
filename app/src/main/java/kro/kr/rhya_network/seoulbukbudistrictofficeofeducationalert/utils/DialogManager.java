package kro.kr.rhya_network.seoulbukbudistrictofficeofeducationalert.utils;

import android.app.Activity;
import android.app.Dialog;
import android.graphics.Color;
import android.graphics.Insets;
import android.graphics.Point;
import android.graphics.Rect;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.util.Size;
import android.view.Window;
import android.view.WindowInsets;
import android.view.WindowManager;
import android.view.WindowMetrics;

import kro.kr.rhya_network.seoulbukbudistrictofficeofeducationalert.R;

public class DialogManager {
    public final Dialog dialog;
    private final Activity activity;

    public DialogManager(Activity activity) {
        this.activity = activity;

        dialog = new Dialog(activity);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
    }

    public void showDialog() {
        if (!dialog.isShowing()) {
            dialog.show();
        }
    }
    public void dismissDialog() {
        if (dialog.isShowing()) {
            dialog.dismiss();
        }
    }

    public void setBackground(Drawable background) {
        if (background == null)
            dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        else
            dialog.getWindow().setBackgroundDrawable(background);
    }

    public void setCancelable(boolean isCancelable) {
        dialog.setCancelable(isCancelable);
    }

    public void setContentView(int layout) {
        dialog.setContentView(layout);
    }

    public void setGravity(int gravity) {
        dialog.getWindow().setGravity(gravity);
    }

    public void setDialogSizeWithFullSize() {
        WindowManager.LayoutParams params = dialog.getWindow().getAttributes();
        params.width = WindowManager.LayoutParams.MATCH_PARENT;
        params.height = WindowManager.LayoutParams.MATCH_PARENT;

        dialog.getWindow().setAttributes(params);
    }

    public void setDialogSizeWithCustomSize(double dSize) {
        WindowManager.LayoutParams params = dialog.getWindow().getAttributes();
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.R) {
            final WindowMetrics metrics = activity.getWindowManager().getCurrentWindowMetrics();
            // Gets all excluding insets
            final WindowInsets windowInsets = metrics.getWindowInsets();
            Insets insets = windowInsets.getInsetsIgnoringVisibility(WindowInsets.Type.navigationBars()
                    | WindowInsets.Type.displayCutout());
            int insetsWidth = insets.right + insets.left;
            int insetsHeight = insets.top + insets.bottom;

            // Legacy size that Display#getSize reports
            final Rect bounds = metrics.getBounds();
            final Size legacySize = new Size(bounds.width() - insetsWidth,
                    bounds.height() - insetsHeight);
            params.width = (int) Math.round((legacySize.getWidth() * dSize));
        }else {
            Point size = new Point();
            activity.getWindowManager().getDefaultDisplay().getRealSize(size);
            params.width = (int) Math.round((size.x * dSize));
        }

        params.windowAnimations = R.style.AnimationPopupStyle;

        dialog.getWindow().setAttributes(params);
    }
}
