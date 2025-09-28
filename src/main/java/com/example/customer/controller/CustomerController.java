package com.example.customer.controller;

import com.example.customer.entity.Customer;
import com.example.customer.service.CustomerService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import javax.validation.Valid;
import java.util.Optional;

@Controller
@RequestMapping("/customers")
public class CustomerController {

    private final CustomerService customerService;

    @Autowired
    public CustomerController(CustomerService customerService) {
        this.customerService = customerService;
    }

    @GetMapping
    public String listCustomers(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "id") String sortBy,
            @RequestParam(defaultValue = "asc") String sortDir,
            @RequestParam(required = false) String name,
            @RequestParam(required = false) String phone,
            @RequestParam(required = false) String email,
            Model model) {

        Sort sort = sortDir.equalsIgnoreCase("desc") ?
                Sort.by(sortBy).descending() :
                Sort.by(sortBy).ascending();

        Pageable pageable = PageRequest.of(page, size, sort);
        Page<Customer> customerPage = customerService.findCustomersWithFilters(name, phone, email, pageable);

        model.addAttribute("customerPage", customerPage);
        model.addAttribute("currentPage", page);
        model.addAttribute("pageSize", size);
        model.addAttribute("sortBy", sortBy);
        model.addAttribute("sortDir", sortDir);
        model.addAttribute("reverseSortDir", sortDir.equals("asc") ? "desc" : "asc");

        model.addAttribute("searchName", name);
        model.addAttribute("searchPhone", phone);
        model.addAttribute("searchEmail", email);

        model.addAttribute("totalCustomers", customerService.getTotalCustomerCount());

        return "customers/list";
    }

    @GetMapping("/new")
    public String showCreateForm(Model model) {
        model.addAttribute("customer", new Customer());
        model.addAttribute("isEdit", false);
        model.addAttribute("genders", Customer.Gender.values());
        return "customers/form";
    }

    @PostMapping
    public String createCustomer(@Valid @ModelAttribute Customer customer,
                                BindingResult result,
                                RedirectAttributes redirectAttributes,
                                Model model) {
        if (result.hasErrors()) {
            model.addAttribute("isEdit", false);
            model.addAttribute("genders", Customer.Gender.values());
            return "customers/form";
        }

        try {
            customerService.saveCustomer(customer);
            redirectAttributes.addFlashAttribute("successMessage", "客户创建成功！");
            return "redirect:/customers";
        } catch (RuntimeException e) {
            model.addAttribute("errorMessage", e.getMessage());
            model.addAttribute("isEdit", false);
            model.addAttribute("genders", Customer.Gender.values());
            return "customers/form";
        }
    }

    @GetMapping("/{id}")
    public String showCustomer(@PathVariable Long id, Model model, RedirectAttributes redirectAttributes) {
        Optional<Customer> customer = customerService.findCustomerById(id);
        if (customer.isPresent()) {
            model.addAttribute("customer", customer.get());
            return "customers/detail";
        } else {
            redirectAttributes.addFlashAttribute("errorMessage", "客户不存在！");
            return "redirect:/customers";
        }
    }

    @GetMapping("/{id}/edit")
    public String showEditForm(@PathVariable Long id, Model model, RedirectAttributes redirectAttributes) {
        Optional<Customer> customer = customerService.findCustomerById(id);
        if (customer.isPresent()) {
            model.addAttribute("customer", customer.get());
            model.addAttribute("isEdit", true);
            model.addAttribute("genders", Customer.Gender.values());
            return "customers/form";
        } else {
            redirectAttributes.addFlashAttribute("errorMessage", "客户不存在！");
            return "redirect:/customers";
        }
    }

    @PostMapping("/{id}")
    public String updateCustomer(@PathVariable Long id,
                                @Valid @ModelAttribute Customer customer,
                                BindingResult result,
                                RedirectAttributes redirectAttributes,
                                Model model) {
        if (result.hasErrors()) {
            model.addAttribute("isEdit", true);
            model.addAttribute("genders", Customer.Gender.values());
            return "customers/form";
        }

        try {
            customer.setId(id);
            customerService.updateCustomer(customer);
            redirectAttributes.addFlashAttribute("successMessage", "客户信息更新成功！");
            return "redirect:/customers/" + id;
        } catch (RuntimeException e) {
            model.addAttribute("errorMessage", e.getMessage());
            model.addAttribute("isEdit", true);
            model.addAttribute("genders", Customer.Gender.values());
            return "customers/form";
        }
    }

    @PostMapping("/{id}/delete")
    public String deleteCustomer(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        try {
            customerService.deleteCustomer(id);
            redirectAttributes.addFlashAttribute("successMessage", "客户删除成功！");
        } catch (RuntimeException e) {
            redirectAttributes.addFlashAttribute("errorMessage", e.getMessage());
        }
        return "redirect:/customers";
    }

    @GetMapping("/search")
    public String searchCustomers(@RequestParam String query, Model model) {
        model.addAttribute("customers", customerService.findCustomersByName(query));
        model.addAttribute("searchQuery", query);
        return "customers/search-results";
    }
}

@RestController
@RequestMapping("/api/customers")
class CustomerRestController {

    private final CustomerService customerService;

    @Autowired
    public CustomerRestController(CustomerService customerService) {
        this.customerService = customerService;
    }

    @GetMapping
    public Page<Customer> getAllCustomers(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "id") String sortBy,
            @RequestParam(defaultValue = "asc") String sortDir) {

        Sort sort = sortDir.equalsIgnoreCase("desc") ?
                Sort.by(sortBy).descending() :
                Sort.by(sortBy).ascending();

        Pageable pageable = PageRequest.of(page, size, sort);
        return customerService.findCustomersWithPagination(pageable);
    }

    @PostMapping
    public Customer createCustomer(@Valid @RequestBody Customer customer) {
        return customerService.saveCustomer(customer);
    }

    @GetMapping("/{id}")
    public Customer getCustomer(@PathVariable Long id) {
        return customerService.findCustomerById(id)
                .orElseThrow(() -> new RuntimeException("客户不存在, ID: " + id));
    }

    @PutMapping("/{id}")
    public Customer updateCustomer(@PathVariable Long id, @Valid @RequestBody Customer customer) {
        customer.setId(id);
        return customerService.updateCustomer(customer);
    }

    @DeleteMapping("/{id}")
    public void deleteCustomer(@PathVariable Long id) {
        customerService.deleteCustomer(id);
    }
}