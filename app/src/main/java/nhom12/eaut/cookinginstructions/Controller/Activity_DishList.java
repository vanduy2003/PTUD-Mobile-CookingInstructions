package nhom12.eaut.cookinginstructions.Controller;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.GridView;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

import nhom12.eaut.cookinginstructions.Adapter.DishAdapter;
import nhom12.eaut.cookinginstructions.Model.DishItem;
import nhom12.eaut.cookinginstructions.R;

public class Activity_DishList extends AppCompatActivity {
    GridView gvDishList;
    DishAdapter dishAdapter;
    ArrayList<DishItem> arr = new ArrayList<>();
    private FirebaseDatabase database;
    private DatabaseReference recipesRef;
    FloatingActionButton btnThoat;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dish_list);

        gvDishList = findViewById(R.id.gvDishList);
        dishAdapter = new DishAdapter(this, R.layout.layout_item_dishlist, arr);
        gvDishList.setAdapter(dishAdapter);

        // Nhận CategoryId từ Intent
        String categoryId = getIntent().getStringExtra("CategoryId");

        loadDishesByCategory(categoryId);

        // Xử lý sự kiện khi nhấn vào một món ăn
        gvDishList.setOnItemClickListener((parent, view, position, id) -> {
            DishItem selectedDish = arr.get(position);
            Intent intent = new Intent(Activity_DishList.this, Activity_RecipeDetail.class);
            intent.putExtra("RecipeId", selectedDish.getId());
            intent.putExtra("RecipeName", selectedDish.getTitle());
            intent.putExtra("RecipeDescription", selectedDish.getDescription());
            startActivity(intent);
        });

        btnThoat = findViewById(R.id.btnThoat);
        btnThoat.setOnClickListener(v -> {
            finish();
        });
    }

    private void loadDishesByCategory(String categoryId) {
        database = FirebaseDatabase.getInstance();
        recipesRef = database.getReference("Recipes");

        recipesRef.orderByChild("category_id").equalTo(categoryId)
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        arr.clear(); // Xóa dữ liệu cũ trước khi thêm dữ liệu mới
                        for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                            String RecipeId = snapshot.getKey(); // Lấy id của món ăn
                            DishItem dish = snapshot.getValue(DishItem.class);
                            if (dish != null) {
                                dish.setId(RecipeId);
                                arr.add(dish);
                            }
                        }
                        dishAdapter.notifyDataSetChanged();
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {
                        Log.w("Firebase", "loadDishesByCategory:onCancelled", databaseError.toException());
                    }
                });
    }
}

