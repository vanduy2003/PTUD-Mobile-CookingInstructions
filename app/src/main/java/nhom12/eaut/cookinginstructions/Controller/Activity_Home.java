package nhom12.eaut.cookinginstructions.Controller;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.widget.GridView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.Toast;

import java.util.ArrayList;

import nhom12.eaut.cookinginstructions.Adapter.CatagoryAdapter;
import nhom12.eaut.cookinginstructions.Model.CatagoryItem;
import nhom12.eaut.cookinginstructions.R;
public class Activity_Home extends AppCompatActivity {
    GridView gvCatagory;
    CatagoryAdapter catagoryAdapter;
    ArrayList<CatagoryItem> arr = new ArrayList<>();
    private FirebaseDatabase database;
    private DatabaseReference categoriesRef;
    private ImageButton btnAcc;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_home);
        catagoryAdapter = new CatagoryAdapter(this, R.layout.layout_item_catagory, arr);
        gvCatagory = findViewById(R.id.gvDishList);
        btnAcc = findViewById(R.id.btnAcount);
        gvCatagory.setAdapter(catagoryAdapter);

        loadCategories();

        // Khi người dùng nhấn nút tài khoản
        btnAcc.setOnClickListener(view -> {
            // Lấy userId từ SharedPreferences
            SharedPreferences preferences = getSharedPreferences("MyAppPrefs", MODE_PRIVATE);
            String userId = preferences.getString("userId", null);

            if (userId != null) {
                Intent intent = new Intent(Activity_Home.this, UpdateInfor.class);
                intent.putExtra("userId", userId); // Truyền userId qua Intent
                startActivity(intent);
            } else {
                Toast.makeText(Activity_Home.this, "Không tìm thấy thông tin người dùng", Toast.LENGTH_SHORT).show();
            }
        });


        gvCatagory.setOnItemClickListener((parent, view, position, id) -> {
            CatagoryItem selectedCategory = arr.get(position);
            // Tạo Intent để chuyển sang Activity mới
            Intent intent = new Intent(Activity_Home.this, Activity_DishList.class);
            // Gửi dữ liệu qua Activity mới
            intent.putExtra("CategoryId", selectedCategory.getId());
            startActivity(intent);
        });

    }

    private void loadCategories() {
        database = FirebaseDatabase.getInstance();
        DatabaseReference categoriesRef = database.getReference("Categories");

        categoriesRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                arr.clear(); // Xóa dữ liệu cũ trước khi thêm dữ liệu mới
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    String categoryId = snapshot.getKey(); // Lấy id của danh mục
                    CatagoryItem category = snapshot.getValue(CatagoryItem.class);
                    if (category != null) {
                        category.setId(categoryId); // Gán id cho đối tượng CatagoryItem
                        arr.add(category);
                    }
                }
                catagoryAdapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Log.w("Firebase", "loadCategories:onCancelled", databaseError.toException());
            }
        });
    }


}

