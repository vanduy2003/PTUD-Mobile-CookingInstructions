package nhom12.eaut.cookinginstructions;

import static android.app.ProgressDialog.show;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import nhom12.eaut.cookinginstructions.Controller.Activity_Home;
import nhom12.eaut.cookinginstructions.Controller.Activity_Register;

public class MainActivity extends AppCompatActivity {

    private EditText emailEditText, passwordEditText;
    private Button loginButton;
    private FirebaseDatabase database;
    private DatabaseReference usersRef;
    private TextView signupText;

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Khởi tạo Firebase Database
        database = FirebaseDatabase.getInstance();
        usersRef = database.getReference("Users");

        // Liên kết UI elements
        emailEditText = findViewById(R.id.emailEditText);
        passwordEditText = findViewById(R.id.passwordEditText);
        loginButton = findViewById(R.id.loginButton);
        signupText = findViewById(R.id.signupText);

        // Xử lý sự kiện khi nhấn nút đăng nhập
        loginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String email = emailEditText.getText().toString();
                String password = passwordEditText.getText().toString();
                loginUser(email, password);
            }
        });


        // Xử lý sự kiện khi nhấn chữ "Đăng ký"
        signupText.setOnClickListener(v -> {
            // Chuyển đến màn hình đăng ký
             Intent intent = new Intent(MainActivity.this, Activity_Register.class);
             startActivity(intent);
        });

        Intent intent = getIntent();
        String email = intent.getStringExtra("email");
        String password = intent.getStringExtra("password");

        if (email != null && password != null) {
            emailEditText.setText(email);
            passwordEditText.setText(password);

        }
    }

    private void loginUser(String email, String password) {
        // Truy vấn dữ liệu trong bảng Users
        usersRef.orderByChild("email").equalTo(email)
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        if (dataSnapshot.exists()) {
                            // Lặp qua kết quả để kiểm tra mật khẩu
                            for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                                String storedPassword = snapshot.child("password").getValue(String.class);
                                if (storedPassword != null && storedPassword.equals(password)) {
                                    // Đăng nhập thành công
                                    String uid = snapshot.getKey(); // Lấy UID của người dùng
                                    saveUserId(uid); // Lưu UID vào SharedPreferences
                                    Toast.makeText(MainActivity.this, "Đăng nhập thành công!", Toast.LENGTH_SHORT).show();

                                    // Chuyển đến màn hình chính
                                    Intent intent = new Intent(MainActivity.this, Activity_Home.class);
                                    startActivity(intent);

                                } else {
                                    // Mật khẩu không đúng
                                    Toast.makeText(MainActivity.this, "Sai mật khẩu!", Toast.LENGTH_SHORT).show();
                                }
                            }
                        } else {
                            // Email không tồn tại
                            Toast.makeText(MainActivity.this, "Email không tồn tại!", Toast.LENGTH_SHORT).show();
                        }
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {
                        Log.w("Firebase", "loadUser:onCancelled", databaseError.toException());
                    }
                });
    }

    private void saveUserId(String uid) {
        SharedPreferences preferences = getSharedPreferences("MyAppPrefs", MODE_PRIVATE);
        SharedPreferences.Editor editor = preferences.edit();
        editor.putString("userId", uid);
        editor.apply();
    }

    @SuppressLint("MissingSuperCall")
    @Override
    public void onBackPressed () {
        // Tao Dialog xac nhan thoat
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Xác nhận");
        builder.setMessage("Bạn có muốn thoát ứng dụng không?");
        builder.setIcon(R.mipmap.cooking);
        builder.setPositiveButton("Có", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                finish();
            }
        });
        builder.setNegativeButton("Không", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
            }
        });
        builder.create().show();
    }
}