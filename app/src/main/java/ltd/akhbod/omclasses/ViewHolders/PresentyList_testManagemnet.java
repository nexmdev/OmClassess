package ltd.akhbod.omclasses.ViewHolders;

import android.content.Context;
import android.os.Build;
import android.support.annotation.RequiresApi;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;

import java.util.ArrayList;

import ltd.akhbod.omclasses.ModalClasses.ProfileDetails;
import ltd.akhbod.omclasses.R;

/**
 * Created by ibm on 14-06-2018.
 */

public class PresentyList_testManagemnet extends RecyclerView.ViewHolder{

    public View mView;
    public TextView mPresenetLayout,mAbsentLayout;
    public TextView mName;
    public ImageButton mSendMessage;

    public PresentyList_testManagemnet(View itemView) {
        super(itemView);
        mView=itemView;

        mName=mView.findViewById(R.id.manage_singleSitem_name);
        mPresenetLayout=mView.findViewById(R.id.manage_singleSitem_P);
        mAbsentLayout=mView.findViewById(R.id.manage_singleSitem_A);
        mSendMessage=mView.findViewById(R.id.manage_singleSitem_massageBtn);
    }


    public void setDetails(Context applicationContext, ProfileDetails model, final int position) {

        mName.setText(position+1+". "+model.getName());

    }
}
