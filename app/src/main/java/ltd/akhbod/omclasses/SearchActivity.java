package ltd.akhbod.omclasses;

import android.app.DatePickerDialog;
import android.icu.text.SimpleDateFormat;
import android.os.Build;
import android.support.annotation.RequiresApi;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ListAdapter;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;

import java.util.Date;
import java.util.Locale;

import ltd.akhbod.omclasses.ModalClasses.ProfileDetails;
import ltd.akhbod.omclasses.ModalClasses.SearchByDateDetails;
import ltd.akhbod.omclasses.ViewHolders.SearchByStudent_Search;
import ltd.akhbod.omclasses.ViewHolders.SearchByDate_Search;

public class SearchActivity extends AppCompatActivity implements DatePickerDialog.OnDateSetListener {

    //layout variables
    private EditText mSearchField;
    private Spinner mSearchTypeSpinner,mStanderdSpinner;
    private ImageView mSerachBtn,mDateBtn;
    private RecyclerView mRecyclerView;
    private ProgressBar progressBar;
    private TextView noDataTextView;

    //Activity variables
    DatePickerDialog datePickerDialog;
    private String mSearchTypeText,mSelectedStanderdText,durationText;

    //firebase variables
    private DatabaseReference mDatabaseRef;
    private FirebaseRecyclerAdapter<SearchByDateDetails, SearchByDate_Search> testAdapter=null;

    @RequiresApi(api = Build.VERSION_CODES.N)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);

        getSupportActionBar().setTitle("Search Record");
        int year= Integer.parseInt(new SimpleDateFormat("yyyy", Locale.getDefault()).format(new Date()));
        int nextYear=year+1;
        durationText=year+"-"+nextYear;

        mSearchField = findViewById(R.id.search_field);
        mSearchTypeSpinner=findViewById(R.id.search_type);
        mStanderdSpinner=findViewById(R.id.search_standerd);
        mSerachBtn = findViewById(R.id.search_btn);
        mDateBtn=findViewById(R.id.search_date);
        mRecyclerView = findViewById(R.id.result_list);
        mDatabaseRef = FirebaseDatabase.getInstance().getReference();
        progressBar = (ProgressBar)findViewById(R.id.search_progressBar) ;
        noDataTextView = findViewById(R.id.search_no_data_textview);

        int month = Integer.parseInt(new SimpleDateFormat("MM", Locale.getDefault()).format(new Date()));
        datePickerDialog = new DatePickerDialog(
                SearchActivity.this,  SearchActivity.this, year,month,
                1);


        ArrayAdapter SearchTypeAdapter=ArrayAdapter.createFromResource(this,R.array.search_type,android.R.layout.simple_spinner_dropdown_item);
        mSearchTypeSpinner.setAdapter(SearchTypeAdapter);
        mSearchTypeSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                mSearchTypeText=parent.getItemAtPosition(position).toString();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });
        mSearchTypeText="student";

        ArrayAdapter standeredAdapter=ArrayAdapter.createFromResource(this,R.array.class_name,android.R.layout.simple_spinner_dropdown_item);
        mStanderdSpinner.setAdapter(standeredAdapter);
        mStanderdSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                mSelectedStanderdText=parent.getItemAtPosition(position).toString();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                mSelectedStanderdText=parent.getItemAtPosition(0).toString();
            }
        });
        mSelectedStanderdText="11th";


       mSearchField.addTextChangedListener(new TextWatcher(){
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                progressBar.setVisibility(View.VISIBLE);
                if(mSearchTypeText.equals("student")) firebaseStudentSearch(s.toString());
                else if(mSearchTypeText.equals("date")) firebaseTestSearch(s.toString());
            }

            @Override
            public void afterTextChanged(Editable s) {
              //  progressBar.setVisibility(View.VISIBLE);
              //  if(mSearchTypeText.equals("student")) firebaseStudentSearch(s.toString());
              //  else if(mSearchTypeText.equals("date")) firebaseTestSearch(s.toString());
            }
        });


        mDateBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
             datePickerDialog.show();
            }});


        mSerachBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(getApplicationContext(),"search"+mSearchField.getText().toString(),Toast.LENGTH_SHORT).show();
                progressBar.setVisibility(View.VISIBLE);
                if(mSearchTypeText.equals("student")) firebaseStudentSearch(mSearchField.getText().toString());
                else if(mSearchTypeText.equals("date")) firebaseTestSearch(mSearchField.getText().toString());
            }});

        mRecyclerView.setHasFixedSize(true);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(this));

    }

    private void firebaseStudentSearch(final String searchText) {

        Query query=null;

            if(searchText.isEmpty())    query=mDatabaseRef.child(mSelectedStanderdText+"("+durationText+")").child("profile").orderByChild("name");
            else                        query=mDatabaseRef.child(mSelectedStanderdText+"("+durationText+")").child("profile").orderByChild("name").startAt(searchText).endAt(searchText+"\uf88f");

        FirebaseRecyclerAdapter<ProfileDetails,SearchByStudent_Search> adapter=new FirebaseRecyclerAdapter <ProfileDetails, SearchByStudent_Search>(
                ProfileDetails.class,
                R.layout.search_singlestudent_layout,
                SearchByStudent_Search.class,
                query
        ) {
            @Override
            public void onDataChanged() {
                if(getItemCount()==0){
                    noDataTextView.setVisibility(View.VISIBLE);
                    progressBar.setVisibility(View.INVISIBLE);
                }else{
                    progressBar.setVisibility(View.INVISIBLE);
                    noDataTextView.setVisibility(View.INVISIBLE);
                }
            }
            @Override
            protected void populateViewHolder(SearchByStudent_Search viewHolder, final ProfileDetails model, int position) {
                viewHolder.setDetails(model,getApplicationContext(),mSelectedStanderdText);
            }};
        adapter.notifyDataSetChanged();
        mRecyclerView.setAdapter(adapter);


    }


    private void firebaseTestSearch(final String searchText) {

        Query query=null;

        if(searchText.isEmpty())    query=mDatabaseRef.child(mSelectedStanderdText+"("+durationText+")").child("search").orderByKey();
        else                        query=mDatabaseRef.child(mSelectedStanderdText+"("+durationText+")").child("search").orderByKey().startAt(searchText).endAt(searchText+"\uf88f");

        testAdapter=new FirebaseRecyclerAdapter <SearchByDateDetails,SearchByDate_Search>(
                SearchByDateDetails.class,
                R.layout.search_singlestudent_layout,
                SearchByDate_Search.class,
                query
        ) {
            @Override
            public void onDataChanged() {
                if(getItemCount()==0){
                    noDataTextView.setVisibility(View.VISIBLE);
                    progressBar.setVisibility(View.INVISIBLE);
                }else{
                    progressBar.setVisibility(View.INVISIBLE);
                    noDataTextView.setVisibility(View.INVISIBLE);
                }
            }
            @Override
            protected void populateViewHolder(SearchByDate_Search viewHolder, final SearchByDateDetails model, int position) {
                final String key=testAdapter.getRef(position).getKey();
                viewHolder.setDetails(model,getApplicationContext(),key,searchText,mSelectedStanderdText);
            }};

        mRecyclerView.setAdapter(testAdapter);
        progressBar.setVisibility(View.INVISIBLE);
    }



    @Override
    public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {

        month++;
        String MONTH;
        if(month<10){
            MONTH="0"+(month);
        }
        else
            MONTH=""+(month);

        String date=dayOfMonth+"-"+MONTH+"-"+year;
        mSearchField.setText(date);
    }



    }
