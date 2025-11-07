using ManageLibrary.Models;
using Microsoft.AspNetCore.Mvc;
using System;
using System.Linq;
using System.Threading.Tasks;
using Microsoft.EntityFrameworkCore;

namespace ManageLibrary.Controllers
{
    public class SignUpController : Controller
    {
        private readonly ManageLibraryContext _context;

        public SignUpController(ManageLibraryContext context)
        {
            _context = context;
        }

        [HttpGet]
        public IActionResult Index()
        {
            var model = new Tuple<Account, Reader>(new Account(), new Reader());
            return View(model);
        }

        [HttpPost]
        [ValidateAntiForgeryToken]
        public async Task<IActionResult> Index(
            [Bind(Prefix = "Item1")] Account account,
            [Bind(Prefix = "Item2")] Reader reader,
            string confirmPassword)
        {
            var random = new Random();

            // 1. Validate tùy chỉnh
            if (account.Password != confirmPassword)
            {
                ModelState.AddModelError("confirmPassword", "Mật khẩu xác nhận không khớp.");
            }
            if (await _context.Accounts.AnyAsync(a => a.Username == account.Username))
            {
                ModelState.AddModelError("Item1.Username", "Tên đăng nhập này đã tồn tại.");
            }
            if (await _context.Readers.AnyAsync(r => r.Email == reader.Email))
            {
                ModelState.AddModelError("Item2.Email", "Email này đã được sử dụng.");
            }
            if (await _context.Readers.AnyAsync(r => r.NationalId == reader.NationalId))
            {
                ModelState.AddModelError("Item2.NationalId", "Số CCCD/CMND này đã được sử dụng.");
            }

            string newReaderId;
            bool readerIdExists;
            do
            {
                string msv = random.Next(100000000, 999999999).ToString();
                newReaderId = "DG" + msv;
                readerIdExists = await _context.Readers.AnyAsync(r => r.ReaderId == newReaderId);
            } while (readerIdExists);

            reader.ReaderId = newReaderId;

            string newAccountId;
            bool accountIdExists;
            do
            {
                newAccountId = "AC" + random.Next(100000, 999999).ToString();
                accountIdExists = await _context.Accounts.AnyAsync(a => a.AccountId == newAccountId);
            } while (accountIdExists);

            account.AccountId = newAccountId;
            account.ReaderId = newReaderId;


            ModelState.Remove("Item1.AccountId");
            ModelState.Remove("Item1.ReaderId");
            ModelState.Remove("Item1.EmployeeId");
            ModelState.Remove("Item2.ReaderId");

            if (ModelState.IsValid)
            {
                using (var transaction = await _context.Database.BeginTransactionAsync())
                {
                    try
                    {
                        _context.Readers.Add(reader);
                        _context.Accounts.Add(account);

                        await _context.SaveChangesAsync();

                        await transaction.CommitAsync();

                        TempData["SuccessMessage"] = "Đăng ký tài khoản thành công! Vui lòng đăng nhập.";
                        return RedirectToAction("Index", "Login");
                    }
                    catch (Exception ex)
                    {
                        await transaction.RollbackAsync();
                        ModelState.AddModelError("", "Đã xảy ra lỗi không mong muốn. Vui lòng thử lại. " + ex.Message);
                    }
                }
            }

            var model = new Tuple<Account, Reader>(account, reader);
            return View(model);
        }
    }
}