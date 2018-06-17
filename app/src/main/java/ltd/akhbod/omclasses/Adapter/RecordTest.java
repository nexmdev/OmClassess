package ltd.akhbod.omclasses.Adapter;

import android.content.Context;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.EditText;
import android.widget.TextView;

import java.util.ArrayList;

import ltd.akhbod.omclasses.R;

/**
 * Created by ibm on 14-06-2018.
 */

public class RecordTest extends BaseAdapter {

    Context c;

    String key=null;
    int TotalStudentCount=0;

    ArrayList<String> marksArray=new ArrayList<>();
    ArrayList<String> studentIdArray=new ArrayList<>();
    ArrayList<String> studentNamesArray=new ArrayList<>();


    public RecordTest(Context c, ArrayList<String> studentIds, ArrayList<String> studentNames, String key, int totalCount){
        this.c=c;
        this.studentIdArray=studentIds;
        this.studentNamesArray=studentNames;
        this.key=key;
        this.TotalStudentCount=totalCount;
    }

    @Override
    public int getCount() {
        return TotalStudentCount;
    }

    @Override
    public Object getItem(int position) {
        return null;
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {

        marksArray.add(position,"0");

        LayoutInflater inflater= (LayoutInflater) c.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View row=inflater.inflate(R.layout.recordtest_singlelayout,parent,false);


        TextView mPosition=row.findViewById(R.id.recordtest_single_postion);
        TextView mName=row.findViewById(R.id.recordtest_single_name);
        final EditText mEditMarks=row.findViewById(R.id.recordtest_single_editMarksEdit);

        mEditMarks.addTextChangedListener(new TextWatcher(){

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {

                marksArray.set(position,s.toString());
            }
        });

        mPosition.setText(position+1+")");
        mName.setText(studentNamesArray.get(position));



        return row;
    }

    public ArrayList<String> getMarks(){
        return marksArray;
    }
}