CREATE DATABASE QLThuVien;
GO
USE QLThuVien;
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
