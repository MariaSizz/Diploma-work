package java.com.integrationTests;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.trade.tradeApplication.entity.AdEntity;
import com.trade.tradeApplication.entity.CommentEntity;
import com.trade.tradeApplication.entity.UserEntity;
import com.trade.tradeApplication.model.CreateOrUpdateComment;
import com.trade.tradeApplication.model.Role;
import com.trade.tradeApplication.repository.AdRepository;
import com.trade.tradeApplication.repository.CommentRepository;
import com.trade.tradeApplication.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest(classes = com.trade.tradeApplication.TradeApplication.class)
@AutoConfigureMockMvc
@Transactional
@ActiveProfiles("test")
class CommentsControllerIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private CommentRepository commentRepository;

    @Autowired
    private AdRepository adRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    private UserEntity testUser;
    private UserEntity adminUser;
    private AdEntity testAd;
    private CommentEntity testComment;
    private Authentication userAuth;
    private Authentication adminAuth;

    @BeforeEach
    void setUp() {
        commentRepository.deleteAll();
        adRepository.deleteAll();
        userRepository.deleteAll();

        testUser = new UserEntity();
        testUser.setUsername("testuser@example.com");
        testUser.setPassword(passwordEncoder.encode("password123"));
        testUser.setFirstName("John");
        testUser.setLastName("Doe");
        testUser.setPhone("+79123456789");
        testUser.setRole(Role.USER);
        testUser.setImage("user-avatar.jpg");
        testUser = userRepository.save(testUser);

        adminUser = new UserEntity();
        adminUser.setUsername("admin@example.com");
        adminUser.setPassword(passwordEncoder.encode("admin123"));
        adminUser.setFirstName("Admin");
        adminUser.setLastName("User");
        adminUser.setPhone("+79999999999");
        adminUser.setRole(Role.ADMIN);
        adminUser.setImage("admin-avatar.jpg");
        adminUser = userRepository.save(adminUser);

        testAd = new AdEntity();
        testAd.setAuthor(testUser);
        testAd.setTitle("Test Ad");
        testAd.setPrice(1000);
        testAd.setDescription("Test Description");
        testAd.setImage("test-image.jpg");
        testAd = adRepository.save(testAd);

        testComment = new CommentEntity();
        testComment.setAuthor(testUser);
        testComment.setAd(testAd);
        testComment.setText("Test comment text");
        testComment.setCreatedAt(System.currentTimeMillis());
        testComment = commentRepository.save(testComment);

        com.trade.tradeApplication.config.CustomUserDetails userDetails =
                new com.trade.tradeApplication.config.CustomUserDetails(testUser);
        userAuth = new org.springframework.security.authentication.UsernamePasswordAuthenticationToken(
                userDetails, null, userDetails.getAuthorities()
        );

        com.trade.tradeApplication.config.CustomUserDetails adminDetails =
                new com.trade.tradeApplication.config.CustomUserDetails(adminUser);
        adminAuth = new org.springframework.security.authentication.UsernamePasswordAuthenticationToken(
                adminDetails, null, adminDetails.getAuthorities()
        );
    }

    private void setAuthentication(Authentication authentication) {
        org.springframework.security.core.context.SecurityContextHolder.getContext().setAuthentication(authentication);
    }

    @Test
    void getComments_WhenAdExists_ShouldReturnComments() throws Exception {
        setAuthentication(userAuth);

        System.out.println("=== DEBUG INFO ===");
        System.out.println("Test Ad ID: " + testAd.getPk());
        System.out.println("Test Comment ID: " + testComment.getPk());
        System.out.println("Comment Ad ID: " + (testComment.getAd() != null ? testComment.getAd().getPk() : "NULL"));


        List<CommentEntity> allComments = commentRepository.findAll();
        System.out.println("Total comments in DB: " + allComments.size());
        allComments.forEach(c -> System.out.println("Comment " + c.getPk() + " -> Ad: " +
                (c.getAd() != null ? c.getAd().getPk() : "NULL")));

        mockMvc.perform(get("/ads/{adId}/comments", testAd.getPk()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.count").value(1))
                .andExpect(jsonPath("$.results[0].pk").value(testComment.getPk()))
                .andExpect(jsonPath("$.results[0].text").value("Test comment text"))
                .andExpect(jsonPath("$.results[0].author").value(testUser.getId()))
                .andExpect(jsonPath("$.results[0].authorFirstName").value("John"))
                .andExpect(jsonPath("$.results[0].authorImage").value("user-avatar.jpg"));
    }

    @Test
    void getComments_WhenAdNotExists_ShouldReturnNotFound() throws Exception {
        setAuthentication(userAuth);

        mockMvc.perform(get("/ads/{adId}/comments", 999))
                .andExpect(status().isNotFound());
    }

    @Test
    void getComments_WhenNoComments_ShouldReturnEmptyList() throws Exception {
        setAuthentication(userAuth); // Добавляем аутентификацию

        AdEntity newAd = new AdEntity();
        newAd.setAuthor(testUser);
        newAd.setTitle("New Ad");
        newAd.setPrice(2000);
        newAd.setDescription("New Description");
        newAd.setImage("new-image.jpg");
        newAd = adRepository.save(newAd);

        mockMvc.perform(get("/ads/{adId}/comments", newAd.getPk()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.count").value(0))
                .andExpect(jsonPath("$.results").isArray())
                .andExpect(jsonPath("$.results").isEmpty());
    }

    @Test
    void getComments_WhenNotAuthenticated_ShouldReturnUnauthorized() throws Exception {
        // Не устанавливаем аутентификацию
        mockMvc.perform(get("/ads/{adId}/comments", testAd.getPk()))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void addComment_WhenAuthenticatedAndValidData_ShouldCreateComment() throws Exception {
        setAuthentication(userAuth);

        CreateOrUpdateComment newComment = new CreateOrUpdateComment();
        newComment.setText("New comment text");

        mockMvc.perform(post("/ads/{adId}/comments", testAd.getPk())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(newComment)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.text").value("New comment text"))
                .andExpect(jsonPath("$.author").value(testUser.getId()))
                .andExpect(jsonPath("$.authorFirstName").value("John"))
                .andExpect(jsonPath("$.authorImage").value("user-avatar.jpg"))
                .andExpect(jsonPath("$.pk").exists())
                .andExpect(jsonPath("$.createdAt").exists());

        List<CommentEntity> comments = commentRepository.findByAdPk(testAd.getPk());
        assert comments.size() == 2;
    }

    @Test
    void addComment_WhenNotAuthenticated_ShouldReturnUnauthorized() throws Exception {
        CreateOrUpdateComment newComment = new CreateOrUpdateComment();
        newComment.setText("New comment text");

        mockMvc.perform(post("/ads/{adId}/comments", testAd.getPk())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(newComment)))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void addComment_WhenAdNotExists_ShouldReturnNotFound() throws Exception {
        setAuthentication(userAuth);

        CreateOrUpdateComment newComment = new CreateOrUpdateComment();
        newComment.setText("New comment text");

        mockMvc.perform(post("/ads/{adId}/comments", 999)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(newComment)))
                .andExpect(status().isNotFound());
    }

    @Test
    void addComment_WhenEmptyText_ShouldReturnBadRequest() throws Exception {
        setAuthentication(userAuth);

        CreateOrUpdateComment newComment = new CreateOrUpdateComment();
        newComment.setText(""); // пустой текст

        mockMvc.perform(post("/ads/{adId}/comments", testAd.getPk())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(newComment)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void deleteComment_WhenAuthor_ShouldDeleteComment() throws Exception {
        setAuthentication(userAuth);

        mockMvc.perform(delete("/ads/{adId}/comments/{commentId}", testAd.getPk(), testComment.getPk()))
                .andExpect(status().isOk());

        // Проверяем, что комментарий удалился из базы
        boolean exists = commentRepository.existsById(testComment.getPk());
        assert !exists;
    }

    @Test
    void deleteComment_WhenAdmin_ShouldDeleteComment() throws Exception {
        setAuthentication(adminAuth);

        mockMvc.perform(delete("/ads/{adId}/comments/{commentId}", testAd.getPk(), testComment.getPk()))
                .andExpect(status().isOk());

        boolean exists = commentRepository.existsById(testComment.getPk());
        assert !exists;
    }

    @Test
    void deleteComment_WhenNotAuthor_ShouldReturnForbidden() throws Exception {
        UserEntity otherUser = new UserEntity();
        otherUser.setUsername("other@example.com");
        otherUser.setPassword(passwordEncoder.encode("password123"));
        otherUser.setFirstName("Other");
        otherUser.setLastName("User");
        otherUser.setPhone("+79999999999");
        otherUser.setRole(Role.USER);
        otherUser = userRepository.save(otherUser);

        com.trade.tradeApplication.config.CustomUserDetails otherUserDetails =
                new com.trade.tradeApplication.config.CustomUserDetails(otherUser);
        Authentication otherUserAuth = new org.springframework.security.authentication.UsernamePasswordAuthenticationToken(
                otherUserDetails, null, List.of(new SimpleGrantedAuthority("ROLE_USER"))
        );

        setAuthentication(otherUserAuth);

        mockMvc.perform(delete("/ads/{adId}/comments/{commentId}", testAd.getPk(), testComment.getPk()))
                .andExpect(status().isForbidden());
    }

    @Test
    void deleteComment_WhenNotAuthenticated_ShouldReturnUnauthorized() throws Exception {
        mockMvc.perform(delete("/ads/{adId}/comments/{commentId}", testAd.getPk(), testComment.getPk()))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void deleteComment_WhenCommentNotExists_ShouldReturnNotFound() throws Exception {
        setAuthentication(userAuth);

        mockMvc.perform(delete("/ads/{adId}/comments/{commentId}", testAd.getPk(), 999))
                .andExpect(status().isNotFound());
    }

    @Test
    void deleteComment_WhenAdNotExists_ShouldReturnNotFound() throws Exception {
        setAuthentication(userAuth);

        mockMvc.perform(delete("/ads/{adId}/comments/{commentId}", 999, testComment.getPk()))
                .andExpect(status().isNotFound());
    }

    @Test
    void updateComment_WhenAuthor_ShouldUpdateComment() throws Exception {
        setAuthentication(userAuth);

        CreateOrUpdateComment updateComment = new CreateOrUpdateComment();
        updateComment.setText("Updated comment text");

        mockMvc.perform(patch("/ads/{adId}/comments/{commentId}", testAd.getPk(), testComment.getPk())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updateComment)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.text").value("Updated comment text"))
                .andExpect(jsonPath("$.pk").value(testComment.getPk()))
                .andExpect(jsonPath("$.author").value(testUser.getId()));

        CommentEntity updatedComment = commentRepository.findById(testComment.getPk()).orElseThrow();
        assert updatedComment.getText().equals("Updated comment text");
    }

    @Test
    void updateComment_WhenAdmin_ShouldUpdateComment() throws Exception {
        setAuthentication(adminAuth);

        CreateOrUpdateComment updateComment = new CreateOrUpdateComment();
        updateComment.setText("Updated by admin");

        mockMvc.perform(patch("/ads/{adId}/comments/{commentId}", testAd.getPk(), testComment.getPk())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updateComment)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.text").value("Updated by admin"));

        CommentEntity updatedComment = commentRepository.findById(testComment.getPk()).orElseThrow();
        assert updatedComment.getText().equals("Updated by admin");
    }

    @Test
    void updateComment_WhenNotAuthor_ShouldReturnForbidden() throws Exception {
        UserEntity otherUser = new UserEntity();
        otherUser.setUsername("other@example.com");
        otherUser.setPassword(passwordEncoder.encode("password123"));
        otherUser.setFirstName("Other");
        otherUser.setLastName("User");
        otherUser.setPhone("+79999999999");
        otherUser.setRole(Role.USER);
        otherUser = userRepository.save(otherUser);

        com.trade.tradeApplication.config.CustomUserDetails otherUserDetails =
                new com.trade.tradeApplication.config.CustomUserDetails(otherUser);
        Authentication otherUserAuth = new org.springframework.security.authentication.UsernamePasswordAuthenticationToken(
                otherUserDetails, null, List.of(new SimpleGrantedAuthority("ROLE_USER"))
        );

        setAuthentication(otherUserAuth);

        CreateOrUpdateComment updateComment = new CreateOrUpdateComment();
        updateComment.setText("Updated text");

        mockMvc.perform(patch("/ads/{adId}/comments/{commentId}", testAd.getPk(), testComment.getPk())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updateComment)))
                .andExpect(status().isForbidden());
    }

    @Test
    void updateComment_WhenNotAuthenticated_ShouldReturnUnauthorized() throws Exception {
        CreateOrUpdateComment updateComment = new CreateOrUpdateComment();
        updateComment.setText("Updated text");

        mockMvc.perform(patch("/ads/{adId}/comments/{commentId}", testAd.getPk(), testComment.getPk())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updateComment)))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void updateComment_WhenCommentNotExists_ShouldReturnNotFound() throws Exception {
        setAuthentication(userAuth);

        CreateOrUpdateComment updateComment = new CreateOrUpdateComment();
        updateComment.setText("Updated text");

        mockMvc.perform(patch("/ads/{adId}/comments/{commentId}", testAd.getPk(), 999)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updateComment)))
                .andExpect(status().isNotFound());
    }

    @Test
    void updateComment_WhenAdNotExists_ShouldReturnNotFound() throws Exception {
        setAuthentication(userAuth);

        CreateOrUpdateComment updateComment = new CreateOrUpdateComment();
        updateComment.setText("Updated text");

        mockMvc.perform(patch("/ads/{adId}/comments/{commentId}", 999, testComment.getPk())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updateComment)))
                .andExpect(status().isNotFound());
    }

    @Test
    void updateComment_WhenEmptyText_ShouldReturnBadRequest() throws Exception {
        setAuthentication(userAuth);

        CreateOrUpdateComment updateComment = new CreateOrUpdateComment();
        updateComment.setText("");

        mockMvc.perform(patch("/ads/{adId}/comments/{commentId}", testAd.getPk(), testComment.getPk())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updateComment)))
                .andExpect(status().isBadRequest());
    }
}
