package userinterface.responsemodel;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.drawable.Drawable;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.Log;
import android.util.TypedValue;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.target.BitmapImageViewTarget;
import com.bumptech.glide.request.target.SimpleTarget;
import com.bumptech.glide.request.transition.Transition;
import com.example.appiapi.R;
import com.squareup.picasso.Picasso;
import com.squareup.picasso.Target;

import java.io.IOException;
import java.util.concurrent.ExecutionException;

public class UserImage {
    public static Bitmap getBitmapFromString(String text, float fontSizeSP, Context context) {
        int fontSizePX = convertDiptoPix(context, fontSizeSP);
        int pad = (fontSizePX / 7);
        Paint paint = new Paint();

        paint.setAntiAlias(true);
        paint.setColor(Color.parseColor("#BB1E45"));
        paint.setTextAlign(Paint.Align.CENTER);
        paint.setTextSize(fontSizePX-12);

        int textWidth = (int) (paint.measureText(text) + pad * 2);
        int height = (int) (fontSizePX / 0.71);
        Bitmap bitmap = Bitmap.createBitmap(textWidth, height, Bitmap.Config.ARGB_4444);
        Canvas canvas = new Canvas(bitmap);
        float xOriginal = pad;
        canvas.drawText(text, xOriginal, fontSizePX, paint);

        return bitmap;
    }
    public static int convertDiptoPix(Context context, float dip) {
        int value = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dip, context.getResources().getDisplayMetrics());
        return value;
    }
}
