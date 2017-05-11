package com.example.eds.pointsofinterest;

/**
 * Created by Eds on 10/05/2017.
 */

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;


public class AddInterest extends Activity implements View.OnClickListener {


    TextView name;
    TextView types;
    TextView Desc;

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.interest);
        Button button = (Button)findViewById(R.id.button);
        button.setOnClickListener(this);

        name = (TextView) findViewById(R.id.editText);

        types = (TextView) findViewById(R.id.editText2);


        Desc = (TextView) findViewById(R.id.editText3);



    }


    public void onClick (View v){



        Intent intent = new Intent();
        Bundle bundle = new Bundle();

        String names = "";
        String type = "";
        String des= "";

        if (v.getId() == R.id.button){

            names = (name.getText().toString());
            type = (types.getText().toString());
            des = (Desc.getText().toString());


        }

        bundle.putString("com.example.tx1",names);
        bundle.putString("com.example.tx2",type);
        bundle.putString("com.example.tx3", des);


        intent.putExtras(bundle);
        setResult(RESULT_OK,intent);
        finish();
    }

}