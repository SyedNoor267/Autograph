package adapter_classes;

import android.content.Context;
import android.graphics.Bitmap;
import android.support.annotation.NonNull;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.RequestOptions;
import helpers.HeaderMenu;
import userinterface.responsemodel.UserImage;

import com.example.appiapi.R;
import com.mikhaellopez.circularimageview.CircularImageView;
import com.yarolegovich.discretescrollview.DiscreteScrollView;

import java.util.ArrayList;

public class ScrollPaneAdapter extends DiscreteScrollView.Adapter<ScrollPaneAdapter.ScrollPaneHolder> {

    private final ArrayList<HeaderMenu> menuList;
    private final Context context;

    public ScrollPaneAdapter(Context context, ArrayList<HeaderMenu> menuList){
        this.context = context;
        this.menuList = menuList;
    }

    @NonNull
    @Override
    public ScrollPaneHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(context).inflate(R.layout.header_layout, viewGroup, false);
        return new ScrollPaneHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ScrollPaneHolder viewHolder, int i) {
        viewHolder.headerTitle.setText(menuList.get(i).getTitle());
        //
        if(menuList.get(i).getImageType().equals("BITMAP")){
            String initials = menuList.get(i).getImage().toString().toUpperCase();
            Bitmap imagedb = UserImage.getBitmapFromString(initials, 32.0f, context);
            viewHolder.headerImageHolder.setImageBitmap(imagedb);
        }else {
            Glide.with(context)
                    .asBitmap()
                    .load(menuList.get(i).getImage())
                    .apply(new RequestOptions()
                            .diskCacheStrategy(DiskCacheStrategy.AUTOMATIC)
                            .fitCenter()
                            .override(200, 200)
                    ).into(viewHolder.headerImageHolder);
        }
    }

    @Override
    public int getItemCount() {
        return menuList.size();
    }

    public class ScrollPaneHolder extends DiscreteScrollView.ViewHolder{
        final CircularImageView headerImageHolder;
        final TextView headerTitle;
        private ScrollPaneHolder(@NonNull View itemView) {
            super(itemView);
            headerImageHolder = itemView.findViewById(R.id.header_image_holder);
            headerTitle = itemView.findViewById(R.id.header_title);
        }
    }


}
