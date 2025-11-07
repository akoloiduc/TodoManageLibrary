using Azure.Core;
using ManageLibrary.Models;
using Microsoft.AspNetCore.Authorization;
using Microsoft.AspNetCore.Mvc;
using Microsoft.AspNetCore.Mvc.Rendering;
using Microsoft.EntityFrameworkCore;
using System.Threading.Tasks;
// Thêm namespace này để dùng ToListAsync()
using System.Linq;

namespace ManageLibrary.Controllers
{
    [Route("Home")]
    public class ClientPageController : Controller
    {
        private readonly ManageLibraryContext _context;

        public ClientPageController(ManageLibraryContext context)
        {
            _context = context;
        }

        // === SỬA ĐỔI CHÍNH Ở ĐÂY ===
        // Chuyển sang async và truy vấn CSDL
        public async Task<IActionResult> Index(string query) // Thêm tham số 'query'
        {
            // 1. Lưu lại query để hiển thị trên ô tìm kiếm
            ViewData["CurrentFilter"] = query;

            // 2. Bắt đầu câu truy vấn
            var booksQuery = _context.Books
                                .AsNoTracking()
                                .Include(b => b.Author)
                                .Include(b => b.Category)
                                .AsQueryable();

            // 3. (Logic của Search) Áp dụng bộ lọc nếu có query
            if (!String.IsNullOrEmpty(query))
            {
                string searchLower = query.ToLower();
                booksQuery = booksQuery.Where(b =>
                    b.Name.ToLower().Contains(searchLower) ||
                    (b.Author != null && b.Author.Name.ToLower().Contains(searchLower))
                );
            }

            // 4. Lấy kết quả cuối cùng
            var bookList = await booksQuery
                                .OrderBy(b => b.Name)
                                .ToListAsync();

            // 5. Tải dropdowns (nếu cần)
            await PopulateDropdowns();

            // 6. Gửi danh sách đã lọc (hoặc đầy đủ) sang View
            return View(bookList);
        }

        // Action 'Details' giữ nguyên
        // === THÊM DÒNG NÀY VÀO ===
        [HttpGet("Details/{id}")]
        public async Task<IActionResult> Details(string id)
        {
            // ... (giữ nguyên code Details của bạn) ...
            if (id == null)
            {
                return NotFound();
            }

            var book = await _context.Books
                .Include(b => b.Author)
                .Include(b => b.Category)
                .Include(b => b.Publisher)
                .AsNoTracking()
                .FirstOrDefaultAsync(m => m.BookId == id);

            if (book == null)
            {
                return NotFound();
            }

            return View(book);
        }
        private async Task PopulateDropdowns(string? selectedAuthorId = null, string? selectedPublisherId = null, string? selectedCategoryId = null)
        {
            // Tải danh sách Tác giả
            ViewBag.Authors = new SelectList(await _context.Authors.AsNoTracking().OrderBy(a => a.Name).ToListAsync(),
                "AuthorId", "Name", selectedAuthorId);

            // Tải danh sách Nhà xuất bản
            ViewBag.Publishers = new SelectList(await _context.Publishers.AsNoTracking().OrderBy(p => p.Name).ToListAsync(),
                "PublisherId", "Name", selectedPublisherId);

            // Tải danh sách Thể loại
            ViewBag.Categories = new SelectList(await _context.Categories.AsNoTracking().OrderBy(c => c.Name).ToListAsync(),
                "CategoryId", "Name", selectedCategoryId);
        }
    }
}