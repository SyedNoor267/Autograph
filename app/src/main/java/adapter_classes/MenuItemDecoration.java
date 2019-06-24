package adapter_classes;

import android.graphics.Rect;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.View;

import com.yarolegovich.discretescrollview.DiscreteScrollView;

public class MenuItemDecoration extends DiscreteScrollView.ItemDecoration {
    @Override
    public void getItemOffsets(@NonNull Rect outRect, @NonNull View view, @NonNull RecyclerView parent, @NonNull RecyclerView.State state) {
        super.getItemOffsets(outRect, view, parent, state);
        //outRect.right = view.getResources().getDisplayMetrics().densityDpi * 2;
        outRect.right = -30;
        outRect.left = -30;
    }
}
