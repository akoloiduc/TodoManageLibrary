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

