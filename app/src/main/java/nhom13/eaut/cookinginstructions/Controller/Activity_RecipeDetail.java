package nhom13.eaut.cookinginstructions.Controller;

import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.webkit.WebView;
import android.webkit.WebViewClient;
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

import nhom13.eaut.cookinginstructions.Model.Favorite;
import nhom13.eaut.cookinginstructions.Model.Recipe;
import nhom13.eaut.cookinginstructions.Model.Step;
import nhom13.eaut.cookinginstructions.R;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

public class Activity_RecipeDetail extends AppCompatActivity {
    private TextView tvIngredient, txtNameF, txtTitle, txtDesc, tvSteps, txtTitleVideo;
    private LinearLayout layoutSteps;
    private FirebaseDatabase database;
    private DatabaseReference recipeRef, favoriteRef;
    FloatingActionButton btnThoat;
    Button btnTym, btnShare;
    WebView webView;
    private String userId;
    private boolean isFavorite = false;  // Biến để theo dõi tình trạng yêu thích
    private String imageUrl;  // Lưu URL hình ảnh công thức

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
        txtTitleVideo = findViewById(R.id.textView5);
        btnShare = findViewById(R.id.btnShare);
        webView = findViewById(R.id.webView);

        // Lấy userId từ SharedPreferences
        userId = getSharedPreferences("MyAppPrefs", MODE_PRIVATE).getString("userId", null);

        String recipeId = getIntent().getStringExtra("RecipeId");
        loadRecipeDetails(recipeId);
        checkFavoriteStatus(recipeId);

        // Sự kiện click cho nút "Chia sẻ"
        btnShare.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                shareRecipe();
            }
        });

        btnThoat.setOnClickListener(v -> finish());
    }

    // Hàm chia sẻ công thức
    private void shareRecipe() {
        // Lấy nội dung cần chia sẻ
        String title = txtTitle.getText().toString();
        String description = txtDesc.getText().toString();
        String ingredients = tvIngredient.getText().toString();


        // Kiểm tra xem dữ liệu đã được load đầy đủ chưa
        if (title.isEmpty() || description.isEmpty() || ingredients.isEmpty()) {
            Toast.makeText(Activity_RecipeDetail.this, "Vui lòng đợi nội dung được tải đầy đủ", Toast.LENGTH_SHORT).show();
            return;
        }

        // Tạo nội dung chia sẻ
        String shareContent = title + "\n\n" + description + "\n\nNguyên liệu:\n" + ingredients + "\n\nCác bước làm:\n";

        // Nếu có hình ảnh, thêm vào nội dung chia sẻ
        if (imageUrl != null && !imageUrl.isEmpty()) {
            shareContent += "\n\nHình ảnh món ăn: " + imageUrl;
        }

        // Tạo Intent chia sẻ
        Intent shareIntent = new Intent(Intent.ACTION_SEND);
        shareIntent.setType("text/plain");
        shareIntent.putExtra(Intent.EXTRA_SUBJECT, "Chia sẻ công thức món ăn");
        shareIntent.putExtra(Intent.EXTRA_TEXT, shareContent);

        // Kiểm tra xem có ứng dụng nào hỗ trợ chia sẻ không
        if (shareIntent.resolveActivity(getPackageManager()) != null) {
            startActivity(Intent.createChooser(shareIntent, "Chia sẻ công thức qua"));
        } else {
            Toast.makeText(Activity_RecipeDetail.this, "Không có ứng dụng nào hỗ trợ chia sẻ", Toast.LENGTH_SHORT).show();
        }
    }

    // Hàm kiểm tra xem công thức đã có trong danh sách yêu thích chưa
    private void checkFavoriteStatus(String recipeId) {
        database = FirebaseDatabase.getInstance();
        favoriteRef = database.getReference("Favorites").child(userId).child(recipeId);

        favoriteRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    // Nếu đã có trong yêu thích
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

        ProgressDialog progressDialog = new ProgressDialog(Activity_RecipeDetail.this);
        progressDialog.setMessage("Đang tải...");
        progressDialog.show();

        recipeRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                progressDialog.dismiss();
                Recipe recipe = dataSnapshot.getValue(Recipe.class);
                    txtNameF.setText(recipe.getTitle());
                    txtDesc.setText(recipe.getDescription());
                    txtTitle.setText(recipe.getTitle());
                    imageUrl = recipe.getImg();  // Lưu URL hình ảnh để chia sẻ

                    if (recipe.getUrlVideo() != null && !recipe.getUrlVideo().isEmpty()) {
                        // Cấu hình WebView để hiển thị và phát video trong app
                        webView.getSettings().setJavaScriptEnabled(true);  // Kích hoạt JavaScript
                        webView.getSettings().setDomStorageEnabled(true);  // Kích hoạt DOM storage
                        webView.getSettings().setJavaScriptCanOpenWindowsAutomatically(true);  // Cho phép mở cửa sổ mới
                        webView.getSettings().setMediaPlaybackRequiresUserGesture(false);  // Cho phép phát media không cần thao tác người dùng

                        // Sử dụng WebViewClient để không mở ứng dụng bên ngoài
                        webView.setWebViewClient(new WebViewClient());

                        // Hiển thị video YouTube nhúng
                        String youtubeEmbedUrl = "https://www.youtube.com/embed/" + extractYouTubeVideoId(recipe.getUrlVideo());
                        webView.loadUrl(youtubeEmbedUrl);

                    }else {
                        // Ẩn tiêu đề video và WebView khi không có URL video
                        txtTitleVideo.setVisibility(View.GONE);
                        webView.setVisibility(View.GONE);
                    }

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

            // Hàm trích xuất ID video từ URL YouTube
            private String extractYouTubeVideoId(String videoUrl) {
                String videoId = "";
                String pattern = "^(https?://)?(www\\.)?(youtube\\.com|youtu\\.?be)/.+$";

                if (videoUrl.matches(pattern)) {
                    String[] parts = videoUrl.split("v=");
                    if (parts.length > 1) {
                        videoId = parts[1].split("&")[0];  // Trích xuất ID video
                    }
                }
                return videoId;
            }


            @Override
            public void onCancelled(DatabaseError databaseError) {
                progressDialog.dismiss();
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
            Toast.makeText(Activity_RecipeDetail.this, "Đã bỏ thích", Toast.LENGTH_SHORT).show();
            btnTym.setText("Yêu thích");
            isFavorite = false;  // Cập nhật trạng thái sau khi xóa
        }).addOnFailureListener(e -> {
            Toast.makeText(Activity_RecipeDetail.this, "Bỏ thích thất bại", Toast.LENGTH_SHORT).show();
        });
    }
}
