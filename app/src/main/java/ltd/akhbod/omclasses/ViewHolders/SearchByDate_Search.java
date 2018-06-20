package ltd.akhbod.omclasses.ViewHolders;

import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;

import ltd.akhbod.omclasses.R;
import ltd.akhbod.omclasses.RecordTestActivity;
import ltd.akhbod.omclasses.ModalClasses.SearchByDateDetails;

/**
 * Created by ibm on 14-06-2018.
 */

public class SearchByDate_Search extends RecyclerView.ViewHolder {

    TextView mName;
    View mView;
    public interface OnItemClickListener {
        void onItemClick(View itemView, int position);
    }
    private static SearchByDate_Search.OnItemClickListener listener;

    public void setOnItemClickListener(OnItemClickListener listener){
        SearchByDate_Search.listener = listener;
    }


    public SearchByDate_Search(final View itemView) {
        super(itemView);
        mView = itemView;
        mName=mView.findViewById(R.id.search_singleLayout_name);
        itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (listener != null) {
                    int position = getAdapterPosition();

                    if (position != RecyclerView.NO_POSITION) {
                        listener.onItemClick(itemView,position);
                    }
                }
            }
        });
    }

    public void setDetails(final SearchByDateDetails model, final Context applicationContext,String key,  String searchText, final String mSelectedStanderdText) {


        String testText=key.replace(searchText+"-","");
        mName.setText(testText);
       /* mName.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {


                Intent intent = new Intent(applicationContext, RecordTestActivity.class);
                intent.putExtra("key",key);
                intent.putExtra("totalPresent",model.getTotalPresent());
                intent.putExtra("class",mSelectedStanderdText);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                applicationContext.startActivity(intent);

            }
        });*/

    }
}
