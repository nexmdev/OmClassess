package ltd.akhbod.omclasses.ViewHolders;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ProgressBar;
import android.widget.TextView;

import ltd.akhbod.omclasses.ModalClasses.ProfileDetails;
import ltd.akhbod.omclasses.R;

/**
 * Created by ibm on 14-06-2018.
 */

public class PresentyList_testManagemnet extends RecyclerView.ViewHolder{

    public final View mView;
    public final TextView mPresenetLayout;
    public final TextView mAbsentLayout;
    public final TextView mName;
    public final ImageButton mSendMessage;
    public final ProgressBar progressBar;
    public PresentyList_testManagemnet(View itemView) {
        super(itemView);
        mView=itemView;

        mName=mView.findViewById(R.id.manage_singleSitem_name);
        mPresenetLayout=mView.findViewById(R.id.manage_singleSitem_P);
        mAbsentLayout=mView.findViewById(R.id.manage_singleSitem_A);
        mSendMessage=mView.findViewById(R.id.manage_singleSitem_massageBtn);
        progressBar = mView.findViewById(R.id.test_managment_single_row_progressbar);
    }


    public void setDetails(Context applicationContext, ProfileDetails model, final int position,String present) {

        mName.setText(position+1+". "+model.getName());
        if(present.matches("yes")){
            mPresenetLayout.setBackgroundResource(R.color.green);
            mAbsentLayout.setBackground(null);
            mSendMessage.setEnabled(false);
        }else if(present.matches("x")){
            mPresenetLayout.setBackground(null);
            mAbsentLayout.setBackground(null);
           // mSendMessage.setBackgroundResource(R.drawable.ic_message_black_24dp_grey);
            mSendMessage.setEnabled(false);
        }else{
            mAbsentLayout.setBackgroundResource(R.color.red);
            mSendMessage.setBackgroundResource(R.drawable.ic_message_black_24dp);
            mSendMessage.setEnabled(true);
        }

    }
}
