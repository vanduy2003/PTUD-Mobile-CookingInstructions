package nhom12.eaut.cookinginstructions.Controller;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;


import nhom12.eaut.cookinginstructions.Model.Favorite;
import nhom12.eaut.cookinginstructions.Model.Recipe;
import nhom12.eaut.cookinginstructions.Model.Step;
import nhom12.eaut.cookinginstructions.R;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

public class Activity_RecipeDetail extends AppCompatActivity {
    private TextView tvIngredient, txtNameF, txtTitle, txtDesc, tvSteps;
    private LinearLayout layoutSteps;
    private FirebaseDatabase database;
    private DatabaseReference recipeRef, favoriteRef;
    FloatingActionButton btnThoat;
    Button btnTym;
    private String userId;
    private boolean isFavorite = false;  // Biến để theo dõi tình trạng yêu thích

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_recipe_detail);

        tvIngredient = findViewById(R.id.tvIngredient);
        tvSteps = findViewById(R.id.tvSteps);
        layoutSteps = findViewById(R.id.layoutSteps);
        txtNameF = findViewById(R.id.txtNameF);
        btnThoat = findViewById(R.id.btnThoat);
        txtDesc = findViewById(R.id.txtDesc);
        txtTitle = findViewById(R.id.txtTitle);
        btnTym = findViewById(R.id.btnTym);

        // Lấy userId từ SharedPreferences
        userId = getSharedPreferences("MyAppPrefs", MODE_PRIVATE).getString("userId", null);

        String recipeId = getIntent().getStringExtra("RecipeId");
        loadRecipeDetails(recipeId);
        checkFavoriteStatus(recipeId);

        btnThoat.setOnClickListener(v -> finish());
    }

    // Hàm kiểm tra xem công thức đã có trong danh sách yêu thích chưa
    private void checkFavoriteStatus(String recipeId) {
        database = FirebaseDatabase.getInstance();
        favoriteRef = database.getReference("Favorites").child(userId).child(recipeId);

        favoriteRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    // Nếu đã có trong yêu thích, đổi nút thành "Bỏ yêu thích"
                    btnTym.setText("Bỏ thích");
                    isFavorite = true;  // Đánh dấu là đã yêu thích
                } else {
                    // Nếu chưa có, nút sẽ hiển thị "Thêm vào yêu thích"
                    btnTym.setText("Yêu thích");
                    isFavorite = false; // Đánh dấu là chưa yêu thích
                }

                // Gán sự kiện click cho btnTym sau khi kiểm tra trạng thái
                btnTym.setOnClickListener(v -> {
                    if (isFavorite) {
                        removeRecipeFromFavorites(recipeId);  // Xóa khỏi danh sách yêu thích
                    } else {
                        addRecipeToFavorites(recipeId);  // Thêm vào danh sách yêu thích
                    }
                });
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Log.w("Firebase", "checkFavoriteStatus:onCancelled", databaseError.toException());
            }
        });
    }

    private void loadRecipeDetails(String recipeId) {
        database = FirebaseDatabase.getInstance();
        recipeRef = database.getReference("Recipes").child(recipeId);

        recipeRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                Recipe recipe = dataSnapshot.getValue(Recipe.class);
                if (recipe != null) {
                    txtNameF.setText(recipe.getTitle());
                    txtDesc.setText(recipe.getDescription());
                    txtTitle.setText(recipe.getTitle());

                    // Hiển thị nguyên liệu
                    StringBuilder ingredients = new StringBuilder();
                    for (String item : recipe.getIngredients()) {
                        ingredients.append("  - ").append(item).append("\n");
                    }
                    tvIngredient.setText(ingredients.toString());

                    // Hiển thị các bước
                    layoutSteps.removeAllViews();
                    for (Step step : recipe.getSteps()) {
                        TextView stepTextView = new TextView(Activity_RecipeDetail.this);
                        stepTextView.setText("Bước " + step.getStepNumber() + ": " + "\n" + " - " + step.getDescription());
                        stepTextView.setTextSize(22);
                        stepTextView.setPadding(0, 20, 0, 20);

                        ImageView stepImageView = new ImageView(Activity_RecipeDetail.this);
                        stepImageView.setLayoutParams(new LinearLayout.LayoutParams(
                                LinearLayout.LayoutParams.MATCH_PARENT,
                                500
                        ));
                        stepImageView.setScaleType(ImageView.ScaleType.CENTER_CROP);

                        Glide.with(Activity_RecipeDetail.this)
                                .load(step.getImageUrl())
                                .into(stepImageView);

                        layoutSteps.addView(stepTextView);
                        layoutSteps.addView(stepImageView);
                    }
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Log.w("Firebase", "loadRecipeDetails:onCancelled", databaseError.toException());
            }
        });
    }

    private void addRecipeToFavorites(String recipeId) {
        recipeRef = database.getReference("Recipes").child(recipeId);
        favoriteRef = database.getReference("Favorites").child(userId).child(recipeId);

        recipeRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                Recipe recipe = dataSnapshot.getValue(Recipe.class);
                if (recipe != null) {
                    Favorite favoriteRecipe = new Favorite(
                            recipeId,
                            recipe.getTitle(),
                            recipe.getImg()
                    );

                    favoriteRef.setValue(favoriteRecipe).addOnSuccessListener(aVoid -> {
                        Toast.makeText(Activity_RecipeDetail.this, "Đã thêm vào món yêu thích", Toast.LENGTH_SHORT).show();
                        btnTym.setText("Bỏ thích");
                        isFavorite = true;  // Cập nhật trạng thái sau khi thêm
                    }).addOnFailureListener(e -> {
                        Toast.makeText(Activity_RecipeDetail.this, "Thêm vào món yêu thích thất bại", Toast.LENGTH_SHORT).show();
                    });
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Log.w("Firebase", "addRecipeToFavorites:onCancelled", databaseError.toException());
            }
        });
    }

    private void removeRecipeFromFavorites(String recipeId) {
        favoriteRef = database.getReference("Favorites").child(userId).child(recipeId);

        favoriteRef.removeValue().addOnSuccessListener(aVoid -> {
            Toast.makeText(Activity_RecipeDetail.this, "Đã xóa khỏi món yêu thích", Toast.LENGTH_SHORT).show();
            btnTym.setText("Yêu thích");
            isFavorite = false;  // Cập nhật trạng thái sau khi xóa
        }).addOnFailureListener(e -> {
            Toast.makeText(Activity_RecipeDetail.this, "Xóa khỏi món yêu thích thất bại", Toast.LENGTH_SHORT).show();
        });
    }

}