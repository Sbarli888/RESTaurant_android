package com.uliamar.restaurant.app.controller;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.media.MediaRouter;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.text.AndroidCharacter;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.Spinner;
import android.widget.Toast;

import com.squareup.otto.Subscribe;
import com.uliamar.restaurant.app.Bus.BusProvider;
import com.uliamar.restaurant.app.Bus.GetOrderDatasEvent;
import com.uliamar.restaurant.app.Bus.OnRestaurantDatasReceivedEvent;
import com.uliamar.restaurant.app.Bus.OnSavedOrderEvent;
import com.uliamar.restaurant.app.Bus.SaveOrderEvent;
import com.uliamar.restaurant.app.R;
import com.uliamar.restaurant.app.model.Dishe;
import com.uliamar.restaurant.app.model.Order;
import com.uliamar.restaurant.app.model.Restaurant;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class OrderEditActivity extends ActionBarActivity {
    Button mSendInvitationButton;
    OrderEditActivity ref;
    ProgressDialog progressDialog;
    Restaurant restaurant;
    Spinner mPeriod;
    DatePicker mDate;
    private int mRestaurantID;
    private List<String> list = new ArrayList<String>();
    private ArrayAdapter<String> adapter;
    private Order order;

    public static final String ARG_RESTAURANT_ID = "ARG_RESTAURANT_ID";


    public static Intent createIntent(Context c, int restaurantID) {
        Intent myIntent = new Intent(c, OrderEditActivity.class);
        myIntent.putExtra(ARG_RESTAURANT_ID, restaurantID);
        return myIntent;
    }

    private void init(){
        order = new Order();
        list.add("noon");
        list.add("evening");
        list.add("midnight");
        adapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item,list);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        init();
        setContentView(R.layout.activity_order_edit);
        ref = this;
        mRestaurantID = getIntent().getIntExtra(ARG_RESTAURANT_ID, 0);

        mPeriod = (Spinner) findViewById(R.id.spinner);
        mPeriod.setAdapter(adapter);
        /**
         *      Caused by: java.lang.RuntimeException: setOnItemClickListener cannot be used with a spinner.
         */
        mPeriod.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view,
                                       int position, long id) {
                //order.setRequest_date("noon");
                order.setRequest_period(list.get(position));
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                // TODO Auto-generated method stub
            }
        });

        mDate = (DatePicker) findViewById(R.id.datePicker);
        mDate.init(2014,5,14,new DatePicker.OnDateChangedListener() {
            @Override
            public void onDateChanged(DatePicker datePicker, int i, int i2, int i3) {
                order.setRequest_date(i+"-"+i2+"-"+i3);
                //order.setDate(date);
            }
        });

        mSendInvitationButton = (Button) findViewById(R.id.SendActivityButton);
        mSendInvitationButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                /**
                 * @To-DO: retrieve value of the UI and fill the order object;
                 */
                //Order order = new Order();
                //mDate.

                /**
                 {
                 ""customer_id"": 1,
                 ""restaurant_id"": 00001,
                 ""request_date"": ""2014-05-05"",
                 ""request_period"": ""noon|evening|midnight"",
                 ""customer_ids"": [1,2], // include host
                 ""dishes"": [
                 {
                 ""d_id"": 101,
                 ""name"": ""Big Mac"",
                 ""price"": ""18"",
                 ""quantity"": 3
                 },
                 // other ordered dish object
                 ]
                 }"
                 */

                order.setCustomer_id(1);
                order.setRestaurant_id(mRestaurantID);
                order.setRequest_date("2014-05-05");
                order.setRequest_period("1");
                int[] l;
                l = new int[2];
                l[0] = 1;
                l[1] = 2;
                order.setCustomer_ids(l);
                List<Dishe> dl = new ArrayList<Dishe>();
                Dishe d = new Dishe();
                d.setD_id(1);
                d.setName("Bites");
                d.setPrice(42);
                d.setQuantity(42);
                dl.add(d);
                order.setDishes(dl);

                progressDialog = new ProgressDialog(ref);
                progressDialog.setTitle("Loading");
                progressDialog.setMessage("Wait while loading...");
                progressDialog.show();
                BusProvider.get().post(new SaveOrderEvent(order));

            }
        });
    }


    @Override
    protected void onResume() {
        super.onResume();
        BusProvider.get().register(this);

        if (restaurant == null) {
            progressDialog = new ProgressDialog(ref);
            progressDialog.setTitle("Loading");
            progressDialog.setMessage("Wait while loading...");
            progressDialog.show();

            BusProvider.get().post(new GetOrderDatasEvent(mRestaurantID));
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        BusProvider.get().unregister(this);
    }

    @Subscribe
    public void OnRestaurantDatasReceived(OnRestaurantDatasReceivedEvent e) {
        progressDialog.dismiss();
        /**
         * @To-do: feed the view with data
         */
        Toast.makeText(this, "Datas received", Toast.LENGTH_SHORT).show();
    }

    @Subscribe
    public void OnSavedOrderEvent(OnSavedOrderEvent e) {
        progressDialog.dismiss();
        Intent i = OrderReviewActivity.createIntent(this, e.get().getiID());
        startActivity(i);
        finish();
    }

}