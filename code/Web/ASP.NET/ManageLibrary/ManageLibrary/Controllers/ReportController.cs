using ManageLibrary.Models;
using Microsoft.AspNetCore.Mvc;
using Microsoft.EntityFrameworkCore;
using System;
using System.Linq;
using System.Threading.Tasks;

namespace ManageLibrary.Controllers
{
    // Đặt tên Controller này tùy theo cách bạn tổ chức
    [Route("/Admin/Report")]
    public class ReportController : Controller
    {
        private readonly ManageLibraryContext _context;

        public ReportController(ManageLibraryContext context)
        {
            _context = context;
        }

        [HttpGet]
        public async Task<IActionResult> Index()
        {
            // Lấy ngày hôm nay (chuyển sang DateOnly để so sánh)
            var today = DateOnly.FromDateTime(DateTime.Today);

            // --- LOGIC TÍNH TOÁN ĐỘNG ---

            // 1. (SỬA) Phiếu Đang Mượn (Chưa trả VÀ chưa tới hạn)
            int currentLoans = await _context.LoanSlips
                .CountAsync(l => l.ReturnDate == null && l.ExpiredDate >= today);

            // 2. (SỬA) Phiếu Quá Hạn (Chưa trả VÀ đã qua ngày hết hạn)
            int overdueLoans = await _context.LoanSlips
                .CountAsync(l => l.ReturnDate == null && l.ExpiredDate < today);

            // 3. Tổng số đầu sách (đếm số BookId)
            int totalBooks = await _context.Books.CountAsync();

            // (Nếu bạn muốn đếm tổng số *bản sao* sách còn lại, dùng:
            // int totalBookCopies = await _context.Books.SumAsync(b => b.Quantity);

            // 4. Tổng số độc giả
            int totalReaders = await _context.Readers.CountAsync();

            // 5. Top sách mượn (đây là logic phức tạp hơn, cần DTO)
            var topLoanedBooks = await _context.LoanDetails
                .GroupBy(ld => new { ld.BookId, ld.Book.Name })
                .Select(g => new BookStatsViewModel // Sửa tên class thành BookStatsViewModel
                {
                    BookName = g.Key.Name,
                    LoanCount = g.Count()
                })
                .OrderByDescending(g => g.LoanCount)
                .Take(10)
                .ToListAsync();

            // 6. Sách theo thể loại (cũng cần DTO)
            var booksByCategory = await _context.Books
                .GroupBy(b => new { b.CategoryId, b.Category.Name })
                .Select(g => new CategoryStatsViewModel // Sửa tên class thành CategoryStatsViewModel
                {
                    CategoryName = g.Key.Name,
                    BookCount = g.Count() // Đếm số *loại* sách
                })
                .ToListAsync();


            // Tạo ViewModel để trả về
            var model = new ReportViewModel
            {
                TotalBooks = totalBooks,
                TotalReaders = totalReaders,
                CurrentLoans = currentLoans, // Gán giá trị ĐÚNG
                OverdueLoans = overdueLoans, // Gán giá trị ĐÚNG
                TopLoanedBooks = topLoanedBooks,
                BooksByCategory = booksByCategory
            };

            return View(model); // Trả về View bạn đã cung cấp
        }
    }

   
}