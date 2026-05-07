package com.example.back.controllers.transactions.transaction;

import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.time.LocalDate;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.security.test.context.support.WithMockUser;

import com.example.back.controllers.GlobalExceptionController;
import com.example.back.controllers.TransactionController;
import com.example.back.dto.transaction.transaction.TransactionResponseDTO;
import com.example.back.entities.auth.Role;
import com.example.back.entities.transactions.Account;
import com.example.back.entities.transactions.Transaction;
import com.example.back.entities.user.User;
import com.example.back.enums.TransactionType;

@WebMvcTest(controllers = TransactionController.class)
@Import(GlobalExceptionController.class)
@WithMockUser(username = "test@gmail.com")
public class TransactionControllerGetTest extends TransactionControllerBase {

        private User user;
        private Account destination;
        private Account origin;

        @BeforeEach
        void setUp() {
                user = new User("test", "test@gmail.com", "123456789", new Role());
                destination = new Account(150.50, user, "1234567899876543211234");
                origin = new Account(350.69, user, "9876543211234567899516");

                user.setId(1L);
                destination.setId(1L);
                origin.setId(2L);
        }

        @Test
        void shouldReturnTransactionPageByUserId() throws Exception {
                Transaction transaction = new Transaction(
                                "test concept", 6.99, LocalDate.now(),
                                TransactionType.DEPOSIT, user, destination, null);
                transaction.setId(1L);

                Page<Transaction> transactionPage = new PageImpl<>(List.of(transaction));

                when(transactionServices.getAllTransaction(user.getEmail(), 0, 25))
                                .thenReturn(transactionPage);

                when(transactionMapper.toDto(transaction))
                                .thenReturn(new TransactionResponseDTO());

                mockMvc.perform(get("/transaction")
                                .with(user("test@gmail.com").roles("CLIENT")))

                                .andExpect(status().isOk())
                                .andExpect(jsonPath("$.content").isArray())
                                .andExpect(jsonPath("$.content.length()").value(1))
                                .andExpect(jsonPath("$.currentPage").value(0))
                                .andExpect(jsonPath("$.totalElements").value(1))
                                .andExpect(jsonPath("$.totalPages").value(1));
        }

        @Test
        void shouldReturnEmptyPageIfUserHasNoTransactions() throws Exception {
                Page<Transaction> emptyPage = new PageImpl<>(List.of());

                when(transactionServices.getAllTransaction(user.getEmail(), 0, 25))
                                .thenReturn(emptyPage);

                mockMvc.perform(get("/transaction").with(user("test@gmail.com").roles("CLIENT")))
                                .andExpect(status().isOk())
                                .andExpect(jsonPath("$.content").isArray())
                                .andExpect(jsonPath("$.content.length()").value(0))
                                .andExpect(jsonPath("$.totalElements").value(0));
        }
}
