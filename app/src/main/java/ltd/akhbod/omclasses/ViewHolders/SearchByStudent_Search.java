package ltd.akhbod.omclasses.ViewHolders;

import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import ltd.akhbod.omclasses.ModalClasses.ProfileDetails;
import ltd.akhbod.omclasses.R;
import ltd.akhbod.omclasses.RecordActivity;

/**
 * Created by ibm on 13-06-2018.
 */

public class SearchByStudent_Search extends RecyclerView.ViewHolder {

    TextView mName;
    View mView;

    public SearchByStudent_Search(View itemView) {
        super(itemView);
        mView = itemView;
        mName=mView.findViewById(R.id.search_singleLayout_name);
    }


    public void setDetails(final ProfileDetails model, final Context applicationContext, final String selectedStanderdText,
    final String duration) {



        mName.setText(model.getName());
        mName.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(applicationContext,model.getName(),Toast.LENGTH_SHORT);
                Intent intent = new Intent(applicationContext, RecordActivity.class);
                intent.putExtra("id", model.getId());
                intent.putExtra("name", model.getName());
                intent.putExtra("schoolname", model.getSchool());
                intent.putExtra("address", model.getAddress());
                intent.putExtra("mobile", model.getMobNo());
                intent.putExtra("class",selectedStanderdText);
                intent.putExtra("duration",duration);
                intent.putExtra("url",model.getImageUrl());
                intent.putExtra("duration",durationText);
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                applicationContext.startActivity(intent);
            }
        });
    }
}
