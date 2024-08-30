package nhom12.eaut.cookinginstructions;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import nhom12.eaut.cookinginstructions.Controller.DanhSachNhanSuActivity;
import nhom12.eaut.cookinginstructions.Model.NhanSu;

public class Activity_NhanSu extends AppCompatActivity {

    private TextInputEditText etName, etDob, etPosition, etPhone, etEmail;
    private Button btnSave, btnHienThiDanhSach;
    private DatabaseReference databaseReference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_nhan_su);

        // Initialize Firebase Database reference
        databaseReference = FirebaseDatabase.getInstance().getReference("NhanSu");

        // Find views
        etName = findViewById(R.id.etName);
        etDob = findViewById(R.id.etDob);
        etPosition = findViewById(R.id.etPosition);
        etPhone = findViewById(R.id.etPhone);
        etEmail = findViewById(R.id.etEmail);

        btnSave = findViewById(R.id.btnSave);
        btnHienThiDanhSach = findViewById(R.id.btnShowList);

        // Set click listener for "Save" button
        btnSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                saveNhanSu();
            }
        });

        // Set click listener for "Show List" button
        btnHienThiDanhSach.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Activity_NhanSu.this, DanhSachNhanSuActivity.class);
                startActivity(intent);
            }
        });
    }

    private void saveNhanSu() {
        String id = databaseReference.push().getKey();
        String hoTen = etName.getText().toString().trim();
        String ngaySinh = etDob.getText().toString().trim();
        String chucVu = etPosition.getText().toString().trim();
        String soDienThoai = etPhone.getText().toString().trim();
        String email = etEmail.getText().toString().trim();

        if (hoTen.isEmpty() || hoTen.length() < 2) {
            etName.setError("Họ tên phải có ít nhất 2 ký tự");
            return;
        }

        if (ngaySinh.isEmpty()) {
            etDob.setError("Vui lòng nhập ngày sinh");
            return;
        }

        if (chucVu.isEmpty()) {
            etPosition.setError("Vui lòng nhập chức vụ");
            return;
        }

        if (soDienThoai.isEmpty() || soDienThoai.length() < 10) {
            etPhone.setError("Số điện thoại phải có ít nhất 10 chữ số");
            return;
        }

        if (email.isEmpty() || !email.contains("@")) {
            etEmail.setError("Vui lòng nhập email hợp lệ");
            return;
        }

        NhanSu nhanSu = new NhanSu(id, hoTen, ngaySinh, chucVu, soDienThoai, email);

        databaseReference.child(id).setValue(nhanSu)
                .addOnSuccessListener(aVoid -> {
                    Toast.makeText(Activity_NhanSu.this, "Lưu thông tin thành công", Toast.LENGTH_SHORT).show();
                    clearInputs();
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(Activity_NhanSu.this, "Lưu thông tin thất bại: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                });
    }

    private void clearInputs() {
        etName.setText("");
        etDob.setText("");
        etPosition.setText("");
        etPhone.setText("");
        etEmail.setText("");
    }
}
