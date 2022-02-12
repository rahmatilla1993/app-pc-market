package com.example.apppcmarket.controller;

import com.example.apppcmarket.entity.Product;
import com.example.apppcmarket.enums.Elements;
import com.example.apppcmarket.model.ProductDTO;
import com.example.apppcmarket.model.Result;
import com.example.apppcmarket.service.ProductService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;

@RestController
@RequestMapping("/api/product")
public class ProductController {

    Elements messageProduct = Elements.PRODUCT;
    Elements messageCategory = Elements.CATEGORY;

    @Autowired
    ProductService productService;

    @GetMapping
    //@PreAuthorize(value = "hasAnyRole('MODERATOR','OPERATOR','SUPER_ADMIN')")
    //@PreAuthorize(value = "hasAuthority('READ_PRODUCTS')")
    public HttpEntity<?> getAllProducts(@RequestParam int page) {
        Page<Product> allProducts = productService.getAllProducts(page);
        return ResponseEntity.ok(allProducts);
    }

    @GetMapping("/{id}")
    // @PreAuthorize(value = "hasAuthority('READ_ONE_PRODUCT')")
    //@PreAuthorize(value = "hasAnyRole('MODERATOR','OPERATOR','SUPER_ADMIN')")
    public HttpEntity<?> getProductById(@PathVariable Integer id) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        Result result = productService.getProductById(id);
        return ResponseEntity.status(result.isSuccess() ? HttpStatus.OK : HttpStatus.NOT_FOUND).body(result);
    }

    @GetMapping("/byCategoryId/{category_id}")
    //@PreAuthorize(value = "hasAuthority('READ_PRODUCTS_BY_CATEGORY')")
    //@PreAuthorize(value = "hasAnyRole('MODERATOR','OPERATOR','SUPER_ADMIN')")
    public HttpEntity<?> getProductsByCategoryId(@PathVariable Integer category_id) {
        List<Result> results = productService.getProductsByCategoryId(category_id);
        return ResponseEntity.status(results.get(0).isSuccess() ? HttpStatus.OK : HttpStatus.NOT_FOUND).body(results);
    }

    @PostMapping
    //@PreAuthorize(value = "hasAuthority('ADD_PRODUCT')")
    //@PreAuthorize(value = "hasAnyRole('MODERATOR','OPERATOR','SUPER_ADMIN')")
    public HttpEntity<?> addProduct(@Valid @RequestBody ProductDTO productDTO) {
        Result result = productService.addProduct(productDTO);
        return ResponseEntity.status(result.isSuccess() ? HttpStatus.CREATED : result.getMessage().equals(messageCategory.getElementIsNotActive()) ?
                HttpStatus.FORBIDDEN : result.getMessage().equals(messageProduct.getElementExists()) ? HttpStatus.CONFLICT : HttpStatus.NOT_FOUND).body(result);
    }

    @PutMapping("/{id}")
    //@PreAuthorize(value = "hasAuthority('EDIT_PRODUCT')")
    @PreAuthorize(value = "hasAnyRole('MODERATOR','SUPER_ADMIN')")
    public HttpEntity<?> editProductById(@PathVariable Integer id, @Valid @RequestBody ProductDTO productDTO) {
        Result result = productService.editProductById(id, productDTO);
        return ResponseEntity.status(result.isSuccess() ? HttpStatus.ACCEPTED : result.getMessage().equals(messageCategory.getElementIsNotActive()) ?
                HttpStatus.FORBIDDEN : result.getMessage().equals(messageProduct.getElementExists()) ? HttpStatus.CONFLICT : HttpStatus.NOT_FOUND).body(result);
    }

    @DeleteMapping("/{id}")
    //@PreAuthorize(value = "hasAuthority('DELETE_PRODUCT')")
    @PreAuthorize(value = "hasRole('SUPER_ADMIN')")
    public HttpEntity<?> deleteProductById(@PathVariable Integer id) {
        Result result = productService.deleteProductById(id);
        return ResponseEntity.status(result.isSuccess() ? HttpStatus.ACCEPTED : HttpStatus.NOT_FOUND).body(result);
    }
}
