package com.example.sbbTest.category;

import com.example.sbbTest.DataNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@RequiredArgsConstructor
@Service
public class CategoryService {

    private final CategoryRepository categoryRepository;

    public Category create(String name){
        Category category = new Category();
        category.setName(name);
        this.categoryRepository.save(category);
        return category;
    }

    public List<Category> getAll(){
        return this.categoryRepository.findAll();
    }

    public Category getCategoryByName(String name){
        Optional<Category> oc = this.categoryRepository.findByName(name);
        if (oc.isPresent()){
            return oc.get();
        }else {
            throw new DataNotFoundException("category not found");
        }
    }
}
