// 应用程序主JavaScript文件

// 页面加载完成后执行
document.addEventListener('DOMContentLoaded', function() {
    // 初始化工具提示
    initTooltips();

    // 初始化确认对话框
    initConfirmDialogs();

    // 初始化表单验证
    initFormValidation();

    // 初始化搜索功能
    initSearch();

    // 初始化响应式表格
    initResponsiveTables();
});

/**
 * 初始化Bootstrap工具提示
 */
function initTooltips() {
    var tooltipTriggerList = [].slice.call(document.querySelectorAll('[data-bs-toggle="tooltip"]'));
    var tooltipList = tooltipTriggerList.map(function (tooltipTriggerEl) {
        return new bootstrap.Tooltip(tooltipTriggerEl);
    });
}

/**
 * 初始化确认对话框
 */
function initConfirmDialogs() {
    // 删除确认
    document.querySelectorAll('[data-confirm-delete]').forEach(function(element) {
        element.addEventListener('click', function(e) {
            e.preventDefault();
            const customerName = this.getAttribute('data-customer-name');
            const customerId = this.getAttribute('data-customer-id');

            if (confirm(`确定要删除顾客"${customerName}"吗？此操作不可撤销。`)) {
                // 创建并提交删除表单
                const form = document.createElement('form');
                form.method = 'POST';
                form.action = `/customers/${customerId}/delete`;
                document.body.appendChild(form);
                form.submit();
            }
        });
    });
}

/**
 * 初始化表单验证
 */
function initFormValidation() {
    // 获取所有需要验证的表单
    const forms = document.querySelectorAll('.needs-validation');

    // 为每个表单添加验证
    Array.prototype.slice.call(forms).forEach(function(form) {
        form.addEventListener('submit', function(event) {
            if (!form.checkValidity()) {
                event.preventDefault();
                event.stopPropagation();

                // 滚动到第一个错误字段
                const firstInvalidField = form.querySelector('.is-invalid');
                if (firstInvalidField) {
                    firstInvalidField.scrollIntoView({ behavior: 'smooth', block: 'center' });
                    firstInvalidField.focus();
                }
            }
            form.classList.add('was-validated');
        }, false);
    });

    // 手机号验证
    const phoneInputs = document.querySelectorAll('input[type="tel"]');
    phoneInputs.forEach(function(input) {
        input.addEventListener('input', function() {
            validatePhone(this);
        });
    });

    // 邮箱验证
    const emailInputs = document.querySelectorAll('input[type="email"]');
    emailInputs.forEach(function(input) {
        input.addEventListener('input', function() {
            validateEmail(this);
        });
    });
}

/**
 * 验证手机号
 */
function validatePhone(input) {
    const phonePattern = /^1[3-9]\d{9}$/;
    const value = input.value.trim();

    if (value && !phonePattern.test(value)) {
        input.setCustomValidity('请输入正确的手机号格式（以1开头的11位数字）');
        input.classList.add('is-invalid');
    } else {
        input.setCustomValidity('');
        input.classList.remove('is-invalid');
        if (value) {
            input.classList.add('is-valid');
        }
    }
}

/**
 * 验证邮箱
 */
function validateEmail(input) {
    const emailPattern = /^[^\s@]+@[^\s@]+\.[^\s@]+$/;
    const value = input.value.trim();

    if (value && !emailPattern.test(value)) {
        input.setCustomValidity('请输入正确的邮箱格式');
        input.classList.add('is-invalid');
    } else {
        input.setCustomValidity('');
        input.classList.remove('is-invalid');
        if (value) {
            input.classList.add('is-valid');
        }
    }
}

/**
 * 初始化搜索功能
 */
function initSearch() {
    // 搜索表单实时验证
    const searchForm = document.querySelector('#searchForm');
    if (searchForm) {
        const searchInputs = searchForm.querySelectorAll('input');
        searchInputs.forEach(function(input) {
            input.addEventListener('input', debounce(function() {
                // 可以在这里添加实时搜索逻辑
            }, 300));
        });
    }

    // 清除搜索
    const clearSearchBtn = document.querySelector('#clearSearch');
    if (clearSearchBtn) {
        clearSearchBtn.addEventListener('click', function() {
            const searchForm = document.querySelector('#searchForm');
            if (searchForm) {
                searchForm.reset();
                // 重新提交表单以清除搜索结果
                searchForm.submit();
            }
        });
    }
}

/**
 * 初始化响应式表格
 */
function initResponsiveTables() {
    // 为小屏幕设备添加表格滚动提示
    const tables = document.querySelectorAll('.table-responsive');
    tables.forEach(function(table) {
        // 检查表格是否需要滚动
        if (table.scrollWidth > table.clientWidth) {
            table.classList.add('table-scroll-hint');
        }
    });
}

/**
 * 防抖函数
 */
function debounce(func, wait) {
    let timeout;
    return function executedFunction(...args) {
        const later = () => {
            clearTimeout(timeout);
            func(...args);
        };
        clearTimeout(timeout);
        timeout = setTimeout(later, wait);
    };
}

/**
 * 显示Toast消息
 */
function showToast(message, type = 'success') {
    const toastContainer = getOrCreateToastContainer();

    const toastElement = document.createElement('div');
    toastElement.className = `toast align-items-center text-white bg-${type} border-0`;
    toastElement.setAttribute('role', 'alert');
    toastElement.setAttribute('aria-live', 'assertive');
    toastElement.setAttribute('aria-atomic', 'true');

    toastElement.innerHTML = `
        <div class="d-flex">
            <div class="toast-body">
                <i class="fas fa-${type === 'success' ? 'check' : 'exclamation-triangle'} me-2"></i>
                ${message}
            </div>
            <button type="button" class="btn-close btn-close-white me-2 m-auto" data-bs-dismiss="toast"></button>
        </div>
    `;

    toastContainer.appendChild(toastElement);

    const toast = new bootstrap.Toast(toastElement, {
        autohide: true,
        delay: 3000
    });

    toast.show();

    // 在Toast隐藏后移除元素
    toastElement.addEventListener('hidden.bs.toast', function() {
        toastContainer.removeChild(toastElement);
    });
}

/**
 * 获取或创建Toast容器
 */
function getOrCreateToastContainer() {
    let container = document.querySelector('.toast-container');
    if (!container) {
        container = document.createElement('div');
        container.className = 'toast-container position-fixed top-0 end-0 p-3';
        container.style.zIndex = '1055';
        document.body.appendChild(container);
    }
    return container;
}

/**
 * 复制文本到剪贴板
 */
function copyToClipboard(text) {
    if (navigator.clipboard) {
        navigator.clipboard.writeText(text).then(function() {
            showToast('内容已复制到剪贴板');
        }).catch(function(err) {
            console.error('复制失败:', err);
            fallbackCopyToClipboard(text);
        });
    } else {
        fallbackCopyToClipboard(text);
    }
}

/**
 * 降级复制方法
 */
function fallbackCopyToClipboard(text) {
    const textArea = document.createElement('textarea');
    textArea.value = text;
    textArea.style.position = 'fixed';
    textArea.style.left = '-999999px';
    textArea.style.top = '-999999px';
    document.body.appendChild(textArea);
    textArea.focus();
    textArea.select();

    try {
        document.execCommand('copy');
        showToast('内容已复制到剪贴板');
    } catch (err) {
        console.error('复制失败:', err);
        showToast('复制失败，请手动复制', 'danger');
    }

    document.body.removeChild(textArea);
}

/**
 * 格式化日期
 */
function formatDate(date, format = 'YYYY-MM-DD HH:mm:ss') {
    const d = new Date(date);
    const year = d.getFullYear();
    const month = String(d.getMonth() + 1).padStart(2, '0');
    const day = String(d.getDate()).padStart(2, '0');
    const hours = String(d.getHours()).padStart(2, '0');
    const minutes = String(d.getMinutes()).padStart(2, '0');
    const seconds = String(d.getSeconds()).padStart(2, '0');

    return format
        .replace('YYYY', year)
        .replace('MM', month)
        .replace('DD', day)
        .replace('HH', hours)
        .replace('mm', minutes)
        .replace('ss', seconds);
}

/**
 * 加载指示器
 */
function showLoading(element) {
    if (element) {
        element.disabled = true;
        const originalText = element.innerHTML;
        element.setAttribute('data-original-text', originalText);
        element.innerHTML = '<span class="spinner-border spinner-border-sm me-2"></span>加载中...';
    }
}

function hideLoading(element) {
    if (element) {
        element.disabled = false;
        const originalText = element.getAttribute('data-original-text');
        if (originalText) {
            element.innerHTML = originalText;
            element.removeAttribute('data-original-text');
        }
    }
}

/**
 * 页面滚动到顶部
 */
function scrollToTop() {
    window.scrollTo({
        top: 0,
        behavior: 'smooth'
    });
}

// 全局函数暴露
window.CustomerApp = {
    showToast,
    copyToClipboard,
    formatDate,
    showLoading,
    hideLoading,
    scrollToTop
};