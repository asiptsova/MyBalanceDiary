package com.application.mybalancediary;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Vector;

public class Snacks_Items extends AppCompatActivity {
    ArrayAdapter vectorAdapter;
    private ListView  list;
    Date date = new Date();
    String eat;
    Vector<String>vector_list =new Vector();
    @SuppressLint("SimpleDateFormat")
    String today= new SimpleDateFormat("yyyy-MM-dd").format(date);
    private DatabaseReference getCaloriesRef(String ref) {
        return FirebaseDatabase.getInstance().getReference("Snacks").child(today)
                .child(FirebaseAuth.getInstance().getCurrentUser().getUid()).child(ref);
    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_snacks_items);
        list=findViewById(R.id.listViewSnacks);
        Vector calories_snack= FoodSnacksAdapter.calories_snack;
        Vector proteins_snack= FoodSnacksAdapter.proteins_snack;
        Vector fats_snack= FoodSnacksAdapter.fats_snack;
        Vector carbs_snack= FoodSnacksAdapter.carbs_snack;
        FirebaseDatabase.getInstance().getReference("Snacks").child(today)
                .orderByKey().equalTo(FirebaseAuth.getInstance().getCurrentUser().getUid())
                .addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot ds : snapshot.getChildren()) {
                    eat=String.valueOf(ds.child("NameSnack").getValue());
                    String[] strArr = eat.split(",");
                    Vector<String> tmp=new Vector<String>();
                    vector_list.clear();
                    for(String str:strArr)
                        if(str!="")
                            vector_list.add(str);
                    vectorAdapter = new ArrayAdapter(Snacks_Items.this,android.R.layout.simple_list_item_checked, vector_list);
                    list.setAdapter(vectorAdapter);
                    list.invalidateViews();
                    list.refreshDrawableState();

                }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
        list.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
                vector_list.remove(position);
                String newTimeInDB="";
                for(String str: vector_list)
                    newTimeInDB+=str+",";
                vectorAdapter.notifyDataSetChanged();
                getCaloriesRef("NameBreakfast").setValue(newTimeInDB);
                vectorAdapter.notifyDataSetChanged();
                Float cal_snack=Float.parseFloat(String.valueOf(calories_snack.get(position)));
                calories_snack.remove(position);
                Float pr_snack=Float.parseFloat(String.valueOf(proteins_snack.get(position)));
                proteins_snack.remove(position);
                Float ft_snack=Float.parseFloat(String.valueOf(fats_snack.get(position)));
                fats_snack.remove(position);
                Float cr_snack=Float.parseFloat(String.valueOf(carbs_snack.get(position)));
                carbs_snack.remove(position);
                vectorAdapter.notifyDataSetChanged();
                list.invalidateViews();
                list.refreshDrawableState();
                FoodSnacksAdapter.total_cal-=cal_snack;
                FoodSnacksAdapter.total_proteins-=pr_snack;
                FoodSnacksAdapter.total_fats-=ft_snack;
                FoodSnacksAdapter.total_carb-=cr_snack;
                getCaloriesRef("total").setValue(Math.round(FoodSnacksAdapter.total_cal*10.0 ) / 10.0);
                getCaloriesRef("totalfats").setValue(Math.round(FoodSnacksAdapter.total_fats*10.0 ) / 10.0);
                getCaloriesRef("totalcarbs").setValue(Math.round(FoodSnacksAdapter.total_carb*10.0 ) / 10.0);
                getCaloriesRef("totalprotein").setValue(Math.round(FoodSnacksAdapter.total_proteins*10.0 ) / 10.0);
                return true;
            }
        });
    }

}