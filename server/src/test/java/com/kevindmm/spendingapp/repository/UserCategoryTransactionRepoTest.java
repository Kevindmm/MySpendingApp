package com.kevindmm.spendingapp.repository;

import com.kevindmm.spendingapp.model.Category;
import com.kevindmm.spendingapp.model.Transaction;
import com.kevindmm.spendingapp.model.User;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.core.env.Environment;
import org.springframework.test.context.ActiveProfiles;

import java.time.LocalDate;
import java.util.Arrays;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@ActiveProfiles("test")
@DataJpaTest
public class UserCategoryTransactionRepoTest {

    @Autowired
    private UserRepository userRepo;

    @Autowired
    private CategoryRepository catRepo;

    @Autowired
    private TransactionRepository transRepo;

    @BeforeAll
    static void before(@Autowired Environment env) {
        System.out.println("activeProfiles=" + Arrays.toString(env.getActiveProfiles()));
        System.out.println("datasource.url=" + env.getProperty("spring.datasource.url"));
    }

    @Test
    void contextLoads() {
        // This test will pass if the application context loads successfully
    }

    @Test
    void saveAndFetchTransactionByUserAndCategory() {
        // given: a user and a category
        User user = new User();
        user.setEmail("test@example.com");
        user.setPasswordHash("hashedpass");
        userRepo.save(user);

        Category category = new Category();
        category.setName("Groceries");
        category.setUser(user);
        catRepo.save(category);

        // and: a transaction linked to both
        Transaction tx = new Transaction();
        tx.setAmount(42.50f);
        tx.setCurrency("EUR");
        tx.setDate(LocalDate.now());
        tx.setNote("Weekly shopping");
        tx.setUser(user);
        tx.setCategory(category);
        transRepo.save(tx);

        // when: fetching by user & category
        List<Transaction> found = transRepo.findByUserIdAndCategoryId(user.getId(), category.getId());

        // then: should return the saved transaction
        assertThat(found).hasSize(1);
        assertThat(found.get(0).getAmount()).isEqualTo(42.50f);
        assertThat(found.get(0).getUser().getEmail()).isEqualTo("test@example.com");
        assertThat(found.get(0).getCategory().getName()).isEqualTo("Groceries");
        assertThat(found.get(0).getNote()).isEqualTo("Weekly shopping");
    }
}
