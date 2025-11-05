CREATE DATABASE ManageLibrary;
GO
USE ManageLibrary;
GO

-- ==============================================
-- BẢNG NHÂN VIÊN
-- ==============================================
CREATE TABLE Employees (
    EmployeeId VARCHAR(20) PRIMARY KEY,              -- Mã nhân viên (VD: NV001)
    FullName NVARCHAR(100) NOT NULL,                 -- Họ tên nhân viên
    Email NVARCHAR(100),                             -- Email
    Telephone NVARCHAR(20),                          -- Số điện thoại
    Role NVARCHAR(50)                                -- Chức vụ (thủ thư, quản lý, ...)
);

-- ==============================================
-- BẢNG ĐỘC GIẢ
-- ==============================================
CREATE TABLE Readers (
    ReaderId VARCHAR(20) PRIMARY KEY,                -- Mã độc giả (VD: DG001)
    FullName NVARCHAR(100) NOT NULL,                 -- Họ tên
    DateOfBirth DATE,                                -- Ngày sinh
    NationalId NVARCHAR(20),                         -- CCCD/CMND
    TypeOfReader NVARCHAR(50),                       -- Loại độc giả (Sinh viên, Giảng viên,...)
    Email NVARCHAR(100),
    Telephone NVARCHAR(20),
    Address NVARCHAR(200),
    Department NVARCHAR(100)                         -- Khoa / Phòng ban
);

-- ==============================================   
-- BẢNG TÀI KHOẢN
-- 1-1 với Reader, 1-n với Employee
-- ==============================================
CREATE TABLE Account (
    AccountId VARCHAR(20) PRIMARY KEY,               -- Mã tài khoản (VD: TK001)
    Username NVARCHAR(50) UNIQUE NOT NULL,           -- Tên đăng nhập
    Password NVARCHAR(100) NOT NULL,                 -- Mật khẩu
    EmployeeId VARCHAR(20) NULL,                     -- Liên kết nhân viên (nếu là tài khoản nhân viên)
    ReaderId VARCHAR(20) NULL,                       -- Liên kết độc giả (nếu là tài khoản độc giả)
    FOREIGN KEY (EmployeeId) REFERENCES Employees(EmployeeId),
    FOREIGN KEY (ReaderId) REFERENCES Readers(ReaderId),
    CONSTRAINT UQ_Account_Reader UNIQUE (ReaderId)   -- Mỗi độc giả chỉ có 1 tài khoản
);

-- ==============================================
-- BẢNG TÁC GIẢ
-- ==============================================
CREATE TABLE Author (
    AuthorId VARCHAR(20) PRIMARY KEY,                -- Mã tác giả (VD: TG001)
    Name NVARCHAR(100) NOT NULL                      -- Tên tác giả
);

-- ==============================================
-- BẢNG NHÀ XUẤT BẢN
-- ==============================================
CREATE TABLE Publisher (
    PublisherId VARCHAR(20) PRIMARY KEY,             -- Mã NXB (VD: NXB001)
    Name NVARCHAR(100) NOT NULL,                     -- Tên nhà xuất bản
    Address NVARCHAR(200),                           -- Địa chỉ
    Telephone NVARCHAR(20)                           -- SĐT
);

-- ==============================================
-- BẢNG THỂ LOẠI
-- ==============================================
CREATE TABLE Category (
    CategoryId VARCHAR(20) PRIMARY KEY,              -- Mã thể loại (VD: TL001)
    Name NVARCHAR(100) NOT NULL                      -- Tên thể loại (CNTT, Văn học,...)
);

-- ==============================================
-- BẢNG SÁCH
-- ==============================================
CREATE TABLE Books (
    BookId VARCHAR(20) PRIMARY KEY,                  -- Mã sách (VD: S001)
    Name NVARCHAR(200) NOT NULL,                     -- Tên sách
    YearOfPublic INT,                                -- Năm xuất bản
    Position NVARCHAR(50),                           -- Vị trí trên kệ
    NumOfPage INT,                                   -- Số trang
    Cost DECIMAL(10,2),                              -- Giá
    CategoryId VARCHAR(20),                          -- Thể loại
    AuthorId VARCHAR(20),                            -- Tác giả
    PublisherId VARCHAR(20),                         -- Nhà xuất bản
    FOREIGN KEY (CategoryId) REFERENCES Category(CategoryId),
    FOREIGN KEY (AuthorId) REFERENCES Author(AuthorId),
    FOREIGN KEY (PublisherId) REFERENCES Publisher(PublisherId)
);
USE ManageLibrary;
GO

ALTER TABLE Books
ADD Quantity INT NOT NULL DEFAULT 0; -- Thêm cột Số lượng, mặc định là 0
GO

PRINT N'Đã thêm cột Quantity vào bảng Books thành công!';

-- ==============================================
-- BẢNG PHIẾU MƯỢN
-- ==============================================
CREATE TABLE LoanSlip (
    LoanId VARCHAR(20) PRIMARY KEY,                  -- Mã phiếu mượn (VD: PM001)
    ReaderId VARCHAR(20) NOT NULL,                   -- Mã độc giả mượn
    EmployeeId VARCHAR(20) NOT NULL,                 -- Mã nhân viên lập phiếu
    LoanDate DATE NOT NULL,                          -- Ngày mượn
    ExpiredDate DATE,                                -- Ngày hết hạn
    ReturnDate DATE,                                 -- Ngày trả
    Status NVARCHAR(50),                             -- Trạng thái (Đang mượn, Đã trả,...)
    FOREIGN KEY (ReaderId) REFERENCES Readers(ReaderId),
    FOREIGN KEY (EmployeeId) REFERENCES Employees(EmployeeId)
);

-- ==============================================
-- BẢNG CHI TIẾT MƯỢN
-- ==============================================
CREATE TABLE LoanDetail (
    LoanDetailId VARCHAR(20) PRIMARY KEY,            -- Mã chi tiết mượn (VD: CT001)
    LoanId VARCHAR(20) NOT NULL,                     -- Mã phiếu mượn
    BookId VARCHAR(20) NOT NULL,                     -- Mã sách
    LoanStatus NVARCHAR(50),                         -- Trạng thái khi mượn (Bình thường, Hư,...)
    ReturnStatus NVARCHAR(50),                       -- Trạng thái khi trả
    IsLose BIT DEFAULT 0,                            -- Có mất không (0 = Không, 1 = Có)
    Fine DECIMAL(10,2) DEFAULT 0,                    -- Tiền phạt
    FOREIGN KEY (LoanId) REFERENCES LoanSlip(LoanId),
    FOREIGN KEY (BookId) REFERENCES Books(BookId)
);
-- ==============================================
-- BẢNG SÁCH - NHIỀU TÁC GIẢ (TÙY CHỌN)
-- ==============================================
CREATE TABLE BookAuthor (
    BookId VARCHAR(20),                              -- Mã sách
    AuthorId VARCHAR(20),                            -- Mã tác giả
    PRIMARY KEY (BookId, AuthorId),
    FOREIGN KEY (BookId) REFERENCES Books(BookId),
    FOREIGN KEY (AuthorId) REFERENCES Author(AuthorId)
);

select * from Employees

-- 1. Thêm một Nhân viên mẫu (vì tài khoản Admin cần liên kết với Employee)
INSERT INTO Employees (EmployeeId, FullName, Role)
VALUES ('NV001', N'Admin Quản Trị', N'Quản lý');
GO

-- 2. Thêm một Tài khoản Admin liên kết với nhân viên 'NV001'
-- TÀI KHOẢN: admin
-- MẬT KHẨU: 123 (văn bản thuần, khớp với code đăng nhập hiện tại)
INSERT INTO Account (AccountId, Username, Password, EmployeeId, ReaderId)
VALUES ('TK001', N'admin', N'123', 'NV001', NULL);
GO



-- Thêm dữ liệu vào bảng Books
INSERT INTO Books (BookId, Name, YearOfPublic, Position, NumOfPage, Cost, CategoryId, AuthorId, PublisherId)
VALUES 
(N'S001', N'Lập trình C# cơ bản', 2020, N'A1', 350, 100000, N'TL001', N'TG001', N'NXB001'),
(N'S002', N'Lập trình Java nâng cao', 2021, N'B2', 420, 120000, N'TL001', N'TG002', N'NXB002'),
(N'S003', N'Thiết kế web với HTML, CSS', 2022, N'C1', 320, 90000, N'TL002', N'TG003', N'NXB003'),
(N'S004', N'Khoa học dữ liệu với Python', 2023, N'A3', 500, 150000, N'TL001', N'TG004', N'NXB001'),
(N'S005', N'Cơ sở dữ liệu SQL', 2019, N'B1', 380, 85000, N'TL003', N'TG001', N'NXB002'),
(N'S006', N'An toàn mạng máy tính', 2021, N'C2', 450, 135000, N'TL002', N'TG005', N'NXB003'),
(N'S007', N'Giới thiệu về Trí tuệ nhân tạo', 2022, N'D1', 330, 110000, N'TL001', N'TG006', N'NXB001');

PRINT N'Bắt đầu cập nhật số lượng cho sách hiện có...';
UPDATE Books SET Quantity = 10 WHERE BookId = N'S001';
UPDATE Books SET Quantity = 8 WHERE BookId = N'S002';
UPDATE Books SET Quantity = 15 WHERE BookId = N'S003';
UPDATE Books SET Quantity = 7 WHERE BookId = N'S004';
UPDATE Books SET Quantity = 12 WHERE BookId = N'S005';
UPDATE Books SET Quantity = 5 WHERE BookId = N'S006';
UPDATE Books SET Quantity = 10 WHERE BookId = N'S007';
UPDATE Books SET Quantity = 20 WHERE BookId = N'S008';
UPDATE Books SET Quantity = 10 WHERE BookId = N'S009';
UPDATE Books SET Quantity = 18 WHERE BookId = N'S010';
UPDATE Books SET Quantity = 15 WHERE BookId = N'S011';
UPDATE Books SET Quantity = 25 WHERE BookId = N'S012';
UPDATE Books SET Quantity = 14 WHERE BookId = N'S013';
UPDATE Books SET Quantity = 9 WHERE BookId = N'S014';
UPDATE Books SET Quantity = 22 WHERE BookId = N'S015';
UPDATE Books SET Quantity = 10 WHERE BookId = N'S016';
UPDATE Books SET Quantity = 16 WHERE BookId = N'S017';
UPDATE Books SET Quantity = 11 WHERE BookId = N'S018';
UPDATE Books SET Quantity = 7 WHERE BookId = N'S019';
UPDATE Books SET Quantity = 13 WHERE BookId = N'S020';

PRINT N'Đã cập nhật số lượng cho 20 cuốn sách.';
GO
select * from Books
-- Thêm dữ liệu vào bảng Category
INSERT INTO Category (CategoryId, Name) 
VALUES 
(N'TL001', N'Công nghệ thông tin'), 
(N'TL002', N'Mạng và bảo mật'), 
(N'TL003', N'Cơ sở dữ liệu');

-- Thêm dữ liệu vào bảng Author
INSERT INTO Author (AuthorId, Name)
VALUES
(N'TG001', N'Nguyễn Văn A'),
(N'TG002', N'Trần Thị B'),
(N'TG003', N'Lê Văn C'),
(N'TG004', N'Phạm Minh D'),
(N'TG005', N'Vũ Thị E'),
(N'TG006', N'Đặng Tiến F');

-- Thêm dữ liệu vào bảng Publisher
INSERT INTO Publisher (PublisherId, Name, Address, Telephone)
VALUES
(N'NXB001', N'Nhà xuất bản Giáo dục', N'Hà Nội', N'0241234567'),
(N'NXB002', N'Nhà xuất bản Trẻ', N'TP.HCM', N'0287654321'),
(N'NXB003', N'Nhà xuất bản Khoa học', N'Đà Nẵng', N'0236123456');
-- Thêm dữ liệu độc giả 
INSERT INTO Readers (ReaderId, FullName, DateOfBirth, NationalId, TypeOfReader, Email, Telephone, Address, Department)
VALUES 
('SV002', N'Lê Minh Tuấn', '2003-01-10', '012345678902', N'Sinh viên', 'tuan.le@email.com', '0912345678', N'123 Đường A, Quận 1, TP. HCM', N'Công nghệ Thông tin');

INSERT INTO Readers (ReaderId, FullName, DateOfBirth, NationalId, TypeOfReader, Email, Telephone, Address, Department)
VALUES 
('SV003', N'Trần Thị Mai', '2004-02-15', '012345678903', N'Sinh viên', 'mai.tran@email.com', '0912345679', N'456 Đường B, Quận 3, TP. HCM', N'Kế toán');

INSERT INTO Readers (ReaderId, FullName, DateOfBirth, NationalId, TypeOfReader, Email, Telephone, Address, Department)
VALUES 
('SV004', N'Phạm Văn Hùng', '2003-03-20', '012345678904', N'Sinh viên', 'hung.pham@email.com', '0912345680', N'789 Đường C, Quận 5, TP. HCM', N'Cơ khí');

INSERT INTO Readers (ReaderId, FullName, DateOfBirth, NationalId, TypeOfReader, Email, Telephone, Address, Department)
VALUES 
('SV005', N'Huỳnh Thị Lan', '2005-04-25', '012345678905', N'Sinh viên', 'lan.huynh@email.com', '0912345681', N'101 Đường D, Quận 7, TP. HCM', N'Ngôn ngữ Anh');

INSERT INTO Readers (ReaderId, FullName, DateOfBirth, NationalId, TypeOfReader, Email, Telephone, Address, Department)
VALUES 
('SV006', N'Bùi Văn Đức', '2003-05-30', '012345678906', N'Sinh viên', 'duc.bui@email.com', '0912345682', N'202 Đường E, Quận 9, TP. HCM', N'Công nghệ Thông tin');

INSERT INTO Readers (ReaderId, FullName, DateOfBirth, NationalId, TypeOfReader, Email, Telephone, Address, Department)
VALUES 
('SV007', N'Đặng Thị Hoa', '2004-06-05', '012345678907', N'Sinh viên', 'hoa.dang@email.com', '0912345683', N'303 Đường F, Quận 11, TP. HCM', N'Quản trị Kinh doanh');

INSERT INTO Readers (ReaderId, FullName, DateOfBirth, NationalId, TypeOfReader, Email, Telephone, Address, Department)
VALUES 
('SV008', N'Ngô Văn Nam', '2003-07-10', '012345678908', N'Sinh viên', 'nam.ngo@email.com', '0912345684', N'404 Đường G, Quận Gò Vấp, TP. HCM', N'Điện tử Viễn thông');

INSERT INTO Readers (ReaderId, FullName, DateOfBirth, NationalId, TypeOfReader, Email, Telephone, Address, Department)
VALUES 
('SV009', N'Dương Thị Thu', '2005-08-15', '012345678909', N'Sinh viên', 'thu.duong@email.com', '0912345685', N'505 Đường H, Quận Tân Bình, TP. HCM', N'Tài chính Ngân hàng');

INSERT INTO Readers (ReaderId, FullName, DateOfBirth, NationalId, TypeOfReader, Email, Telephone, Address, Department)
VALUES 
('SV010', N'Hoàng Văn Long', '2003-09-20', '012345678910', N'Sinh viên', 'long.hoang@email.com', '0912345686', N'606 Đường I, Quận Bình Thạnh, TP. HCM', N'Công nghệ Thông tin');

INSERT INTO Readers (ReaderId, FullName, DateOfBirth, NationalId, TypeOfReader, Email, Telephone, Address, Department)
VALUES 
('SV011', N'Vũ Thị Kim', '2004-10-25', '012345678911', N'Sinh viên', 'kim.vu@email.com', '0912345687', N'707 Đường K, Quận Phú Nhuận, TP. HCM', N'Luật');


-- ==============================================
--  GIẢNG VIÊN
-- ==============================================

INSERT INTO Readers (ReaderId, FullName, DateOfBirth, NationalId, TypeOfReader, Email, Telephone, Address, Department)
VALUES 
('GV002', N'Nguyễn Văn Bình', '1980-01-05', '023456789002', N'Giảng viên', 'binh.nguyen@email.com', '0987654322', N'808 Đường L, Quận 2, TP. HCM', N'Công nghệ Thông tin');

INSERT INTO Readers (ReaderId, FullName, DateOfBirth, NationalId, TypeOfReader, Email, Telephone, Address, Department)
VALUES 
('GV003', N'Trần Thị Cúc', '1982-02-10', '023456789003', N'Giảng viên', 'cuc.tran@email.com', '0987654323', N'909 Đường M, Quận 4, TP. HCM', N'Kế toán');

INSERT INTO Readers (ReaderId, FullName, DateOfBirth, NationalId, TypeOfReader, Email, Telephone, Address, Department)
VALUES 
('GV004', N'Lê Văn Dũng', '1978-03-15', '023456789004', N'Giảng viên', 'dung.le@email.com', '0987654324', N'111 Đường N, Quận 6, TP. HCM', N'Cơ khí');

INSERT INTO Readers (ReaderId, FullName, DateOfBirth, NationalId, TypeOfReader, Email, Telephone, Address, Department)
VALUES 
('GV005', N'Phạm Thị Lan', '1985-04-20', '023456789005', N'Giảng viên', 'lan.pham@email.com', '0987654325', N'222 Đường P, Quận 8, TP. HCM', N'Ngôn ngữ Anh');

INSERT INTO Readers (ReaderId, FullName, DateOfBirth, NationalId, TypeOfReader, Email, Telephone, Address, Department)
VALUES 
('GV006', N'Hoàng Văn Minh', '1990-05-25', '023456789006', N'Giảng viên', 'minh.hoang@email.com', '0987654326', N'333 Đường Q, Quận 10, TP. HCM', N'Công nghệ Thông tin');

INSERT INTO Readers (ReaderId, FullName, DateOfBirth, NationalId, TypeOfReader, Email, Telephone, Address, Department)
VALUES 
('GV007', N'Vũ Thị Nga', '1988-06-30', '023456789007', N'Giảng viên', 'nga.vu@email.com', '0987654327', N'444 Đường R, Quận 12, TP. HCM', N'Quản trị Kinh doanh');

INSERT INTO Readers (ReaderId, FullName, DateOfBirth, NationalId, TypeOfReader, Email, Telephone, Address, Department)
VALUES 
('GV008', N'Đặng Văn Sơn', '1975-07-05', '023456789008', N'Giảng viên', 'son.dang@email.com', '0987654328', N'555 Đường S, Quận Tân Phú, TP. HCM', N'Điện tử Viễn thông');

INSERT INTO Readers (ReaderId, FullName, DateOfBirth, NationalId, TypeOfReader, Email, Telephone, Address, Department)
VALUES 
('GV009', N'Bùi Thị Thảo', '1992-08-10', '023456789009', N'Giảng viên', 'thao.bui@email.com', '0987654329', N'666 Đường T, Quận Bình Tân, TP. HCM', N'Tài chính Ngân hàng');

INSERT INTO Readers (ReaderId, FullName, DateOfBirth, NationalId, TypeOfReader, Email, Telephone, Address, Department)
VALUES 
('GV010', N'Ngô Văn Hùng', '1983-09-15', '023456789010', N'Giảng viên', 'hung.ngo@email.com', '0987654330', N'777 Đường U, TP. Thủ Đức, TP. HCM', N'Công nghệ Thông tin');

INSERT INTO Readers (ReaderId, FullName, DateOfBirth, NationalId, TypeOfReader, Email, Telephone, Address, Department)
VALUES 
('GV011', N'Dương Thị Mai', '1995-10-20', '023456789011', N'Giảng viên', 'mai.duong@email.com', '0987654331', N'888 Đường V, Huyện Củ Chi, TP. HCM', N'Luật');;

-- ==============================================
-- TÀI KHOẢN CHO SINH VIÊN
-- ==============================================

INSERT INTO Account (AccountId, Username, Password, ReaderId) VALUES 
(N'TK002', N'lmtuan', N'123', N'SV002'),
(N'TK003', N'ttmai', N'123', N'SV003'),
(N'TK004', N'pvhung', N'123', N'SV004'),
(N'TK005', N'hthan', N'123', N'SV005'),
(N'TK006', N'bvduc', N'123', N'SV006'),
(N'TK007', N'dthoa', N'123', N'SV007'),
(N'TK008', N'nvnam', N'123', N'SV008'),
(N'TK009', N'dtthu', N'123', N'SV009'),
(N'TK010', N'hvlong', N'123', N'SV010'),
(N'TK011', N'vtkim', N'123', N'SV011');


-- ==============================================
-- TÀI KHOẢN CHOGIẢNG VIÊN
-- ==============================================

INSERT INTO Account (AccountId, Username, Password, ReaderId) VALUES 
(N'TK012', N'gv_nvbinh', N'123', N'GV002'),
(N'TK013', N'gv_ttcuc', N'123', N'GV003'),
(N'TK014', N'gv_lvdung', N'123', N'GV004'),
(N'TK015', N'gv_pthlan', N'123', N'GV005'),
(N'TK016', N'gv_hvminh', N'123', N'GV006'),
(N'TK017', N'gv_vtnga', N'123', N'GV007'),
(N'TK018', N'gv_dvson', N'123', N'GV008'),
(N'TK019', N'gv_btthao', N'123', N'GV009'),
(N'TK020', N'gv_nvhung', N'123', N'GV010'),
(N'TK021', N'gv_dtmai', N'123', N'GV011');


GO

PRINT N'Bắt đầu thêm dữ liệu bổ sung...';
GO

-- ==============================================
-- BỔ SUNG THÊM THỂ LOẠI
-- ==============================================
INSERT INTO Category (CategoryId, Name)
VALUES
(N'TL004', N'Văn học'),
(N'TL005', N'Kinh tế'),
(N'TL006', N'Tâm lý học'),
(N'TL007', N'Khoa học xã hội'),
(N'TL008', N'Ngoại ngữ');
GO

-- ==============================================
-- BỔ SUNG THÊM TÁC GIẢ
-- ==============================================
INSERT INTO Author (AuthorId, Name)
VALUES
(N'TG007', N'Dale Carnegie'),
(N'TG008', N'Yuval Noah Harari'),
(N'TG009', N'Robert Kiyosaki'),
(N'TG010', N'Paulo Coelho'),
(N'TG011', N'Nguyễn Nhật Ánh');
GO

-- ==============================================
-- BỔ SUNG THÊM NHÀ XUẤT BẢN
-- ==============================================
INSERT INTO Publisher (PublisherId, Name, Address, Telephone)
VALUES
(N'NXB004', N'Nhà xuất bản Kim Đồng', N'Hà Nội', N'0249876543'),
(N'NXB005', N'Nhà xuất bản Tổng hợp TPHCM', N'TP.HCM', N'0281122334');
GO

-- ==============================================
-- BỔ SUNG THÊM SÁCH (S008 - S020)
-- ==============================================
INSERT INTO Books (BookId, Name, YearOfPublic, Position, NumOfPage, Cost, CategoryId, AuthorId, PublisherId)
VALUES
(N'S008', N'Đắc nhân tâm', 2018, N'E1', 320, 79000, N'TL006', N'TG007', N'NXB005'),
(N'S009', N'Sapiens: Lược sử loài người', 2017, N'F2', 512, 169000, N'TL007', N'TG008', N'NXB002'),
(N'S010', N'Cha giàu, cha nghèo', 2000, N'E2', 207, 68000, N'TL005', N'TG009', N'NXB003'),
(N'S011', N'Nhà giả kim', 1988, N'G1', 224, 75000, N'TL004', N'TG010', N'NXB005'),
(N'S012', N'Cho tôi xin một vé đi tuổi thơ', 2008, N'G2', 200, 50000, N'TL004', N'TG011', N'NXB004'),
(N'S013', N'Quẳng gánh lo đi và vui sống', 2019, N'E1', 380, 90000, N'TL006', N'TG007', N'NXB005'),
(N'S014', N'Homo Deus: Lược sử tương lai', 2019, N'F2', 540, 179000, N'TL007', N'TG008', N'NXB002'),
(N'S015', N'Mắt biếc', 1990, N'G2', 250, 65000, N'TL004', N'TG011', N'NXB004'),
(N'S016', N'Lập trình Python từ cơ bản đến nâng cao', 2022, N'A4', 600, 250000, N'TL001', N'TG004', N'NXB001'),
(N'S017', N'Ngữ pháp Tiếng Anh cơ bản', 2020, N'H1', 400, 120000, N'TL008', N'TG003', N'NXB001'),
(N'S018', N'Kế toán tài chính', 2021, N'E3', 550, 180000, N'TL005', N'TG002', N'NXB003'),
(N'S019', N'Quản trị mạng căn bản', 2022, N'C3', 450, 160000, N'TL002', N'TG005', N'NXB001'),
(N'S020', N'Tâm lý học đám đông', 2015, N'F1', 300, 88000, N'TL006', N'TG006', N'NXB002');
GO

-- ==============================================
-- THÊM DỮ LIỆU MƯỢN TRẢ (PHIẾU MƯỢN)
-- Giả sử hôm nay là ngày 30/10/2025
-- ==============================================

-- Phiếu 1: Đã trả (Sinh viên DG123456789)
INSERT INTO LoanSlip (LoanId, ReaderId, EmployeeId, LoanDate, ExpiredDate, ReturnDate, Status)
VALUES
(N'PM001', N'DG123456789', N'NV001', '2025-09-01', '2025-09-15', '2025-09-14', N'Đã trả');

-- Phiếu 2: Đang mượn, chưa quá hạn (Sinh viên DG111222333)
INSERT INTO LoanSlip (LoanId, ReaderId, EmployeeId, LoanDate, ExpiredDate, ReturnDate, Status)
VALUES
(N'PM002', N'DG111222333', N'NV001', '2025-10-20', '2025-11-03', NULL, N'Đang mượn');

-- Phiếu 3: Quá hạn (Sinh viên DG444555666)
INSERT INTO LoanSlip (LoanId, ReaderId, EmployeeId, LoanDate, ExpiredDate, ReturnDate, Status)
VALUES
(N'PM003', N'DG444555666', N'NV001', '2025-09-10', '2025-09-24', NULL, N'Quá hạn');

-- Phiếu 4: Đã trả (Giảng viên DG987654321)
INSERT INTO LoanSlip (LoanId, ReaderId, EmployeeId, LoanDate, ExpiredDate, ReturnDate, Status)
VALUES
(N'PM004', N'DG987654321', N'NV001', '2025-10-01', '2025-10-30', '2025-10-25', N'Đã trả');

-- Phiếu 5: Đang mượn (Giảng viên DG888777666)
INSERT INTO LoanSlip (LoanId, ReaderId, EmployeeId, LoanDate, ExpiredDate, ReturnDate, Status)
VALUES
(N'PM005', N'DG888777666', N'NV001', '2025-10-15', '2025-11-15', NULL, N'Đang mượn');

GO

-- ==============================================
-- THÊM DỮ LIỆU CHI TIẾT MƯỢN TRẢ
-- ==============================================

-- Chi tiết cho Phiếu PM001 (Đã trả)
INSERT INTO LoanDetail (LoanDetailId, LoanId, BookId, LoanStatus, ReturnStatus, IsLose, Fine)
VALUES
(N'CT001', N'PM001', N'S001', N'Bình thường', N'Bình thường', 0, 0),
(N'CT002', N'PM001', N'S008', N'Bình thường', N'Bình thường', 0, 0);

-- Chi tiết cho Phiếu PM002 (Đang mượn)
INSERT INTO LoanDetail (LoanDetailId, LoanId, BookId, LoanStatus, ReturnStatus, IsLose, Fine)
VALUES
(N'CT003', N'PM002', N'S009', N'Bình thường', NULL, 0, 0);

-- Chi tiết cho Phiếu PM003 (Quá hạn)
INSERT INTO LoanDetail (LoanDetailId, LoanId, BookId, LoanStatus, ReturnStatus, IsLose, Fine)
VALUES
(N'CT004', N'PM003', N'S010', N'Bình thường', NULL, 0, 0),
(N'CT005', N'PM003', N'S011', N'Bình thường', NULL, 0, 0);

-- Chi tiết cho Phiếu PM004 (Đã trả, 1 cuốn mất, 1 cuốn hư hỏng)
INSERT INTO LoanDetail (LoanDetailId, LoanId, BookId, LoanStatus, ReturnStatus, IsLose, Fine)
VALUES
(N'CT006', N'PM004', N'S012', N'Bình thường', N'Bình thường', 0, 0),
(N'CT007', N'PM004', N'S002', N'Bình thường', N'Hư hỏng nhẹ', 0, 20000), -- Phạt 20k
(N'CT008', N'PM004', N'S003', N'Bình thường', N'Mất sách', 1, 90000);   -- Phạt 90k (bằng giá sách)

-- Chi tiết cho Phiếu PM005 (Đang mượn)
INSERT INTO LoanDetail (LoanDetailId, LoanId, BookId, LoanStatus, ReturnStatus, IsLose, Fine)
VALUES
(N'CT009', N'PM005', N'S016', N'Bình thường', NULL, 0, 0),
(N'CT010', N'PM005', N'S017', N'Bình thường', NULL, 0, 0),
(N'CT011', N'PM005', N'S018', N'Bình thường', NULL, 0, 0);

GO

PRINT N'Đã thêm thành công dữ liệu sách và phiếu mượn!';
GO

