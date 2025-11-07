using System;
using System.Collections.Generic;
using System.ComponentModel.DataAnnotations;

namespace ManageLibrary.Models
{
    public partial class Publisher
    {
        // PublisherId: Bắt buộc, có thể yêu cầu độ dài tối đa
        [Required(ErrorMessage = "Mã nhà xuất bản là bắt buộc.")]
        [StringLength(20, ErrorMessage = "Mã nhà xuất bản không được vượt quá 20 ký tự.")]
        public string PublisherId { get; set; } = null!;

        // Name: Bắt buộc, có thể yêu cầu độ dài tối đa
        [Required(ErrorMessage = "Tên nhà xuất bản là bắt buộc.")]
        [StringLength(100, ErrorMessage = "Tên nhà xuất bản không được vượt quá 100 ký tự.")]
        public string Name { get; set; } = null!;

        // Address: Không bắt buộc nhưng nếu có thì có thể yêu cầu độ dài tối đa
        [StringLength(200, ErrorMessage = "Địa chỉ không được vượt quá 200 ký tự.")]
        public string? Address { get; set; }

        // Telephone: Không bắt buộc nhưng nếu có phải theo định dạng số điện thoại hợp lệ
        [Phone(ErrorMessage = "Số điện thoại không hợp lệ.")]
        public string? Telephone { get; set; }

        // Navigation Property
        public virtual ICollection<Book> Books { get; set; } = new List<Book>();
    }
}
