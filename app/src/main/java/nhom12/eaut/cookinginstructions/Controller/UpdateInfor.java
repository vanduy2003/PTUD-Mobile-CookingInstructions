package nhom12.eaut.cookinginstructions.Controller;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Patterns;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowCompat;
import androidx.core.view.WindowInsetsCompat;

import com.bumptech.glide.Glide;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.database.DataSnapshot;

import nhom12.eaut.cookinginstructions.R;

public class UpdateInfor extends AppCompatActivity {

    private EditText emailEditText, phoneEditText, passwordEditText, addressEditText, usernameEditText, dobEditText, urlImg;
    private ImageView avatarImageView;
    private DatabaseReference mDatabase;
    private String userId;
    private String avatarUrl; // Thay đổi: Thêm biến để lưu URL ảnh đại diện
    FloatingActionButton btnThoat;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Đặt chế độ hiển thị để nội dung ứng dụng tràn viền màn hình
        WindowCompat.setDecorFitsSystemWindows(getWindow(), false);

        // Đặt nội dung giao diện
        setContentView(R.layout.activity_update_infor);

        // Ánh xạ các trường từ XML
        emailEditText = findViewById(R.id.emailEditText);
        phoneEditText = findViewById(R.id.phoneEditText);
        passwordEditText = findViewById(R.id.passwordEditText);
        addressEditText = findViewById(R.id.addressEditText);
        usernameEditText = findViewById(R.id.usernameEditText);
        dobEditText = findViewById(R.id.dobEditText);
        avatarImageView = findViewById(R.id.avatarImageView);
        urlImg = findViewById(R.id.avatarUrlEditText);
        // Initialize Database
        mDatabase = FirebaseDatabase.getInstance().getReference();

        // Nhận userId từ Intent
        Intent intent = getIntent();
        userId = intent.getStringExtra("userId");

        if (userId == null) {
            Toast.makeText(this, "Không có ID người dùng", Toast.LENGTH_SHORT).show();
            finish(); // Kết thúc Activity nếu không có userId
            return;
        }

        // Thêm các TextWatcher để kiểm tra dữ liệu nhập vào
        addTextWatchers();

        // Xử lý giao diện để tràn ra viền màn hình, padding theo viền hệ thống
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        // Hiển thị thông tin người dùng sau khi đăng nhập
        loadUserInfo();

        // Xử lý nút lưu
        Button saveButton = findViewById(R.id.saveButton);
        saveButton.setOnClickListener(v -> saveUserInfo());
        btnThoat = findViewById(R.id.btnThoat);
        btnThoat.setOnClickListener(v -> {
            finish();
        });
    }

    private void loadUserInfo() {
        if (userId != null) {
            mDatabase.child("Users").child(userId).addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot snapshot) {
                    if (snapshot.exists()) {
                        // Lấy thông tin từ snapshot
                        String urlAvatar = snapshot.child("avatar").getValue(String.class);
                        String email = snapshot.child("email").getValue(String.class);
                        String phone = snapshot.child("phone").getValue(String.class);
                        String username = snapshot.child("username").getValue(String.class);
                        String dob = snapshot.child("dob").getValue(String.class);
                        String address = snapshot.child("address").getValue(String.class);
                        avatarUrl = snapshot.child("avatar").getValue(String.class); // Thay đổi: Lấy URL ảnh đại diện

                        // Hiển thị thông tin lên giao diện
                        emailEditText.setText(email);
                        phoneEditText.setText(phone);
                        usernameEditText.setText(username);
                        dobEditText.setText(dob);
                        addressEditText.setText(address);
                        urlImg.setText(urlAvatar);

                        // Để tải ảnh đại diện từ URL, bạn có thể dùng thư viện như Glide hoặc Picasso
                        if (avatarUrl != null && !avatarUrl.isEmpty()) {
                            Glide.with(UpdateInfor.this)
                                    .load(avatarUrl)
                                    .into(avatarImageView);
                        }
                    } else {
                        Toast.makeText(UpdateInfor.this, "Không tìm thấy thông tin người dùng", Toast.LENGTH_SHORT).show();
                    }
                }

                @Override
                public void onCancelled(DatabaseError error) {
                    Toast.makeText(UpdateInfor.this, "Không thể lấy thông tin người dùng", Toast.LENGTH_SHORT).show();
                }
            });
        } else {
            Toast.makeText(this, "Người dùng chưa đăng nhập", Toast.LENGTH_SHORT).show();
        }
    }

    private void saveUserInfo() {
        String newPhone = phoneEditText.getText().toString().trim();
        String newUsername = usernameEditText.getText().toString().trim();
        String newDob = dobEditText.getText().toString().trim();
        String newAddress = addressEditText.getText().toString().trim();
        String newPassword = passwordEditText.getText().toString().trim();
        // Lưu URL ảnh đại diện mới nếu có
        String newAvatarUrl = urlImg.getText().toString().trim(); // Giữ URL hiện tại nếu không thay đổi
        if (userId != null) {
            DatabaseReference userRef = mDatabase.child("Users").child(userId);

            // Cập nhật thông tin khác lên Firebase
            userRef.child("phone").setValue(newPhone);
            userRef.child("username").setValue(newUsername);
            userRef.child("dob").setValue(newDob);
            userRef.child("address").setValue(newAddress);

            // Cập nhật URL ảnh đại diện
            if (newAvatarUrl != null ) {
                userRef.child("avatar").setValue(newAvatarUrl);
            }

            // Cập nhật mật khẩu
            if (newPassword != null && !newPassword.isEmpty()) {
                userRef.child("password").setValue(newPassword);
            }

            Toast.makeText(UpdateInfor.this, "Thông tin đã được cập nhật", Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(this, "Người dùng chưa đăng nhập", Toast.LENGTH_SHORT).show();
        }
    }

    private void addTextWatchers() {
        // Validate Email
        emailEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                String emailInput = emailEditText.getText().toString().trim();
                if (!Patterns.EMAIL_ADDRESS.matcher(emailInput).matches()) {
                    emailEditText.setError("Email không hợp lệ");
                } else {
                    emailEditText.setError(null);
                }
            }

            @Override
            public void afterTextChanged(Editable s) {}
        });

        // Validate Số Điện Thoại
        phoneEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                String phoneInput = phoneEditText.getText().toString().trim();
                if (!Patterns.PHONE.matcher(phoneInput).matches()) {
                    phoneEditText.setError("Số điện thoại không hợp lệ");
                } else {
                    phoneEditText.setError(null);
                }
            }

            @Override
            public void afterTextChanged(Editable s) {}
        });

        // Validate Mật Khẩu
        passwordEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                String passwordInput = passwordEditText.getText().toString().trim();
                if (passwordInput.length() < 6) {
                    passwordEditText.setError("Mật khẩu phải có ít nhất 6 ký tự");
                } else {
                    passwordEditText.setError(null);
                }
            }

            @Override
            public void afterTextChanged(Editable s) {}
        });

        // Validate Địa Chỉ
        addressEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                String addressInput = addressEditText.getText().toString().trim();
                if (addressInput.isEmpty()) {
                    addressEditText.setError("Địa chỉ không được để trống");
                } else {
                    addressEditText.setError(null);
                }
            }

            @Override
            public void afterTextChanged(Editable s) {}
        });
    }
}
