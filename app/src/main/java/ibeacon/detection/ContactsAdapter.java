package ibeacon.detection;

import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.annotation.RequiresApi;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.appiapi.R;
import com.squareup.picasso.Picasso;

import java.util.List;

import userinterface.ProfileScreens;
import userinterface.chat.ChattingStart;
import userinterface.helperconfiguration.ConfigurationConstant;

import static com.facebook.FacebookSdk.getApplicationContext;

public class ContactsAdapter extends RecyclerView.Adapter<ContactsAdapter.Holder> {


    private final Context context;
    private final List<Contact> mContacts;

    public ContactsAdapter(Context mcontext, List<Contact> contacts)
    {
        mContacts = contacts;
        context = mcontext;

    }

    @NonNull
    @Override
    public Holder onCreateViewHolder(@NonNull ViewGroup parent, int i) {

        LayoutInflater inflater = LayoutInflater.from(context);

        return new Holder(inflater.inflate(R.layout.people_near_you, parent, false));

    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    @Override
    public void onBindViewHolder(@NonNull Holder holder, int i) {
        Contact contact = mContacts.get(i);
        TextView textView = holder.nameText;
        textView.setText(contact.name);
        ImageView im = holder.beaconImage;
        Picasso.with(getApplicationContext()).load(contact.image).into(im);
        Button connectButton = holder.connectButton;
        Button profileButton = holder.profileButton;
        connectButton.setOnClickListener(v -> {
            Intent intent=new Intent(v.getContext(), ChattingStart.class);
            intent.putExtra(ConfigurationConstant.NICKNAME,contact.nickname);
            intent.putExtra(ConfigurationConstant.NAME,contact.name);
            intent.putExtra("image",contact.image);

            v.getContext().startActivity(intent);
        });
        profileButton.setOnClickListener(v -> {
            Intent intent=new Intent(v.getContext(), ProfileScreens.class);
            intent.putExtra(ConfigurationConstant.NICKNAME,contact.nickname);
            intent.putExtra("LOAD_SCREEN", "CONTACT_INFORMATION");

            v.getContext().startActivity(intent);
        });

    }

    @Override
    public int getItemCount() {

        return mContacts.size();
    }

    public class Holder extends RecyclerView.ViewHolder{

        private final TextView nameText;
        private final Button connectButton;
        private final Button profileButton;
        private final ImageView beaconImage;

        private Holder(@NonNull View itemView) {

            super(itemView);

            nameText = itemView.findViewById(R.id.beaconId);
            connectButton = itemView.findViewById(R.id.connect_button);
            profileButton = itemView.findViewById(R.id.profile_button);
            beaconImage = itemView.findViewById(R.id.beacon_image);
        }
    }

}


