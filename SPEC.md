# ĐẶC TẢ YÊU CẦU HỆ THỐNG

## Phần mềm Quản lý Hợp tác Khoa học Công nghệ và Quan hệ Quốc tế

**Trường Đại học Kiên Giang — Phòng Hợp tác Khoa học Công nghệ và Quan hệ Quốc tế (P.HTKHCN)**

Phiên bản: 2.0 (Markdown, giai đoạn Web) — Ngày cập nhật: 06/08/2026
Nguồn dữ liệu tham chiếu: `VB ĐHKG_P.HTKHCN.xlsx`

> Phạm vi phiên bản này: **chỉ web**, dùng để triển khai giai đoạn 1. Ứng dụng desktop/di động không nằm trong phạm vi tài liệu này — sẽ thiết kế và xây dựng riêng ở giai đoạn sau (dự kiến dùng Claude Code), gọi lại cùng bộ API đã có.

---

## Mục lục

1. [Giới thiệu](#1-giới-thiệu)
2. [Người dùng và phân quyền](#2-người-dùng-và-phân-quyền)
3. [Các module quản lý dữ liệu](#3-các-module-quản-lý-dữ-liệu)
4. [Chức năng dùng chung](#4-chức-năng-dùng-chung-cho-toàn-hệ-thống)
5. [Yêu cầu phi chức năng](#5-yêu-cầu-phi-chức-năng)
6. [Kiến trúc và công nghệ](#6-kiến-trúc-và-công-nghệ)
7. [Triển khai trên Rocky Linux (ML110)](#7-triển-khai-trên-máy-chủ-rocky-linux-hp-ml110)
8. [Kế hoạch import dữ liệu ban đầu](#8-kế-hoạch-import-dữ-liệu-ban-đầu-từ-file-excel-hiện-có)
9. [Lộ trình triển khai](#9-lộ-trình-triển-khai-đề-xuất)
10. [Rủi ro và khuyến nghị](#10-rủi-ro-và-khuyến-nghị)

---

## 1. Giới thiệu

### 1.1. Bối cảnh

Phòng Hợp tác Khoa học Công nghệ và Quan hệ Quốc tế (P.HTKHCN) hiện đang quản lý dữ liệu bằng một file Excel dùng chung (`VB ĐHKG_P.HTKHCN.xlsx`), gồm 5 sheet: văn bản nội bộ do Trường ban hành, văn bản pháp luật Việt Nam liên quan, các thoả thuận hợp tác (MoU), đoàn khách nước ngoài đến làm việc (đoàn vào), và đoàn cán bộ của Trường đi công tác nước ngoài (đoàn ra).

Cách làm việc này có các hạn chế: nhiều người cùng sửa một file dễ gây ghi đè/mất dữ liệu, không kiểm soát được ai nhập/sửa gì, khó tìm kiếm - lọc - thống kê, không cảnh báo được MoU sắp hết hạn, tên đối tác bị gõ lặp lại không nhất quán giữa các sheet (khó gộp thống kê theo đối tác), và không phân quyền xem/sửa theo vai trò.

### 1.2. Mục tiêu

- Số hoá 5 nhóm dữ liệu hiện có trong Excel, cộng thêm 1 module **Đối tác** dùng chung mới, thành 6 module quản lý riêng biệt trên cùng một hệ thống.
- Cho phép nhiều cán bộ cùng nhập liệu đồng thời qua web, có tài khoản/mật khẩu riêng.
- Ghi nhận lịch sử: ai tạo, ai sửa, sửa lúc nào, nội dung gì đã thay đổi.
- Nhập được dữ liệu lịch sử đã có sẵn trong file Excel bằng chức năng import.
- Tìm kiếm, lọc, xuất báo cáo (Excel/PDF) nhanh chóng thay vì dò tay trong Excel.
- Cảnh báo MoU sắp hết hạn (web + email) để chủ động gia hạn/thanh lý.
- Thống kê theo từng đối tác xuyên suốt cả MoU/đoàn vào/đoàn ra, nhờ tách riêng danh mục Đối tác dùng chung.
- Triển khai trên máy chủ Rocky Linux (HP ML110) hiện có, không phụ thuộc dịch vụ cloud bên ngoài.
- Kiến trúc tách backend/frontend qua REST API, để nếu sau này bổ sung ứng dụng khác (desktop, di động — dự kiến nhờ Claude Code viết) thì chỉ cần gọi lại API sẵn có, không phải sửa backend.

### 1.3. Phạm vi

Phạm vi tài liệu này (giai đoạn 1 — **chỉ Web**) gồm: 6 module dữ liệu, đăng nhập/phân quyền, nhập liệu thủ công, import dữ liệu từ Excel, tìm kiếm/lọc, xuất báo cáo, dashboard thống kê, cảnh báo MoU qua web + email, và 3 báo cáo thống kê chuyên đề.

**Không thuộc phạm vi** tài liệu này: ứng dụng desktop (notifier/tray app), ứng dụng di động, chữ ký số, quy trình ký duyệt điện tử, tích hợp email nội bộ trường ở mức sâu hơn gửi thông báo đơn thuần. Các phần này để ngỏ cho giai đoạn phát triển sau, và vì kiến trúc đã tách API riêng (Mục 6.1), việc bổ sung sau này không đòi hỏi sửa lại phần đã xây.

---

## 2. Người dùng và phân quyền

Hệ thống dùng cơ chế tài khoản/mật khẩu, với 3 nhóm quyền (roles). Một tài khoản có thể được gán một hoặc nhiều vai trò; Admin có thể tạo thêm nhóm quyền tuỳ chỉnh nếu cần (ví dụ giới hạn một cán bộ chỉ được nhập module "Đoàn vào").

| Vai trò | Mô tả | Quyền hạn chính |
|---|---|---|
| **Quản trị viên (Admin)** | Trưởng/phó phòng P.HTKHCN hoặc phụ trách IT | Toàn quyền: quản lý tài khoản, phân quyền, cấu hình danh mục, xem lịch sử chỉnh sửa toàn hệ thống, sao lưu/khôi phục. |
| **Biên tập viên (Editor)** | Cán bộ P.HTKHCN và đơn vị phối hợp được cấp quyền nhập liệu | Thêm/sửa dữ liệu ở (các) module được phân công; tự sửa/xoá bản ghi do mình tạo trong thời hạn quy định; import Excel; xuất báo cáo. |
| **Người xem (Viewer)** | Ban Giám hiệu, lãnh đạo phòng/khoa liên quan | Chỉ xem, tìm kiếm, lọc, xuất báo cáo; không thêm/sửa/xoá. |

Có thể phân quyền Editor theo từng module (VD: cán bộ lễ tân chỉ nhập "Đoàn vào", cán bộ pháp chế chỉ nhập "Văn bản") để tránh nhập nhầm phần không phụ trách.

---

## 3. Các module quản lý dữ liệu

6 module dữ liệu: 5 module tương ứng 1:1 với 5 sheet Excel hiện tại (giữ nguyên tên trường để cán bộ dễ đối chiếu), cộng module mới **Đối tác** — danh mục dùng chung cho MoU, Đoàn vào, Đoàn ra. Mỗi bản ghi ở mọi module tự động có thêm 4 trường ẩn phục vụ truy vết: Người tạo, Ngày tạo, Người sửa gần nhất, Ngày sửa gần nhất — người dùng không cần nhập, hệ thống tự ghi (xem cách triển khai ở Mục 6.4).

### 3.1. Module "Đối tác" — Danh mục đơn vị/tổ chức hợp tác (dùng chung)

Module mới theo yêu cầu bổ sung: tách riêng thông tin đối tác thành một danh mục dùng chung, thay vì gõ tên đối tác tự do lặp lại ở từng MoU/Đoàn vào/Đoàn ra như hiện tại trong Excel. Module MoU, Đoàn vào, Đoàn ra sẽ **liên kết (tham chiếu)** đến đúng 1 bản ghi Đối tác duy nhất, nhờ vậy khi thống kê theo đối tác chỉ cần gộp theo Đối tác đó thay vì dò tìm bằng tên chữ dễ sai lệch chính tả (VD: "Đại học Hoàng Gia" và "ĐH Hoàng Gia" hiện đang bị tính thành 2 đối tác khác nhau nếu chỉ gõ tay).

| Trường dữ liệu | Kiểu dữ liệu | Bắt buộc | Ghi chú |
|---|---|---|---|
| Tên đối tác | Chuỗi ký tự | Có | Kiểm tra trùng/gần giống khi thêm mới (dùng `pg_trgm`, xem Mục 6.4) để tránh tạo 2 bản ghi cho cùng 1 đối tác. |
| Loại đối tác | Danh mục (Trong nước / Ngoài nước) | Có | Chính là trường chưa có tiêu đề ở cột A của sheet MoU trong file Excel gốc — dữ liệu chỉ có 2 giá trị "Trong nước"/"Ngoài nước", nay đưa hẳn vào làm 1 trường chính danh. |
| Quốc gia | Chuỗi ký tự / danh mục | Không | Bắt buộc chọn khi Loại đối tác = Ngoài nước. |
| Địa chỉ | Văn bản dài | Không | Địa chỉ liên hệ của đối tác. |
| Thông tin liên hệ | Chuỗi ký tự | Không | Website, email, số điện thoại đầu mối phía đối tác (nếu có). |
| Ghi chú | Văn bản dài | Không | Lịch sử quan hệ, bối cảnh hợp tác chung... |

Mỗi bản ghi Đối tác có một **"Hồ sơ đối tác"** tổng hợp: liệt kê toàn bộ MoU đã ký, toàn bộ đoàn vào và đoàn ra liên quan qua các năm — xem được bức tranh quan hệ hợp tác đầy đủ mà không phải dò từng module riêng lẻ. Khi nhập MoU/Đoàn vào/Đoàn ra, nếu đối tác chưa có trong danh mục, người dùng có thể tạo nhanh ngay trong màn hình nhập liệu.

### 3.2. Module "Văn bản ĐHKG" — Văn bản nội bộ do Trường ban hành

Danh sách quyết định, quy định, quy trình nội bộ do Trường ban hành liên quan đến hợp tác quốc tế.

| Trường dữ liệu | Kiểu dữ liệu | Bắt buộc | Ghi chú |
|---|---|---|---|
| STT | Số nguyên, tự sinh | Tự động | Đánh số thứ tự hiển thị. |
| Số hiệu | Chuỗi ký tự | Có | VD: 553/QĐ-ĐHKG. Ràng buộc `UNIQUE` ở tầng DB để chặn trùng ngay từ gốc. |
| Tên văn bản | Văn bản dài | Có | Tiêu đề đầy đủ. |
| Loại văn bản | Danh mục (ENUM Postgres) | Có | Quyết định, Nghị quyết... — danh mục mở rộng được bởi Admin. |
| Ngày ban hành | Ngày (date picker) | Có | |
| Ngày có hiệu lực | Ngày (date picker) | Không | |
| Tình trạng hiệu lực | Danh mục (ENUM Postgres) | Có | Còn hiệu lực / Hết hiệu lực toàn bộ / Hết hiệu lực một phần / Bị thay thế / Đã bị bãi bỏ. |
| Cơ quan ban hành | Chuỗi ký tự / danh mục | Có | VD: ĐHKG. |
| Ghi chú | Văn bản dài | Không | Căn cứ pháp lý hết hiệu lực, ghi chú rà soát... |
| Nội dung chính | Văn bản dài / rich text | Không | Tóm tắt các mục, chương, điều. |
| File đính kèm | Tệp tin (PDF/ảnh) | Không | Bản scan gốc, cho phép nhiều tệp. |

### 3.3. Module "VBPL VN" — Văn bản pháp luật Việt Nam liên quan

Cấu trúc dữ liệu giống Mục 3.2 (Số hiệu, Tên văn bản, Loại văn bản, Ngày ban hành, Ngày hiệu lực, Tình trạng hiệu lực, Cơ quan ban hành, Ghi chú, Nội dung chính, File đính kèm). Riêng "Loại văn bản" ở nhóm này có thêm các giá trị: Luật, Pháp lệnh, Nghị định, Thông tư, Thông tư liên tịch, Công văn.

**Đối chiếu với Cơ sở dữ liệu quốc gia (vbpl.vn):** mỗi bản ghi có nút "Tra cứu trên vbpl.vn" mở sẵn kết quả tìm kiếm theo đúng Số hiệu văn bản trên Cơ sở dữ liệu quốc gia về pháp luật (vbpl.vn, do Bộ Tư pháp quản lý). vbpl.vn không công bố API công khai cho bên thứ ba (robots.txt chặn `/api/`), nên hệ thống chỉ hỗ trợ liên kết tra cứu nhanh; việc cập nhật "Tình trạng hiệu lực" vẫn do con người xác nhận và nhập tay — không tự động ghi đè để tránh sai sót pháp lý. Hệ thống ghi thêm trường ẩn "Ngày đối chiếu gần nhất" mỗi khi cán bộ xác nhận đã rà soát.

### 3.4. Module "MoU" — Thoả thuận hợp tác (Memorandum of Understanding)

Module quan trọng nhất vì cần theo dõi hạn hiệu lực để cảnh báo gia hạn.

| Trường dữ liệu | Kiểu dữ liệu | Bắt buộc | Ghi chú |
|---|---|---|---|
| Đối tác | Liên kết (chọn từ module Đối tác, Mục 3.1) | Có | Thay cho gõ tự do "Tên đối tác" + "Địa chỉ" như Excel gốc; Loại đối tác và Địa chỉ lấy tự động từ hồ sơ Đối tác đã chọn. |
| Tên tài liệu | Chuỗi ký tự | Không | Tên văn bản MoU/thoả thuận. |
| Ngày ban hành | Ngày (date picker) | Có | Ngày ký kết. |
| Ngày hết hạn | Ngày (date picker) | Không | Dùng để tính cảnh báo sắp hết hạn (Mục 4.7). |
| Cá nhân/đơn vị đầu mối | Chuỗi ký tự | Không | Đơn vị phía ĐHKG phụ trách theo dõi. |
| Đơn vị thực hiện | Chuỗi ký tự | Không | Đơn vị trực tiếp triển khai chương trình hợp tác. |
| Phạm vi hợp tác | Danh mục (ENUM) | Không | Toàn diện (xã giao) / Theo lĩnh vực cụ thể. |
| Lĩnh vực hợp tác | Văn bản dài | Không | Mô tả các lĩnh vực hợp tác cụ thể. |
| Đầu mối ghi trong MoU | Chuỗi ký tự | Không | Người đại diện phía đối tác. |
| Đại diện KGU ký | Chuỗi ký tự | Không | Người đại diện Trường ký kết. |
| Thời hạn hiệu lực | Chuỗi ký tự (mô tả) | Không | VD: "5 năm, tự động gia hạn thêm 5 năm" — để dạng mô tả vì không phải lúc nào cũng là con số thuần. |
| Số công văn | Chuỗi ký tự | Không | |
| File đính kèm | Tệp tin (PDF/ảnh) | Không | Bản scan MoU đã ký. |

Trường tính toán tự động (không nhập tay): **"Trạng thái"** = Còn hiệu lực / Sắp hết hạn (trong vòng 90 ngày, cấu hình được) / Đã hết hạn — suy ra từ Ngày hết hạn, hiển thị bằng màu (xanh/vàng/đỏ). Vì phụ thuộc ngày hiện tại, trường này triển khai bằng 1 **VIEW** ở Postgres (không dùng generated column vì `CURRENT_DATE` không phải biểu thức immutable) — xem Mục 6.4.

### 3.5. Module "Đoàn vào" — Đoàn khách nước ngoài đến làm việc

| Trường dữ liệu | Kiểu dữ liệu | Bắt buộc | Ghi chú |
|---|---|---|---|
| Năm | Số nguyên | Tự động | Generated column: `EXTRACT(YEAR FROM thoi_gian_den)`. |
| Tên đoàn | Chuỗi ký tự | Có | |
| Đối tác liên quan | Liên kết (chọn từ module Đối tác, Mục 3.1) | Không | Không bắt buộc vì có đoàn khách không gắn với 1 tổ chức cụ thể đã ký MoU; nếu chọn, dùng để gộp thống kê theo đối tác. |
| Thời gian đến | Ngày (date picker) | Có | |
| Thời gian đi | Ngày (date picker) | Có | |
| Số lượng người nước ngoài | Số nguyên | Có | |
| Số lượng người Việt Nam | Số nguyên | Không | Người phía Trường tham gia tiếp đoàn. |
| Quốc tịch | Chuỗi ký tự (nhiều giá trị) | Có | Cho phép nhập/chọn nhiều quốc gia. |
| Nội dung làm việc | Văn bản dài | Không | |
| Số ngày | Số nguyên | Tự động | Generated column: `thoi_gian_di - thoi_gian_den + 1`. |

### 3.6. Module "Đoàn ra" — Đoàn cán bộ Trường đi công tác nước ngoài

| Trường dữ liệu | Kiểu dữ liệu | Bắt buộc | Ghi chú |
|---|---|---|---|
| Năm | Số nguyên | Tự động | Generated column từ Thời gian đi. |
| Đơn vị làm việc (nước ngoài) | Liên kết (chọn từ module Đối tác, Mục 3.1) | Có | Thay cho gõ tự do như Excel gốc; nếu đối tác chưa có trong danh mục, tạo nhanh ngay tại đây. |
| Thời gian đi | Ngày (date picker) | Có | |
| Thời gian về | Ngày (date picker) | Có | |
| Địa điểm đi | Chuỗi ký tự | Không | |
| Địa điểm đến | Chuỗi ký tự | Không | |
| Số lượng đoàn | Số nguyên | Có | Số người tham gia đoàn. |
| Thành phần | Văn bản dài | Không | Danh sách tên + đơn vị của các thành viên. |
| Quốc gia làm việc | Chuỗi ký tự | Có | |
| Nội dung làm việc | Văn bản dài / rich text | Không | Mục đích, yêu cầu, nội dung chương trình. |
| Số ngày | Số nguyên | Tự động | Generated column: `thoi_gian_ve - thoi_gian_di + 1`. |

---

## 4. Chức năng dùng chung cho toàn hệ thống

### 4.1. Đăng nhập & phân quyền

- Đăng nhập bằng tên đăng nhập/email nội bộ + mật khẩu; mật khẩu hash bằng BCrypt (Spring Security) — **không** đẩy việc hash mật khẩu xuống Postgres, đây là phần nên giữ ở tầng ứng dụng vì lý do bảo mật/chuẩn xác thực.
- Khoá tài khoản tạm thời sau nhiều lần đăng nhập sai liên tiếp.
- Admin tạo tài khoản, gán vai trò và module được phép nhập; người dùng tự đổi mật khẩu lần đầu.
- JWT hết hạn sau thời gian cấu hình, tự động yêu cầu đăng nhập lại.

### 4.2. Lịch sử chỉnh sửa (Audit trail)

- Mọi thêm/sửa/xoá đều ghi lại: người thực hiện, thời điểm, module, nội dung trước/sau.
- Triển khai bằng **trigger PostgreSQL** (`AFTER INSERT/UPDATE/DELETE`) ghi vào bảng lịch sử (`*_history`) — xem chi tiết kỹ thuật ở Mục 6.4. Cách này giảm tải cho backend Java (không cần Hibernate Envers hay tự viết interceptor), và vẫn ghi được lịch sử chính xác kể cả khi có ai sửa dữ liệu trực tiếp qua công cụ quản trị DB.
- Admin (và Editor với bản ghi do mình tạo) xem lịch sử ngay trên màn hình chi tiết bản ghi.
- Xoá dữ liệu là "xoá mềm" (cột `deleted_at`), Admin khôi phục được nếu xoá nhầm.

### 4.3. Import dữ liệu từ Excel

- Cung cấp sẵn template Excel đúng cấu trúc từng module, đồng thời chấp nhận trực tiếp cấu trúc file Excel gốc hiện tại.
- Chọn module, tải file `.xlsx` lên, hệ thống đọc bằng **Apache POI** và hiển thị bảng xem trước (preview) toàn bộ dòng sẽ nhập.
- Tự động kiểm tra/cảnh báo lỗi trước khi ghi: thiếu trường bắt buộc, sai định dạng ngày tháng, số hiệu văn bản trùng (nhờ ràng buộc `UNIQUE` ở DB), giá trị danh mục không hợp lệ.
- Chỉ ghi vào hệ thống sau khi xác nhận; dòng lỗi liệt kê riêng để sửa và import lại.
- Với module MoU/Đoàn vào/Đoàn ra: khi import, nếu tên đối tác trong file Excel gần giống một đối tác đã có trong danh mục (dùng `pg_trgm`, xem Mục 6.4), hệ thống gợi ý gộp vào đối tác đó thay vì tạo bản ghi mới — tránh phân mảnh danh mục Đối tác ngay từ bước nhập dữ liệu lịch sử.
- Mỗi lần import là 1 "phiên import": ai, lúc nào, file gốc, số dòng thành công/lỗi — xem lại hoặc rollback được.

### 4.4. Tìm kiếm, lọc & xuất báo cáo

- Tìm kiếm nhanh theo từ khoá, không phân biệt có dấu/không dấu tiếng Việt, trên toàn bộ nội dung — triển khai bằng full-text search của PostgreSQL (`tsvector` + `unaccent`, xem Mục 6.4), không cần dựng công cụ tìm kiếm ngoài (Elasticsearch...) cho quy mô dữ liệu này.
- Lọc theo nhiều tiêu chí kết hợp: khoảng thời gian, loại/tình trạng, quốc gia, đơn vị phụ trách, đối tác...
- Xuất kết quả đang lọc ra Excel hoặc PDF.

### 4.5. Đính kèm tài liệu

Cho phép đính kèm nhiều tệp (PDF, Word, ảnh scan) vào mỗi bản ghi văn bản/MoU.

### 4.6. Trang tổng quan (Dashboard)

- Thống kê nhanh: tổng số văn bản còn hiệu lực, số MoU đang hoạt động, số đoàn vào/ra theo năm, biểu đồ theo quốc gia/lĩnh vực.
- Widget "Thời hạn hiệu lực MoU": danh sách MoU sắp xếp theo số ngày còn lại tăng dần, thanh tiến trình theo % thời gian đã qua, tô màu theo mức cảnh báo (xanh/vàng/cam/đỏ).
- Biểu đồ số lượng MoU đến hạn theo từng tháng trong 12 tháng tới.
- Các số liệu tổng hợp của Dashboard nên lấy từ **materialized view** refresh định kỳ (Mục 6.4) thay vì tính JOIN/aggregate trực tiếp mỗi lần tải trang — giảm tải truy vấn cho backend.

### 4.7. Hệ thống cảnh báo và thông báo (Web + Email)

Khi một MoU sắp đến hạn, hệ thống chủ động nhắc người phụ trách qua 2 kênh: **web** (chuông thông báo) và **email**. (Kênh ứng dụng desktop đã được loại khỏi phạm vi tài liệu này — xem ghi chú đầu trang; nếu triển khai sau, chỉ cần thêm 1 client gọi lại đúng API thông báo đã có ở đây.)

#### 4.7.1. Cơ chế phát hiện & tạo thông báo

- Việc quét MoU sắp hết hạn **không dùng Spring `@Scheduled`** mà giao hẳn cho **pg_cron** (extension lên lịch job ngay trong PostgreSQL, Mục 6.4) chạy 1 hàm PL/pgSQL mỗi ngày — giảm tải cho backend Java và job vẫn chạy đúng giờ dù ứng dụng đang được triển khai lại/khởi động lại.
- Các mốc cảnh báo mặc định: còn 90, 60, 30, 14, 7, 1 và 0 ngày — cấu hình được trong màn hình Cài đặt (lưu trong 1 bảng cấu hình, hàm PL/pgSQL đọc từ đó).
- Mỗi MoU chỉ được tạo thông báo một lần cho mỗi mốc đã qua (đánh dấu "đã cảnh báo mốc X ngày" trong bảng để hôm sau không lặp lại).
- Người nhận: người ghi ở "Cá nhân/đơn vị đầu mối" của MoU đó, cộng toàn bộ tài khoản Admin.
- Khi hàm PL/pgSQL tạo thông báo mới, nó gọi `pg_notify()` — backend Spring Boot chỉ cần **1 listener LISTEN/NOTIFY** duy nhất để biết ngay có thông báo mới, thay vì tự poll định kỳ bằng code Java.

#### 4.7.2. Hai kênh hiển thị

| Kênh | Cách hoạt động |
|---|---|
| **Web** | Biểu tượng chuông hiển thị số thông báo chưa đọc; danh sách lọc theo đã đọc/chưa đọc; bấm vào chuyển đến MoU liên quan. Cập nhật gần-tức-thời nhờ LISTEN/NOTIFY đẩy xuống qua Server-Sent Events (SSE) hoặc polling 60 giây (đơn giản hơn nếu chưa cần SSE ngay). |
| **Email** | Gửi qua SMTP nội bộ trường (hoặc SMTP relay như Google Workspace/Office 365). Backend Spring Boot lắng nghe `NOTIFY`, dùng Spring Mail để gửi. Nếu nhiều MoU cùng đến hạn trong 1 ngày, gộp thành 1 email tổng hợp, tránh spam hộp thư. |

Cấu trúc dữ liệu thông báo (rút gọn): Loại thông báo, Mức độ (info/warning/critical — suy ra từ số ngày còn lại), Tiêu đề, Nội dung, Bản ghi liên quan, Danh sách người nhận, trạng thái đã đọc/chưa đọc theo từng kênh.

### 4.8. Báo cáo thống kê chuyên đề

Ngoài bộ lọc/xuất báo cáo linh hoạt ở Mục 4.4, hệ thống dựng sẵn 3 mẫu báo cáo cố định, đúng bố cục quen dùng để nộp/trình lãnh đạo.

#### 4.8.1. Báo cáo MoU hợp tác trong năm

- Phạm vi: MoU có Ngày ban hành trong năm được chọn (mặc định năm hiện tại), tính đến đúng thời điểm xuất báo cáo.
- Tóm tắt đầu báo cáo: tổng số MoU mới ký, phân theo Loại đối tác, phân theo lĩnh vực hợp tác, so sánh với cùng kỳ năm trước (nếu có dữ liệu).
- Bảng chi tiết: Đối tác, Loại đối tác, Lĩnh vực hợp tác, Ngày ký, Ngày hết hạn, Đơn vị đầu mối, Trạng thái hiệu lực.
- Xuất Excel/PDF kèm tiêu đề, ngày xuất, người xuất.

#### 4.8.2. Báo cáo đoàn ra và đoàn khách vào trường

- Danh sách chi tiết: toàn bộ đoàn vào/đoàn ra trong khoảng thời gian chọn, tách 2 bảng riêng trong cùng 1 báo cáo.
- Báo cáo tổng hợp: tổng số đoàn vào, tổng khách nước ngoài đã đến; tổng số đoàn ra, tổng lượt cán bộ đi công tác, tổng số ngày công tác nước ngoài; phân theo quốc gia và theo tháng (biểu đồ cột).
- Lọc theo 1 đối tác cụ thể nhờ liên kết Đối tác (Mục 3.1).

#### 4.8.3. Báo cáo thời hạn MoU với các đơn vị đối tác

- Khác với widget nhanh trên Dashboard, đây là báo cáo chính thức xuất file: liệt kê MoU còn hiệu lực, sắp xếp theo đối tác, kèm Ngày ký/Ngày hết hạn/Số ngày còn lại/Trạng thái — dùng cho họp giao ban.
- Lọc theo mốc thời gian (VD: MoU hết hạn trong 6 tháng tới).

Cả 3 báo cáo đều lưu lịch sử xuất báo cáo (ai, lúc nào, thông số lọc) để tra lại khi cần.

---

## 5. Yêu cầu phi chức năng

| Hạng mục | Yêu cầu |
|---|---|
| Hiệu năng | Đáp ứng tốt vài chục người dùng đồng thời, dữ liệu tích luỹ nhiều năm (quy mô hiện tại: vài trăm dòng/sheet) — không cần tối ưu cho dữ liệu lớn. |
| Bảo mật | Mật khẩu hash BCrypt; bắt buộc HTTPS; giới hạn số lần đăng nhập sai; phân quyền theo vai trò; nhật ký truy cập. |
| Sao lưu | `pg_dump` + tệp đính kèm tự động hằng ngày (qua `pg_cron` hoặc cron hệ điều hành), lưu tối thiểu 30 bản gần nhất, kiểm thử khôi phục định kỳ. |
| Khả dụng | Chạy trên hạ tầng nội bộ trường; ưu tiên đơn giản, dễ bảo trì bởi 1 quản trị viên bán chuyên trách. |
| Khả năng mở rộng | Kiến trúc tách API/dữ liệu để dễ bổ sung module mới (đề tài NCKH, hội thảo quốc tế...) hoặc client mới (desktop, app) trong tương lai. |
| Ngôn ngữ | Giao diện tiếng Việt có dấu; tìm kiếm hỗ trợ cả có dấu và không dấu (`unaccent`). |
| Thiết bị | Web responsive, dùng tốt trên máy tính và tablet; ưu tiên desktop vì là công việc văn phòng. |

---

## 6. Kiến trúc và công nghệ

### 6.1. Kiến trúc tổng thể

Backend Spring Boot cung cấp REST API (xác thực JWT), frontend React là client duy nhất trong phạm vi tài liệu này. Kiến trúc tách rời để nếu sau này bạn muốn nhờ Claude Code viết thêm ứng dụng desktop/di động, chỉ cần gọi lại API đã có, không phải sửa backend.

```
┌──────────────┐        REST API (JWT)        ┌────────────────────┐
│  React (Web) │ ───────────────────────────▶ │  Spring Boot 4.1.1  │
└──────────────┘                              │  (Java 21 LTS)      │
                                               └─────────┬──────────┘
                                                         │ JDBC / R2DBC
                                                         ▼
                                               ┌────────────────────┐
                                               │   PostgreSQL       │
                                               │  + pg_trgm         │
                                               │  + unaccent        │
                                               │  + pgcrypto        │
                                               │  + pg_cron         │
                                               └────────────────────┘
```

### 6.2. Backend — Spring Boot 4.1.1

- **Phiên bản:** Spring Boot 4.1.1 (theo yêu cầu), đi kèm Spring Framework 7.x. Yêu cầu tối thiểu **Java 17**, khuyến nghị dùng **Java 21 LTS** (LTS mới nhất được hỗ trợ, hiệu năng tốt hơn Java 17 nhờ các cải tiến GC/virtual threads).
- Spring Web (REST API) + Spring Data JPA + PostgreSQL driver.
- Spring Security + JWT: đăng nhập trả token, mọi API xác thực qua token; phân quyền theo vai trò khai báo bằng annotation trên từng API.
- Apache POI: đọc/ghi Excel cho import (Mục 4.3) và xuất báo cáo (Mục 4.4, 4.8).
- Spring Mail (JavaMailSender): gửi email nhắc hạn MoU, kích hoạt bởi LISTEN/NOTIFY từ Postgres (Mục 6.4), không cần `@Scheduled` tự poll.
- springdoc-openapi (Swagger UI): tự sinh tài liệu API — hữu ích nếu sau này Claude Code viết thêm client khác, chỉ cần đọc Swagger để biết API.
- **Chủ trương chung:** những gì Postgres làm tốt hơn/rẻ hơn (tính toán cột suy ra, audit log, lịch chạy job, tìm kiếm mờ, tìm kiếm full-text, thông báo real-time nội bộ) đều đẩy xuống DB — xem chi tiết Mục 6.4. Backend Java chỉ giữ lại phần bắt buộc phải ở tầng ứng dụng: xác thực/phân quyền (Spring Security), gọi SMTP gửi email, expose REST API, và nghiệp vụ phức tạp không tiện diễn đạt bằng SQL/PL-pgSQL.

### 6.3. Frontend giai đoạn 1 — React (Web)

- React + bộ thư viện giao diện dựng sẵn (Ant Design hoặc MUI) để có ngay bảng dữ liệu (data table lọc/sắp xếp/phân trang), biểu mẫu nhập liệu, biểu đồ Dashboard.
- Gọi API Spring Boot qua HTTPS bằng Axios/React Query; lưu token, tự làm mới khi hết hạn.
- Toàn bộ 6 module, Dashboard, và các báo cáo ở Mục 4.8 là các trang React gọi API tương ứng.

### 6.4. Tận dụng PostgreSQL để giảm tải backend

Theo yêu cầu, các khả năng sau của PostgreSQL được dùng để đẩy bớt logic ra khỏi tầng ứng dụng Java:

| Nhu cầu | Tính năng/extension Postgres | Cách dùng trong hệ thống |
|---|---|---|
| Trường tự tính (Số ngày, Năm) | **Generated column** (`GENERATED ALWAYS AS ... STORED`) | Cột tính sẵn, lưu thẳng trong bảng, không cần code Java tính rồi set lại trước khi lưu. |
| Trạng thái MoU (phụ thuộc ngày hiện tại) | **VIEW** thường (không dùng generated column vì `CURRENT_DATE` không immutable) | 1 view `v_mou_trang_thai` với `CASE WHEN ngay_het_han < CURRENT_DATE THEN ...`, backend chỉ `SELECT` từ view. |
| Tìm kiếm không phân biệt dấu tiếng Việt | **`unaccent`** + **full-text search** (`tsvector`, GIN index) | Cột `tsv` sinh tự động từ `to_tsvector('simple', unaccent(...))`; lưu ý cần `ALTER FUNCTION unaccent(regdictionary, text) IMMUTABLE` để dùng được trong generated column/index. |
| Phát hiện đối tác trùng/gần giống tên | **`pg_trgm`** (trigram similarity) | Index GIN `gin_trgm_ops` trên cột tên đối tác; query bằng `similarity()`/toán tử `%` để gợi ý khi thêm mới hoặc khi import Excel (Mục 4.3). |
| Audit trail (lịch sử sửa) | **Trigger** `AFTER INSERT/UPDATE/DELETE` | Trigger tự ghi bản ghi cũ vào bảng `*_history`, không cần Hibernate Envers hay interceptor ở tầng Java. |
| Lịch quét MoU sắp hết hạn hằng ngày | **`pg_cron`** | 1 hàm PL/pgSQL được `cron.schedule(...)` chạy mỗi ngày, tự tạo thông báo — thay hẳn cho Spring `@Scheduled`. |
| Đẩy thông báo real-time cho backend | **`LISTEN` / `NOTIFY`** | Hàm PL/pgSQL gọi `pg_notify()` khi có thông báo mới; Spring Boot chỉ cần 1 kết nối `LISTEN` thay vì tự poll DB liên tục. |
| Số liệu Dashboard/báo cáo tổng hợp | **Materialized view**, refresh qua `pg_cron` | Precompute các phép JOIN/aggregate nặng, tránh tính lại mỗi lần tải trang. |
| Ràng buộc dữ liệu (danh mục cố định, không trùng số hiệu) | **ENUM type**, **UNIQUE**, **CHECK constraint** | Validate ngay ở tầng DB, giảm rủi ro dữ liệu rác dù có sai sót ở tầng ứng dụng. |
| Sinh UUID (nếu chọn UUID làm khoá chính) | **`pgcrypto`** (`gen_random_uuid()`) | Tuỳ chọn thay cho ID tự tăng, tiện nếu sau này đồng bộ dữ liệu với hệ thống khác. |
| Phân quyền dữ liệu theo module (nâng cao) | **Row-Level Security (RLS)** | Cân nhắc dùng nếu muốn enforce "Editor chỉ thấy module được giao" ngay ở tầng DB, không phụ thuộc hoàn toàn vào code Java — có thể để ở giai đoạn sau nếu chưa cần ngay. |

Ví dụ minh hoạ (để Claude Code tham khảo khi cài đặt):

```sql
-- Bật các extension cần thiết
CREATE EXTENSION IF NOT EXISTS pg_trgm;
CREATE EXTENSION IF NOT EXISTS unaccent;
CREATE EXTENSION IF NOT EXISTS pgcrypto;
CREATE EXTENSION IF NOT EXISTS pg_cron;   -- cần khai báo trong shared_preload_libraries và khởi động lại Postgres

-- Cho phép dùng unaccent trong generated column / index
ALTER FUNCTION unaccent(regdictionary, text) IMMUTABLE;

-- Generated column: Số ngày của Đoàn ra
ALTER TABLE doan_ra
  ADD COLUMN so_ngay INTEGER GENERATED ALWAYS AS (thoi_gian_ve - thoi_gian_di + 1) STORED;

-- Full-text search không dấu cho Văn bản
ALTER TABLE van_ban
  ADD COLUMN tsv tsvector GENERATED ALWAYS AS (
    to_tsvector('simple', unaccent(coalesce(ten_van_ban,'') || ' ' || coalesce(noi_dung_chinh,'')))
  ) STORED;
CREATE INDEX idx_van_ban_tsv ON van_ban USING GIN (tsv);

-- Trigram index để gợi ý đối tác trùng tên
CREATE INDEX idx_doi_tac_ten_trgm ON doi_tac USING GIN (ten_doi_tac gin_trgm_ops);
-- Query gợi ý: SELECT * FROM doi_tac WHERE ten_doi_tac % 'Đại học Hoàng Gia' ORDER BY similarity(ten_doi_tac, 'Đại học Hoàng Gia') DESC;

-- Lên lịch quét MoU sắp hết hạn mỗi ngày 6:00 sáng bằng pg_cron
SELECT cron.schedule('quet-mou-sap-het-han', '0 6 * * *', $$ CALL sp_quet_mou_sap_het_han(); $$);
```

> **Lưu ý triển khai:** `pg_cron` không có sẵn trong bản cài PostgreSQL mặc định — cần cài thêm gói (qua kho PGDG) và khai báo `shared_preload_libraries = 'pg_cron'` trong `postgresql.conf`, sau đó khởi động lại dịch vụ. Vì hệ thống tự triển khai trên máy chủ riêng (ML110), việc này hoàn toàn chủ động được, không bị giới hạn như trên dịch vụ Postgres cloud managed.

---

## 7. Triển khai trên máy chủ Rocky Linux (HP ML110)

### 7.1. Đóng gói bằng Docker

Đóng gói bằng Docker Compose (backend Spring Boot, PostgreSQL, frontend React bản build tĩnh, Nginx) thay vì cài trực tiếp lên hệ điều hành. Lý do: kho gói mặc định (dnf) của Rocky Linux thường có phiên bản Java/PostgreSQL cũ hơn khuyến nghị; Docker giúp cô lập phiên bản, nâng cấp/sao lưu dễ dàng, và di chuyển máy chủ khác dễ hơn.

### 7.2. Các thành phần hạ tầng

| Thành phần | Vai trò |
|---|---|
| Nginx | Reverse proxy, HTTPS (TLS), phục vụ bản build tĩnh React, chuyển tiếp `/api/...` sang Spring Boot, giới hạn kích thước upload. |
| Spring Boot (JAR) | Chạy bằng OpenJDK 21 trong container riêng (embedded Tomcat có sẵn, không cần cài Tomcat ngoài). |
| PostgreSQL | Dùng image chính thức + cài thêm `pg_cron`/`pg_trgm`/`unaccent`/`pgcrypto` (`pg_trgm`, `unaccent`, `pgcrypto` có sẵn trong contrib, chỉ cần `CREATE EXTENSION`; `pg_cron` cần image/gói riêng có kèm sẵn, ví dụ image cộng đồng `pgcron` hoặc build thêm layer từ image chính thức). |
| firewalld | Chỉ mở cổng 443 (HTTPS) và 80 (chuyển hướng) ra mạng nội bộ trường. |
| SELinux | Giữ Enforcing; cấu hình policy phù hợp cho container. |
| Chứng chỉ TLS | CA nội bộ trường nếu chỉ dùng trong LAN; Let's Encrypt nếu có tên miền truy cập từ Internet. |

### 7.3. Sao lưu & vận hành

- Sao lưu tự động hằng ngày: `pg_dump` + rsync thư mục tệp đính kèm sang ổ đĩa/NAS riêng, giữ tối thiểu 30 bản gần nhất (có thể tự lên lịch bằng `pg_cron` gọi script, hoặc cron hệ điều hành).
- Cập nhật: `docker compose pull && docker compose up -d` — không ảnh hưởng dữ liệu vì lưu ở volume riêng.
- Giám sát: cảnh báo khi dung lượng ổ đĩa/container gặp lỗi (cron kiểm tra + email, hoặc công cụ nhẹ như Netdata).

---

## 8. Kế hoạch import dữ liệu ban đầu từ file Excel hiện có

File `VB ĐHKG_P.HTKHCN.xlsx` hiện có khoảng 9 dòng "Văn bản ĐHKG", 29 dòng "VBPL VN", 79 dòng "MoU", 81 dòng "Đoàn vào" và 1 dòng "Đoàn ra" đang có dữ liệu (tại thời điểm phân tích).

1. **Chuẩn hoá dữ liệu nguồn:** rà soát giá trị chưa nhất quán (VD: "Phạm vi hợp tác" ở sheet MoU có nơi ghi "x", nơi ghi "X", nơi ghi "Toàn diện (Xã giao)") trước khi import.
2. **Import theo module:** dùng chức năng import (Mục 4.3) nạp trực tiếp từng sheet; hệ thống tự tạo danh mục (loại văn bản, lĩnh vực...) nếu chưa tồn tại. Với sheet MoU, tách riêng cột A (Trong nước/Ngoài nước) thành trường "Loại đối tác" của module Đối tác, đồng thời gộp các dòng "Tên đối tác" trùng/gần giống nhờ gợi ý `pg_trgm` trước khi tạo bản ghi Đối tác mới.
3. **Đối chiếu số dòng:** so sánh số bản ghi mỗi module với số dòng gốc, spot-check 5-10 bản ghi mỗi module.
4. **Đính kèm bản scan** văn bản/MoU gốc (nếu có) vào từng bản ghi tương ứng — làm thủ công sau import.
5. **Khoá/lưu trữ** file Excel gốc làm bản tham chiếu, chính thức chuyển sang hệ thống mới cho các cập nhật từ ngày go-live.

---

## 9. Lộ trình triển khai đề xuất

| Giai đoạn | Nội dung | Thời gian ước tính |
|---|---|---|
| **Giai đoạn 1 — Backend** | Dựng hạ tầng Docker trên ML110; thiết kế schema Postgres (bật extension, generated column, trigger audit, view trạng thái MoU); xây API Spring Boot 4.1.1 cho 6 module + phân quyền + đăng nhập; chức năng import Excel. | 3 - 4 tuần |
| **Giai đoạn 2 — Frontend** | Xây giao diện React cho 6 module, Dashboard, 3 báo cáo chuyên đề (Mục 4.8), cảnh báo MoU (web + email), nút tra cứu chéo VBPL VN với vbpl.vn. | 2 - 3 tuần |
| **Giai đoạn 3 — Go-live** | Import dữ liệu lịch sử, đào tạo người dùng (Admin/Editor/Viewer), cấp tài khoản, thiết lập sao lưu tự động, vận hành song song 2-4 tuần với Excel trước khi ngừng hẳn. | 1 tuần + theo dõi |
| **Giai đoạn 4 — Mở rộng (tuỳ chọn, ngoài phạm vi tài liệu này)** | Ứng dụng desktop/di động (gọi lại API sẵn có — dự kiến nhờ Claude Code), thử nghiệm tự động đối chiếu VBPL VN với vbpl.vn, thêm module mới (đề tài NCKH, hội thảo...), xác thực 2 lớp (2FA). | Theo nhu cầu thực tế |

---

## 10. Rủi ro và khuyến nghị

| Rủi ro | Khuyến nghị giảm thiểu |
|---|---|
| Dữ liệu gốc Excel không nhất quán (VD: "x"/"X"/text tự do cùng 1 cột) | Chuẩn hoá trước khi import (Mục 8, Bước 1); hệ thống mới bắt buộc chọn từ danh mục/ENUM thay vì gõ tự do. |
| Máy chủ vật lý (ML110) là điểm lỗi duy nhất | Sao lưu tự động ra thiết bị khác (ổ rời/NAS) ngoài máy chủ chính; cân nhắc kế hoạch khôi phục thảm hoạ đơn giản. |
| Nhiều người nhập liệu cùng lúc → dễ trùng lặp (đoàn khách, đối tác) | Cảnh báo trùng dựa trên tên + ngày; `pg_trgm` gợi ý đối tác gần giống khi tạo mới; hiển thị người vừa nhập gần đây. |
| Người dùng ngại đổi thói quen từ Excel | Giữ nguyên tên trường/cấu trúc quen thuộc; hướng dẫn ngắn; vận hành song song với Excel một thời gian. |
| `pg_cron` cần cài đặt/cấu hình thêm, không có sẵn mặc định | Cài qua kho PGDG hoặc image Docker có kèm sẵn; nếu vì lý do nào đó không cài được, phương án dự phòng là quay lại Spring `@Scheduled` cho riêng phần lên lịch (chấp nhận tăng nhẹ tải backend). |
| Đẩy nhiều logic xuống Postgres (trigger, generated column, pg_cron) làm tăng độ phức tạp khi debug so với thuần Java | Ghi tài liệu kỹ từng trigger/function PL-pgSQL (tên, mục đích, bảng liên quan) ngay trong migration script để người bảo trì sau dễ tra cứu; đây là đánh đổi hợp lý để giảm tải backend theo đúng yêu cầu. |

---

### Tóm tắt

Backend Spring Boot 4.1.1 (Java 21 LTS) + PostgreSQL, frontend React (web, giai đoạn 1 duy nhất trong tài liệu này), tách rời qua REST API. Module **Đối tác** dùng chung cho MoU/Đoàn vào/Đoàn ra, gắn đúng trường "Loại đối tác" phát hiện từ file Excel gốc. Ba báo cáo chuyên đề (MoU trong năm, đoàn ra/vào, thời hạn MoU theo đối tác) bên cạnh Dashboard. Cảnh báo MoU qua web + email, dùng `pg_cron` + `LISTEN/NOTIFY` để giảm tải backend thay vì tự polling/scheduling trong Java. Tận dụng tối đa các extension Postgres (`pg_trgm`, `unaccent`, generated column, trigger audit, materialized view) để đẩy bớt logic ra khỏi tầng ứng dụng. Ứng dụng desktop/di động nằm ngoài phạm vi — để dành cho giai đoạn sau, kiến trúc hiện tại đã sẵn sàng cho việc đó.
