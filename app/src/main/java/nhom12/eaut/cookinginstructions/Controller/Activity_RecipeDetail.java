package nhom12.eaut.cookinginstructions.Controller;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.util.Log;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.ImageView;

import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import nhom12.eaut.cookinginstructions.Model.Recipe;
import nhom12.eaut.cookinginstructions.Model.Step;
import nhom12.eaut.cookinginstructions.R;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

public class Activity_RecipeDetail extends AppCompatActivity {
    private TextView tvIngredient, txtNameF, txtTitle, txtDesc, tvSteps;
    private LinearLayout layoutSteps;
    private FirebaseDatabase database;
    private DatabaseReference recipeRef;
    FloatingActionButton btnThoat;

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

        String recipeId = getIntent().getStringExtra("RecipeId");
        loadRecipeDetails(recipeId);

        btnThoat.setOnClickListener(v -> finish());
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
}
