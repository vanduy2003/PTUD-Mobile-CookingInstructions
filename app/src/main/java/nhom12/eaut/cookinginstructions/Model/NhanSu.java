package nhom12.eaut.cookinginstructions.Model;

public class NhanSu {
    private String id;
    private String hoTen;
    private String ngaySinh;
    private String chucVu;
    private String soDienThoai;
    private String email;

    // Constructor không tham số (bắt buộc cho Firebase)
    public NhanSu() {}

    // Constructor đầy đủ
    public NhanSu(String id, String hoTen, String ngaySinh, String chucVu, String soDienThoai, String email) {
        this.id = id;
        this.hoTen = hoTen;
        this.ngaySinh = ngaySinh;
        this.chucVu = chucVu;
        this.soDienThoai = soDienThoai;
        this.email = email;
    }

    // Getter và Setter
    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getHoTen() {
        return hoTen;
    }

    public void setHoTen(String hoTen) {
        this.hoTen = hoTen;
    }

    public String getNgaySinh() {
        return ngaySinh;
    }

    public void setNgaySinh(String ngaySinh) {
        this.ngaySinh = ngaySinh;
    }

    public String getChucVu() {
        return chucVu;
    }

    public void setChucVu(String chucVu) {
        this.chucVu = chucVu;
    }

    public String getSoDienThoai() {
        return soDienThoai;
    }

    public void setSoDienThoai(String soDienThoai) {
        this.soDienThoai = soDienThoai;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }
}

