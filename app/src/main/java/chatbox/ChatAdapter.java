package chatbox;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.appiapi.R;

import java.util.ArrayList;



@SuppressWarnings("unchecked")
public class ChatAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    Context context1;
    private ArrayList<ChatObject> arrayList;

    public ChatAdapter(Context context, ArrayList arrayList) {
        context1= context;
        this.arrayList = arrayList;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView;
        if (viewType == 0) {
            // self message
            itemView = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.send_chat_item, parent, false);
        }else {
            itemView = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.recieve_chat_item, parent, false);
        }
        return new ViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        ChatObject chatObject=arrayList.get(position);
        if(chatObject.getCheck()==0){
            ((ViewHolder) holder).msgSend.setText(chatObject.msg);
        }else{
            ((ViewHolder) holder).msgReceive.setText(chatObject.msg);
        }
//        holder.msg.setText(chatObject.msg);
    }

    @Override
    public int getItemViewType(int position) {
        int check=arrayList.get(position).getCheck();
        if(check==0){   //me
            return 0;
        }else if(check==1){
            return 1;
        }else{
            return position;
        }
        //return super.getItemViewType(position);
    }

    @Override
    public int getItemCount() {
        return arrayList.size();
    }

    private class ViewHolder extends RecyclerView.ViewHolder {
        private final TextView msgSend;
        private final TextView msgReceive;

        private ViewHolder(View view) {
            super(view);
            msgSend= itemView.findViewById(R.id.cht_txt_send);
            msgReceive= itemView.findViewById(R.id.cht_txt_rv);
        }
    }

}
