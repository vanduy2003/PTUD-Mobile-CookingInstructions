package nhom12.eaut.cookinginstructions;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import android.widget.ArrayAdapter;
import java.util.ArrayList;

import nhom12.eaut.cookinginstructions.Model.NhanSu;

public class NhanSuAdapter extends ArrayAdapter<NhanSu> {

    private Context context;
    private ArrayList<NhanSu> nhanSuList;

    public NhanSuAdapter(Context context, ArrayList<NhanSu> list) {
        super(context, 0, list);
        this.context = context;
        this.nhanSuList = list;
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        if (convertView == null) {
            convertView = LayoutInflater.from(context).inflate(R.layout.nhan_su_item, parent, false);
        }

        NhanSu currentNhanSu = nhanSuList.get(position);

        TextView hoTenTextView = convertView.findViewById(R.id.textViewHoTen);
        TextView chucVuTextView = convertView.findViewById(R.id.textViewChucVu);

        hoTenTextView.setText(currentNhanSu.getHoTen());
        chucVuTextView.setText(currentNhanSu.getChucVu());

        return convertView;
    }
}