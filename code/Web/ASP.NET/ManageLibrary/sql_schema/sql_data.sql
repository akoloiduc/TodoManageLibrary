CREATE DATABASE ManageLibrary;
GO
USE ManageLibrary;
GO



-- BẢNG NHÂN VIÊN
CREATE TABLE Employees (
    EmployeeId VARCHAR(20) PRIMARY KEY,
    FullName NVARCHAR(100) NOT NULL,
    Email NVARCHAR(100),
    Telephone NVARCHAR(20),
    Role NVARCHAR(50)
);

-- BẢNG ĐỘC GIẢ
CREATE TABLE Readers (
    ReaderId VARCHAR(20) PRIMARY KEY,
    FullName NVARCHAR(100) NOT NULL,
    DateOfBirth DATE,
    NationalId NVARCHAR(20),
    TypeOfReader NVARCHAR(50),
    Email NVARCHAR(100),
    Telephone NVARCHAR(20),
    Address NVARCHAR(200),
    Department NVARCHAR(100)
);

-- BẢNG TÀI KHOẢN
CREATE TABLE Account (
    AccountId VARCHAR(20) PRIMARY KEY,
    Username NVARCHAR(50) UNIQUE NOT NULL,
    Password NVARCHAR(100) NOT NULL,
    EmployeeId VARCHAR(20),
    ReaderId VARCHAR(20),
    FOREIGN KEY (EmployeeId) REFERENCES Employees(EmployeeId),
    FOREIGN KEY (ReaderId) REFERENCES Readers(ReaderId),
    CONSTRAINT UQ_Account_Reader UNIQUE (ReaderId)
);

-- BẢNG TÁC GIẢ
CREATE TABLE Author (
    AuthorId VARCHAR(20) PRIMARY KEY,
    Name NVARCHAR(100) NOT NULL
);

-- BẢNG NHÀ XUẤT BẢN
CREATE TABLE Publisher (
    PublisherId VARCHAR(20) PRIMARY KEY,
    Name NVARCHAR(100) NOT NULL,
    Address NVARCHAR(200),
    Telephone NVARCHAR(20)
);

-- BẢNG THỂ LOẠI
CREATE TABLE Category (
    CategoryId VARCHAR(20) PRIMARY KEY,
    Name NVARCHAR(100) NOT NULL
);

-- BẢNG SÁCH
CREATE TABLE Books (
    BookId VARCHAR(20) PRIMARY KEY,
    Name NVARCHAR(200) NOT NULL,
    YearOfPublic INT,
    Position NVARCHAR(50),
    NumOfPage INT,
    Cost DECIMAL(10,2),
    CategoryId VARCHAR(20),
    AuthorId VARCHAR(20),
    PublisherId VARCHAR(20),
    Quantity INT NOT NULL DEFAULT 0,
    FOREIGN KEY (CategoryId) REFERENCES Category(CategoryId),
    FOREIGN KEY (AuthorId) REFERENCES Author(AuthorId),
    FOREIGN KEY (PublisherId) REFERENCES Publisher(PublisherId)
);

-- PHIẾU MƯỢN
CREATE TABLE LoanSlip (
    LoanId VARCHAR(20) PRIMARY KEY,
    ReaderId VARCHAR(20) NOT NULL,
    EmployeeId VARCHAR(20) NOT NULL,
    LoanDate DATE NOT NULL,
    ExpiredDate DATE,
    ReturnDate DATE,
    Status NVARCHAR(50),
    FOREIGN KEY (ReaderId) REFERENCES Readers(ReaderId),
    FOREIGN KEY (EmployeeId) REFERENCES Employees(EmployeeId)
);

-- CHI TIẾT MƯỢN
CREATE TABLE LoanDetail (
    LoanDetailId VARCHAR(20) PRIMARY KEY,
    LoanId VARCHAR(20) NOT NULL,
    BookId VARCHAR(20) NOT NULL,
    LoanStatus NVARCHAR(50),
    ReturnStatus NVARCHAR(50),
    IsLose BIT DEFAULT 0,
    Fine DECIMAL(10,2) DEFAULT 0,
    FOREIGN KEY (LoanId) REFERENCES LoanSlip(LoanId),
    FOREIGN KEY (BookId) REFERENCES Books(BookId)
);

-- NHIỀU TÁC GIẢ
CREATE TABLE BookAuthor (
    BookId VARCHAR(20),
    AuthorId VARCHAR(20),
    PRIMARY KEY (BookId, AuthorId),
    FOREIGN KEY (BookId) REFERENCES Books(BookId),
    FOREIGN KEY (AuthorId) REFERENCES Author(AuthorId)
);

-- =========================
-- DỮ LIỆU MẪU
-- =========================
INSERT INTO Employees (EmployeeId, FullName, Role)
VALUES ('NV001', N'Admin Quản Trị', N'Quản lý');

INSERT INTO Account (AccountId, Username, Password, EmployeeId)
VALUES ('TK001', N'admin', N'123', 'NV001');

-- CATEGORY
INSERT INTO Category (CategoryId, Name)
VALUES (N'TL001', N'Công nghệ thông tin'),
       (N'TL002', N'Mạng và bảo mật'),
       (N'TL003', N'Cơ sở dữ liệu');

-- AUTHOR
INSERT INTO Author (AuthorId, Name)
VALUES (N'TG001', N'Nguyễn Văn A'),
       (N'TG002', N'Trần Thị B'),
       (N'TG003', N'Lê Văn C'),
       (N'TG004', N'Phạm Minh D'),
       (N'TG005', N'Vũ Thị E'),
       (N'TG006', N'Đặng Tiến F');

-- PUBLISHER
INSERT INTO Publisher (PublisherId, Name, Address, Telephone)
VALUES (N'NXB001', N'Nhà xuất bản Giáo dục', N'Hà Nội', N'0241234567'),
       (N'NXB002', N'Nhà xuất bản Trẻ', N'TP.HCM', N'0287654321'),
       (N'NXB003', N'Nhà xuất bản Khoa học', N'Đà Nẵng', N'0236123456');

-- BOOKS
INSERT INTO Books (BookId, Name, YearOfPublic, Position, NumOfPage, Cost, CategoryId, AuthorId, PublisherId, Quantity)
VALUES 
('S001', N'Lập trình C# cơ bản', 2020, N'A1', 350, 100000, 'TL001', 'TG001', 'NXB001', 10),
('S002', N'Lập trình Java nâng cao', 2021, N'B2', 420, 120000, 'TL001', 'TG002', 'NXB002', 8),
('S003', N'Thiết kế web với HTML, CSS', 2022, N'C1', 320, 90000, 'TL002', 'TG003', 'NXB003', 15),
('S004', N'Khoa học dữ liệu với Python', 2023, N'A3', 500, 150000, 'TL001', 'TG004', 'NXB001', 7),
('S005', N'Cơ sở dữ liệu SQL', 2019, N'B1', 380, 85000, 'TL003', 'TG001', 'NXB002', 12);

PRINT N'Cơ sở dữ liệu đã khởi tạo và thêm dữ liệu mẫu thành công!';
GO

-- DỮ LIỆU MẪU CHO BẢNG ĐỘC GIẢ
INSERT INTO Readers (ReaderId, FullName, DateOfBirth, NationalId, TypeOfReader, Email, Telephone, Address, Department)
VALUES
('DG001', N'Nguyễn Văn Hùng', '2000-05-10', '123456789', N'Sinh viên', 'hungnv@example.com', '0901234567', N'123 Nguyễn Trãi, Hà Nội', N'Công nghệ thông tin'),
('DG002', N'Trần Thị Lan', '1999-08-22', '987654321', N'Sinh viên', 'lantr@example.com', '0912345678', N'45 Hai Bà Trưng, Hà Nội', N'Kinh tế'),
('DG003', N'Lê Minh Tuấn', '1998-12-01', '192837465', N'Cựu sinh viên', 'tuanlm@example.com', '0934567890', N'67 Láng Hạ, Hà Nội', N'Quản trị kinh doanh'),
('DG004', N'Phạm Hồng Nhung', '2001-03-18', '564738291', N'Sinh viên', 'nhungph@example.com', '0945678901', N'89 Cầu Giấy, Hà Nội', N'Sư phạm'),
('DG005', N'Hoàng Văn Tài', '1997-11-05', '837465920', N'Giảng viên', 'taihv@example.com', '0956789012', N'12 Kim Mã, Hà Nội', N'Công nghệ thông tin'),
('DG006', N'Vũ Thị Mai', '2000-02-14', '475839201', N'Sinh viên', 'maivt@example.com', '0967890123', N'34 Lê Lợi, TP.HCM', N'Ngôn ngữ Anh'),
('DG007', N'Ngô Đức Duy', '1999-09-25', '829104756', N'Sinh viên', 'duynd@example.com', '0978901234', N'90 Nguyễn Huệ, Đà Nẵng', N'Kỹ thuật phần mềm'),
('DG008', N'Đặng Thị Hòa', '1998-04-30', '910283746', N'Cựu sinh viên', 'hoadt@example.com', '0989012345', N'56 Phan Đình Phùng, Hải Phòng', N'Tài chính - Ngân hàng'),
('DG009', N'Bùi Quốc Khánh', '2001-06-12', '384756920', N'Sinh viên', 'khanhbq@example.com', '0990123456', N'23 Tôn Đức Thắng, Cần Thơ', N'Luật học'),
('DG010', N'Nguyễn Thị Yến', '2000-01-20', '564738920', N'Sinh viên', 'yentn@example.com', '0902345678', N'101 Phạm Văn Đồng, Hà Nội', N'Thiết kế đồ họa');
GO

PRINT N'Đã thêm dữ liệu mẫu cho bảng Độc giả (Readers) thành công!';
-- =========================
-- NHÂN VIÊN KHÁC
-- =========================
INSERT INTO Employees (EmployeeId, FullName, Email, Telephone, Role)
VALUES 
('NV002', N'Trần Văn Bình', N'binhtv@lib.edu.vn', N'0911222333', N'Nhân viên mượn trả'),
('NV003', N'Lê Thị Mai', N'mailt@lib.edu.vn', N'0933444555', N'Quản lý tài liệu'),
('NV004', N'Phạm Quốc Toàn', N'toanpq@lib.edu.vn', N'0944555666', N'Quản lý độc giả'),
('NV005', N'Vũ Thanh Tùng', N'tungvt@lib.edu.vn', N'0955666777', N'Thủ kho');

-- =========================
-- TÀI KHOẢN NHÂN VIÊN
-- =========================
INSERT INTO Account (AccountId, Username, Password, EmployeeId)
VALUES 
('TK002', N'binhtv', N'123', 'NV002'),
('TK003', N'mailt', N'123', 'NV003'),
('TK004', N'toanpq', N'123', 'NV004'),
('TK005', N'tungvt', N'123', 'NV005');

-- =========================
-- PHIẾU MƯỢN
-- =========================
INSERT INTO LoanSlip (LoanId, ReaderId, EmployeeId, LoanDate, ExpiredDate, ReturnDate, Status)
VALUES
('PM001', 'DG001', 'NV002', '2025-10-20', '2025-11-05', '2025-11-03', N'Đã trả'),
('PM002', 'DG002', 'NV002', '2025-10-25', '2025-11-10', NULL, N'Chưa trả'),
('PM003', 'DG004', 'NV002', '2025-10-15', '2025-10-30', '2025-10-29', N'Đã trả'),
('PM004', 'DG005', 'NV002', '2025-10-28', '2025-11-15', NULL, N'Chưa trả'),
('PM005', 'DG003', 'NV002', '2025-09-30', '2025-10-15', '2025-10-12', N'Đã trả'),
('PM006', 'DG006', 'NV002', '2025-10-02', '2025-10-22', '2025-10-20', N'Đã trả'),
('PM007', 'DG007', 'NV002', '2025-10-21', '2025-11-07', NULL, N'Chưa trả'),
('PM008', 'DG008', 'NV002', '2025-10-24', '2025-11-05', NULL, N'Chưa trả'),
('PM009', 'DG009', 'NV002', '2025-10-23', '2025-11-06', '2025-11-05', N'Đã trả'),
('PM010', 'DG010', 'NV002', '2025-10-29', '2025-11-12', NULL, N'Chưa trả');

-- =========================
-- CHI TIẾT MƯỢN
-- =========================
INSERT INTO LoanDetail (LoanDetailId, LoanId, BookId, LoanStatus, ReturnStatus, IsLose, Fine)
VALUES
('CT001', 'PM001', 'S001', N'Đã mượn', N'Đã trả', 0, 0),
('CT002', 'PM002', 'S003', N'Đã mượn', N'Chưa trả', 0, 0),
('CT003', 'PM003', 'S005', N'Đã mượn', N'Đã trả', 0, 0),
('CT004', 'PM004', 'S004', N'Đã mượn', N'Chưa trả', 0, 0),
('CT005', 'PM005', 'S002', N'Đã mượn', N'Đã trả', 0, 0),
('CT006', 'PM006', 'S005', N'Đã mượn', N'Đã trả', 0, 0),
('CT007', 'PM007', 'S001', N'Đã mượn', N'Chưa trả', 0, 0),
('CT008', 'PM008', 'S002', N'Đã mượn', N'Chưa trả', 0, 0),
('CT009', 'PM009', 'S003', N'Đã mượn', N'Đã trả', 0, 0),
('CT010', 'PM010', 'S004', N'Đã mượn', N'Chưa trả', 0, 0);

-- =========================
-- BẢNG SÁCH - NHIỀU TÁC GIẢ
-- =========================
INSERT INTO BookAuthor (BookId, AuthorId)
VALUES
('S001', 'TG001'),
('S001', 'TG002'),
('S002', 'TG003'),
('S002', 'TG004'),
('S003', 'TG003'),
('S004', 'TG004'),
('S004', 'TG005'),
('S005', 'TG001'),
('S005', 'TG006');

PRINT N'Dữ liệu mượn trả và tác giả đã được thêm thành công!';
GO
