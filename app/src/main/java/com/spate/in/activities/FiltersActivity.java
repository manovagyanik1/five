package com.spate.in.activities;

import android.app.Activity;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.TextView;
import android.widget.TimePicker;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;

import com.spate.in.R;
import com.spate.in.utils.Gen;
import com.spate.in.utils.JsonObjectRequestWithAuth;
import com.spate.in.utils.VolleySingelton;
import com.wdullaer.materialdatetimepicker.time.TimePickerDialog;

import org.florescu.android.rangeseekbar.RangeSeekBar;
import org.json.JSONException;
import org.json.JSONObject;


public class FiltersActivity extends AppCompatActivity implements TimePickerDialog.OnTimeSetListener {

    private static final String TAG = FiltersActivity.class.getSimpleName();
    private static final String FILTERS = "filters";
    private final String MALE = "male";
    private final String FEMALE = "female";
    private final String CASUAL = "casual";
    private final String RELATIONSHIP = "relationship";
    private final String LOVE = "love";
    private final String FRIENDSHIP = "friendship";
    private final String ACTION = "action";
    private final String MINAGE = "minAge";
    private final String MAXAGE = "maxAge";
    private final String MINTIME = "minTime";
    private final String MAXTIME = "maxTime";
    private final String MONDAY = "monday";
    private final String TUESDAY = "tuesday";
    private final String WEDNESDAY = "wednesday";
    private final String THURSDAY = "thursday";
    private final String FRIDAY = "friday";
    private final String SATURDAY = "saturday";
    private final String SUNDAY = "sunday";
    private final String FROM_INIT_TIME = "12:00 am";
    private final String TO_INIT_TIME = "11:59 pm";

    CheckBox mMale, mFemale, mCasual, mRelationship, mLove, mFriendship, mAction, mMonday, mTuesday, mWednesday, mThursday, mFriday, mSaturday, mSunday, mAllDay;
    RangeSeekBar mAgeBar;
    Button mSubmit;
    TextView mFromTime, mToTime;
    String currentlyClicked;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_filters);

        mMale = (CheckBox) findViewById(R.id.male_checkbox);
        mFemale = (CheckBox) findViewById(R.id.female_checkbox);
        mCasual = (CheckBox) findViewById(R.id.casual_checkbox);
        mRelationship = (CheckBox) findViewById(R.id.relationship_checkbox);
        mLove = (CheckBox) findViewById(R.id.love_checkbox);
        mFriendship = (CheckBox) findViewById(R.id.friendship_checkbox);
        mAction = (CheckBox) findViewById(R.id.action_checkbox);
        mMonday = (CheckBox) findViewById(R.id.monday_checkbox);
        mTuesday = (CheckBox) findViewById(R.id.tuesday_checkbox);
        mWednesday = (CheckBox) findViewById(R.id.wednesday_checkbox);
        mThursday = (CheckBox) findViewById(R.id.thursday_checkbox);
        mFriday = (CheckBox) findViewById(R.id.friday_checkbox);
        mSaturday = (CheckBox) findViewById(R.id.saturday_checkbox);
        mSunday = (CheckBox) findViewById(R.id.sunday_checkbox);
        mAllDay = (CheckBox) findViewById(R.id.all_day_checkbox);
        mAgeBar = (RangeSeekBar) findViewById(R.id.age_seekbar);
        mSubmit = (Button) findViewById(R.id.submit_filters);
        mFromTime = (TextView) findViewById(R.id.from_time);
        mToTime = (TextView) findViewById(R.id.to_time);

        mFromTime.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                currentlyClicked = "FromTime";
                onTimerClick(v);
            }
        });

        mToTime.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                currentlyClicked = "ToTime";
                onTimerClick(v);
            }
        });

        mAllDay.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if(isChecked){
                    mMonday.setChecked(true);
                    mTuesday.setChecked(true);
                    mWednesday.setChecked(true);
                    mThursday.setChecked(true);
                    mFriday.setChecked(true);
                    mSaturday.setChecked(true);
                    mSunday.setChecked(true);
                } else {
                    mMonday.setChecked(false);
                    mTuesday.setChecked(false);
                    mWednesday.setChecked(false);
                    mThursday.setChecked(false);
                    mFriday.setChecked(false);
                    mSaturday.setChecked(false);
                    mSunday.setChecked(false);
                }
            }
        });

        mSubmit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                saveFilters();
            }
        });
    }

    public void onTimerClick(View v) {

        final TextView textView = (TextView) v;
        if (v == textView) {
            if(textView.getText().toString().equals("") || !textView.getText().toString().contains(":")){
                if(textView == mFromTime)
                    textView.setText(FROM_INIT_TIME);
                else
                    textView.setText(TO_INIT_TIME);
            }
            String time = textView.getText().toString().split(" ")[0];
            int hour = Integer.parseInt(time.split(":")[0]);
            int min = Integer.parseInt(time.split(":")[1]);

            // Launch Time Picker Dialog
            TimePickerDialog timePickerDialog  = TimePickerDialog.newInstance(FiltersActivity.this, hour, min, false);
            if(textView == mFromTime){
                timePickerDialog.setMaxTime(23, 54, 0);
            } else if(textView == mToTime){
                String[] fromTime = Gen.convertTime12To24format(mFromTime.getText().toString()).split(":");
                int fhour = Integer.parseInt(fromTime[0]);
                int fmin = Integer.parseInt(fromTime[1]);
                timePickerDialog.setMinTime(fhour, fmin, 0);
            }
            timePickerDialog.show(getFragmentManager(), "Timepickerdialog");
        }
    }

    @Override
    protected void onStart() {
        super.onStart();
        loadFilters();
    }

    private void loadFilters() {
        RequestQueue requestQueue = VolleySingelton.getInstance().getRequestQueue();
        Gen.showLoader(this);
        final Activity activity = this;
        JsonObjectRequest request = new JsonObjectRequestWithAuth(Request.Method.GET, Gen.SERVER_URL + "/get_filters", null, new Response.Listener<JSONObject>() {

            @Override
            public void onResponse(JSONObject response) {
                Gen.hideLoader(activity);
                if(!response.isNull("filters")){
                    try {
                        JSONObject filters = response.getJSONObject("filters");
                        mMale.setChecked(filters.getBoolean(MALE ));
                        mFemale.setChecked(filters.getBoolean(FEMALE ));
                        mCasual.setChecked(filters.getBoolean(CASUAL ));
                        mRelationship.setChecked(filters.getBoolean(RELATIONSHIP ));
                        mLove.setChecked(filters.getBoolean(LOVE ));
                        mFriendship.setChecked(filters.getBoolean(FRIENDSHIP ));
                        mAction.setChecked(filters.getBoolean(ACTION ));
                        mMonday.setChecked(filters.getBoolean(MONDAY ));
                        mTuesday.setChecked(filters.getBoolean(TUESDAY ));
                        mWednesday.setChecked(filters.getBoolean(WEDNESDAY ));
                        mThursday.setChecked(filters.getBoolean(THURSDAY ));
                        mFriday.setChecked(filters.getBoolean(FRIDAY ));
                        mSaturday.setChecked(filters.getBoolean(SATURDAY ));
                        mSunday.setChecked(filters.getBoolean(SUNDAY ));

                        mAgeBar.setSelected(true);
                        mAgeBar.setSelectedMinValue(filters.getInt(MINAGE));
                        mAgeBar.setSelectedMaxValue(filters.getInt(MAXAGE));

                        if(!filters.has(MINTIME) || filters.getString(MINTIME).equals("")){
                            mFromTime.setText(FROM_INIT_TIME);
                        } else{
                            mFromTime.setText(Gen.convertTime24To12format(filters.getString(MINTIME)));
                        }

                        if(!filters.has(MAXTIME) || filters.getString(MAXTIME).equals("")){
                            mToTime.setText(TO_INIT_TIME);
                        } else{
                            mToTime.setText(Gen.convertTime24To12format(filters.getString(MAXTIME)));
                        }
                    } catch (JSONException e) {
                        Gen.showError(e);
                    }
                }
            }
        }, new Response.ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError error) {
                Gen.hideLoader(activity);
                Gen.showVolleyError(error);
            }
        });
        requestQueue.add(request);
    }


    @Override
    protected void onResume() {
        super.onResume();
        Gen.setCurrentForegroundActivity(this);
        Gen.setAppActive(true);
    }

    @Override
    protected void onStop() {
        super.onStop();
        Gen.setAppActive(false);
    }

    private boolean validateData() {
        if(!mMale.isChecked() && !mFemale.isChecked()){
            Gen.toast("At least select one gender to talk to!");
            return false;
        } else if(!mCasual.isChecked() && !mRelationship.isChecked() && !mLove.isChecked()&& !mFriendship.isChecked()&& !mAction.isChecked()){
            Gen.toast("Select something to talk about!");
            return false;
        } else if(!mMonday.isChecked() && !mTuesday.isChecked() && !mWednesday.isChecked()&& !mThursday.isChecked()&& !mFriday.isChecked()&& !mSaturday.isChecked()&& !mSunday.isChecked()){
            Gen.toast("Select at least one day when you want to talk!");
            return false;
        }
        return true;
    }

    private void saveFilters(){
        if(!validateData())
            return;

        Gen.showLoader(this);
        RequestQueue requestQueue = VolleySingelton.getInstance().getRequestQueue();
        JSONObject postData = null;
        try {
            postData = getPostData();
        } catch (JSONException e) {
            Gen.showError(e);
        }

        final Activity activity = this;
        JsonObjectRequest request = new JsonObjectRequestWithAuth(Request.Method.POST, Gen.SERVER_URL + "/update_user_details", postData, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                Gen.saveFiltersToLocalStorage(true); // this indicates that the filter screen is done by the user
                Gen.hideLoader(activity);
                Gen.startActivity(activity, false, CallStatusActivity.class);
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Gen.hideLoader(activity);
                Gen.showVolleyError(error);
            }
        });
        requestQueue.add(request);
    }

    private JSONObject getPostData() throws JSONException {
        JSONObject js = new JSONObject();
        js.put(MALE, mMale.isChecked());
        js.put(FEMALE, mFemale.isChecked());
        js.put(CASUAL, mCasual.isChecked());
        js.put(RELATIONSHIP, mRelationship.isChecked());
        js.put(LOVE, mLove.isChecked());
        js.put(FRIENDSHIP, mFriendship.isChecked());
        js.put(ACTION, mAction.isChecked());
        js.put(MONDAY, mMonday.isChecked());
        js.put(TUESDAY, mTuesday.isChecked());
        js.put(WEDNESDAY, mWednesday.isChecked());
        js.put(THURSDAY, mThursday.isChecked());
        js.put(FRIDAY, mFriday.isChecked());
        js.put(SATURDAY, mSaturday.isChecked());
        js.put(SUNDAY, mSunday.isChecked());
        js.put(MINAGE, mAgeBar.getSelectedMinValue());
        js.put(MAXAGE, mAgeBar.getSelectedMaxValue());
        js.put(MINTIME, Gen.convertTime12To24format(mFromTime.getText().toString()));
        js.put(MAXTIME, Gen.convertTime12To24format(mToTime.getText().toString()));
        JSONObject filters = new JSONObject();
        filters.put(FILTERS, js);
        return filters;
    }

    @Override
    public void onTimeSet(TimePickerDialog view, int hourOfDay, int minute, int second) {
        String AMPM = "am";
        int hourOfDayPrev = hourOfDay;
        if(hourOfDay >=12){
            AMPM = "pm";
        } if(hourOfDay >=13){
            hourOfDay-=12;
        }
        if(hourOfDay == 0){
            hourOfDay = 12;
        }
        String hour = hourOfDay+"";
        if(hourOfDay<10)
            hour = "0"+hour;

        String min = minute+"";
        if(minute<10)
            min = "0"+min;
        if(currentlyClicked.equals("FromTime")){
            mFromTime.setText(hour + ":" + min + " " + AMPM);

            // TODO: Shubham Was seeing some crashes here in the phone. Not sure why. So commenting it for now.
//            String[] toTime = Gen.convertTime12To24format(mToTime.getText().toString()).split(":");
//            int thour = Integer.parseInt(toTime[0]);
//            int tmin = Integer.parseInt(toTime[1]);
//            if(hourOfDayPrev > thour || (hourOfDayPrev == thour && minute > tmin)){
//                mToTime.callOnClick();
//            }
        }
        else if(currentlyClicked.equals("ToTime"))
            mToTime.setText(hour + ":" + min + " " + AMPM);
    }
}
