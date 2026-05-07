package com.example.back.controllers.transactions.transaction;

import static org.hamcrest.Matchers.is;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.verify;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;

import com.example.back.controllers.GlobalExceptionController;
import com.example.back.controllers.TransactionController;
import com.example.back.dto.transaction.transaction.CreateTransactionRequestDTO;
import com.example.back.entities.auth.Role;
import com.example.back.entities.transactions.Account;
import com.example.back.entities.user.User;
import com.example.back.enums.TransactionType;

@WebMvcTest(controllers = TransactionController.class)
@Import(GlobalExceptionController.class)
@WithMockUser(username = "test@gmail.com")
public class TransactionControllerCreateTest extends TransactionControllerBase {

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
        void shouldCreateTransactionWithValidFieldsTypeDeposit() throws Exception {
                CreateTransactionRequestDTO request = new CreateTransactionRequestDTO(
                                "test concept", 6.99,
                                destination.getAccountNumber(), null,
                                TransactionType.DEPOSIT);

                doNothing().when(transactionServices).createTransaction(any(), any());

                mockMvc.perform(post("/transaction/create")
                                .with(user("test@gmail.com").roles("CLIENT"))
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(request)))
                                .andExpect(status().isOk())
                                .andExpect(jsonPath("$.message", is("Transacción creada")))
                                .andExpect(jsonPath("$.status", is(201)));
        }

        @Test
        void shouldCreateTransactionWithValidFieldsTypeWithdrawal() throws Exception {
                CreateTransactionRequestDTO request = new CreateTransactionRequestDTO(
                                "test concept",
                                6.99,
                                null,
                                origin.getAccountNumber(),
                                TransactionType.WITHDRAWAL);

                doNothing().when(transactionServices).createTransaction(any(), any());

                mockMvc.perform(post("/transaction/create")
                                .with(user("test@gmail.com").roles("CLIENT"))
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(request)))
                                .andExpect(status().isOk())
                                .andExpect(jsonPath("$.message", is("Transacción creada")))
                                .andExpect(jsonPath("$.status", is(201)));

                verify(transactionServices).createTransaction(any(), any());
        }

        @Test
        void shouldCreateTransactionWithValidFieldsTypeTransfer() throws Exception {
                CreateTransactionRequestDTO request = new CreateTransactionRequestDTO(
                                "test concept",
                                6.99,
                                destination.getAccountNumber(),
                                origin.getAccountNumber(),
                                TransactionType.TRANSFER);

                doNothing().when(transactionServices).createTransaction(any(), any());

                mockMvc.perform(post("/transaction/create")
                                .with(user("test@gmail.com").roles("CLIENT"))
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(request)))
                                .andExpect(status().isOk())
                                .andExpect(jsonPath("$.message", is("Transacción creada")))
                                .andExpect(jsonPath("$.status", is(201)));

                verify(transactionServices).createTransaction(any(), any());
        }

        @Test
        void shouldReturnBadRequestIfDestinationAndOriginAreNull() throws Exception {
                CreateTransactionRequestDTO request = new CreateTransactionRequestDTO(
                                "test concept",
                                6.99,
                                null,
                                null,
                                TransactionType.DEPOSIT);

                doThrow(new IllegalArgumentException())
                                .when(transactionServices).createTransaction(any(), any());

                mockMvc.perform(post("/transaction/create")
                                .with(user("test@gmail.com").roles("CLIENT"))
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(request)))
                                .andDo(print())
                                .andExpect(status().isBadRequest());
        }

}
