package nhom12.eaut.cookinginstructions.Controller;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

import nhom12.eaut.cookinginstructions.Model.NhanSu;
import nhom12.eaut.cookinginstructions.NhanSuAdapter;
import nhom12.eaut.cookinginstructions.R;

public class DanhSachNhanSuActivity extends AppCompatActivity {

    private ListView listViewNhanSu;
    private ArrayList<NhanSu> nhanSuList;
    private NhanSuAdapter nhanSuAdapter;
    private DatabaseReference databaseReference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_danh_sach_nhan_su);

        listViewNhanSu = findViewById(R.id.listViewNhanSu);
        nhanSuList = new ArrayList<>();
        nhanSuAdapter = new NhanSuAdapter(this, nhanSuList);
        listViewNhanSu.setAdapter(nhanSuAdapter);

        // Initialize Firebase Database reference
        databaseReference = FirebaseDatabase.getInstance().getReference("NhanSu");

        // Query data from Firebase
        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                nhanSuList.clear();
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    NhanSu nhanSu = snapshot.getValue(NhanSu.class);
                    nhanSuList.add(nhanSu);
                }
                nhanSuAdapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                // Handle errors if necessary
            }
        });

        // Handle item click
        listViewNhanSu.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                NhanSu selectedNhanSu = nhanSuList.get(position);
                showDeleteConfirmationDialog(selectedNhanSu.getId());
            }
        });
    }

    private void showDeleteConfirmationDialog(String id) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        LayoutInflater inflater = getLayoutInflater();
        View dialogView = inflater.inflate(R.layout.dialog_confirm_delete, null);
        builder.setView(dialogView);

        TextView textViewMessage = dialogView.findViewById(R.id.textViewMessage);
        Button buttonCancel = dialogView.findViewById(R.id.buttonCancel);
        Button buttonConfirm = dialogView.findViewById(R.id.buttonConfirm);

        textViewMessage.setText("Bạn có chắc chắn muốn xóa nhân sự này?");

        final String itemId = id;
        final AlertDialog dialog = builder.create();

        buttonCancel.setOnClickListener(v -> dialog.dismiss());

        buttonConfirm.setOnClickListener(v -> {
            deleteNhanSu(itemId);
            dialog.dismiss();
        });

        dialog.show();
    }

    private void deleteNhanSu(String id) {
        databaseReference.child(id).removeValue()
                .addOnSuccessListener(aVoid -> {
                    // Notify user of success
                    Toast.makeText(DanhSachNhanSuActivity.this, "Xóa nhân sự thành công", Toast.LENGTH_SHORT).show();
                })
                .addOnFailureListener(e -> {
                    // Notify user of failure
                    Toast.makeText(DanhSachNhanSuActivity.this, "Xóa nhân sự thất bại: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                });
    }
}
