package com.integrationTests;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.trade.tradeApplication.TradeApplication;
import com.trade.tradeApplication.config.CustomUserDetails;
import com.trade.tradeApplication.entity.AdEntity;
import com.trade.tradeApplication.entity.UserEntity;
import com.trade.tradeApplication.model.Role;
import com.trade.tradeApplication.repository.AdRepository;
import com.trade.tradeApplication.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest(classes = TradeApplication.class)
@AutoConfigureMockMvc
@Transactional
@ActiveProfiles("test")
class AdsControllerIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private AdRepository adRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    private UserEntity testUser;
    private UserEntity adminUser;
    private Authentication userAuth;
    private Authentication adminAuth;

    private String uniqueUsername;
    private String uniqueAdminUsername;

    @BeforeEach
    void setUp() {
        // Генерируем уникальные username для каждого теста
        uniqueUsername = "testuser_" + UUID.randomUUID().toString().substring(0, 8);
        uniqueAdminUsername = "adminuser_" + UUID.randomUUID().toString().substring(0, 8);

        // Создаем тестового пользователя
        testUser = new UserEntity();
        testUser.setUsername(uniqueUsername);
        testUser.setPassword(passwordEncoder.encode("password123"));
        testUser.setFirstName("John");
        testUser.setLastName("Doe");
        testUser.setPhone("+79123456789");
        testUser.setRole(Role.USER);
        testUser = userRepository.save(testUser);

        // Создаем админа
        adminUser = new UserEntity();
        adminUser.setUsername(uniqueAdminUsername);
        adminUser.setPassword(passwordEncoder.encode("adminpass123"));
        adminUser.setFirstName("Admin");
        adminUser.setLastName("User");
        adminUser.setPhone("+79876543210");
        adminUser.setRole(Role.ADMIN);
        adminUser = userRepository.save(adminUser);

        // Создаем аутентификацию для пользователя
        CustomUserDetails userDetails = new CustomUserDetails(testUser);
        userAuth = new UsernamePasswordAuthenticationToken(
                userDetails, null, List.of(new SimpleGrantedAuthority("ROLE_USER"))
        );

        // Создаем аутентификацию для админа
        CustomUserDetails adminDetails = new CustomUserDetails(adminUser);
        adminAuth = new UsernamePasswordAuthenticationToken(
                adminDetails, null, List.of(new SimpleGrantedAuthority("ROLE_ADMIN"))
        );
    }

    private void setAuthentication(Authentication authentication) {
        SecurityContextHolder.getContext().setAuthentication(authentication);
    }

    @Test
    @WithMockUser
    void getAllAds_WhenNoAds_ShouldReturnEmptyList() throws Exception {
        mockMvc.perform(get("/ads"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.count").value(0))
                .andExpect(jsonPath("$.results").isArray())
                .andExpect(jsonPath("$.results").isEmpty());
    }

    @Test
    @WithMockUser
    void getAllAds_WhenAdsExist_ShouldReturnAdsList() throws Exception {

        AdEntity ad1 = new AdEntity();
        ad1.setAuthor(testUser);
        ad1.setTitle("Test Ad 1");
        ad1.setPrice(1000);
        ad1.setDescription("Test Description 1");
        ad1.setImage("image1.jpg");
        adRepository.save(ad1);

        AdEntity ad2 = new AdEntity();
        ad2.setAuthor(adminUser);
        ad2.setTitle("Test Ad 2");
        ad2.setPrice(2000);
        ad2.setDescription("Test Description 2");
        ad2.setImage("image2.jpg");
        adRepository.save(ad2);

        mockMvc.perform(get("/ads"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.count").value(2))
                .andExpect(jsonPath("$.results").isArray())
                .andExpect(jsonPath("$.results.length()").value(2))
                .andExpect(jsonPath("$.results[0].title").value("Test Ad 1"))
                .andExpect(jsonPath("$.results[1].title").value("Test Ad 2"));
    }

    @Test
    @WithMockUser
    void getAd_WhenAdExists_ShouldReturnAd() throws Exception {
        AdEntity ad = new AdEntity();
        ad.setAuthor(testUser);
        ad.setTitle("Test Ad");
        ad.setPrice(1000);
        ad.setDescription("Test Description");
        ad.setImage("test.jpg");
        ad = adRepository.save(ad);

        mockMvc.perform(get("/ads/{id}", ad.getPk()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.pk").value(ad.getPk()))
                .andExpect(jsonPath("$.title").value("Test Ad"))
                .andExpect(jsonPath("$.price").value(1000));
    }

    @Test
    @WithMockUser
    void getAd_WhenAdNotExists_ShouldReturnNotFound() throws Exception {
        mockMvc.perform(get("/ads/{id}", 999))
                .andExpect(status().isNotFound());
    }

    @Test
    void createAd_WhenAuthenticatedWithValidData_ShouldCreateAd() throws Exception {
        setAuthentication(userAuth);

        String propertiesJson = "{" +
                "\"title\": \"New Test Ad\"," +
                "\"price\": 1500," +
                "\"description\": \"New test description\"" +
                "}";


        MockMultipartFile propertiesFile = new MockMultipartFile(
                "properties",
                "properties.json",
                MediaType.APPLICATION_JSON_VALUE,
                propertiesJson.getBytes()
        );


        MockMultipartFile imageFile = new MockMultipartFile(
                "image",
                "test-image.jpg",
                MediaType.IMAGE_JPEG_VALUE,
                "test image content".getBytes()
        );

        mockMvc.perform(multipart("/ads")
                        .file(propertiesFile)
                        .file(imageFile)
                        .contentType(MediaType.MULTIPART_FORM_DATA))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.title").value("New Test Ad"))
                .andExpect(jsonPath("$.price").value(1500))
                .andExpect(jsonPath("$.description").value("New test description"))
                .andExpect(jsonPath("$.image").exists());
    }

    @Test
    void createAd_WhenInvalidData_ShouldReturnBadRequest() throws Exception {
        setAuthentication(userAuth);

        String invalidPropertiesJson = "{" +
                "\"title\": \"A\"," +
                "\"price\": -100," +
                "\"description\": \"Short\"" +
                "}";


        MockMultipartFile propertiesFile = new MockMultipartFile(
                "properties",
                "properties.json",
                MediaType.APPLICATION_JSON_VALUE,
                invalidPropertiesJson.getBytes()
        );

        mockMvc.perform(multipart("/ads")
                        .file(propertiesFile) // Используем созданный файл
                        .param("image", "image.jpg")
                        .contentType(MediaType.MULTIPART_FORM_DATA))
                .andExpect(status().isBadRequest());
    }
    @Test
    void updateAd_WhenOwner_ShouldUpdateAd() throws Exception {
        // Создаем объявление от testUser
        AdEntity ad = new AdEntity();
        ad.setAuthor(testUser);
        ad.setTitle("Original Title");
        ad.setPrice(1000);
        ad.setDescription("Original Description");
        ad.setImage("original.jpg");
        ad = adRepository.save(ad);

        setAuthentication(userAuth);

        String updateJson = "{" +
                "\"title\": \"Updated Title\"," +
                "\"price\": 2000," +
                "\"description\": \"Updated description\"" +
                "}";

        mockMvc.perform(patch("/ads/{id}", ad.getPk())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(updateJson))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.title").value("Updated Title"))
                .andExpect(jsonPath("$.price").value(2000));
    }

    @Test
    void updateAd_WhenAdmin_ShouldUpdateAnyAd() throws Exception {
        // Создаем объявление от testUser
        AdEntity ad = new AdEntity();
        ad.setAuthor(testUser);
        ad.setTitle("Original Title");
        ad.setPrice(1000);
        ad.setDescription("Original Description");
        ad.setImage("original.jpg");
        ad = adRepository.save(ad);

        setAuthentication(adminAuth);

        String updateJson = "{" +
                "\"title\": \"Admin Updated Title\"," +
                "\"price\": 3000," +
                "\"description\": \"Admin updated description\"" +
                "}";

        mockMvc.perform(patch("/ads/{id}", ad.getPk())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(updateJson))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.title").value("Admin Updated Title"));
    }

    @Test
    void updateAd_WhenNotOwner_ShouldReturnForbidden() throws Exception {
        // Создаем объявление от adminUser
        AdEntity ad = new AdEntity();
        ad.setAuthor(adminUser);
        ad.setTitle("Admin Ad");
        ad.setPrice(1000);
        ad.setDescription("Admin Description");
        ad.setImage("admin.jpg");
        ad = adRepository.save(ad);

        setAuthentication(userAuth); // Пытаемся обновить чужое объявление

        String updateJson = "{" +
                "\"title\": \"Hacked Title\"," +
                "\"price\": 1," +
                "\"description\": \"Hacked description\"" +
                "}";

        mockMvc.perform(patch("/ads/{id}", ad.getPk())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(updateJson))
                .andExpect(status().isForbidden());
    }

    @Test
    void deleteAd_WhenOwner_ShouldDeleteAd() throws Exception {
        AdEntity ad = new AdEntity();
        ad.setAuthor(testUser);
        ad.setTitle("Ad to delete");
        ad.setPrice(1000);
        ad.setDescription("Description");
        ad.setImage("image.jpg");
        ad = adRepository.save(ad);

        setAuthentication(userAuth);

        mockMvc.perform(delete("/ads/{id}", ad.getPk()))
                .andExpect(status().isOk());

        // Проверяем через репозиторий, что объявление удалено
        Optional<AdEntity> deletedAd = adRepository.findById(ad.getPk());
        assertThat(deletedAd).isEmpty();
    }

    @Test
    void deleteAd_WhenAdmin_ShouldDeleteAnyAd() throws Exception {
        AdEntity ad = new AdEntity();
        ad.setAuthor(testUser);
        ad.setTitle("Ad to delete by admin");
        ad.setPrice(1000);
        ad.setDescription("Description");
        ad.setImage("image.jpg");
        ad = adRepository.save(ad);

        setAuthentication(adminAuth);

        mockMvc.perform(delete("/ads/{id}", ad.getPk()))
                .andExpect(status().isOk());
    }

    @Test
    void deleteAd_WhenNotOwner_ShouldReturnForbidden() throws Exception {
        AdEntity ad = new AdEntity();
        ad.setAuthor(adminUser);
        ad.setTitle("Admin Ad");
        ad.setPrice(1000);
        ad.setDescription("Description");
        ad.setImage("image.jpg");
        ad = adRepository.save(ad);

        setAuthentication(userAuth);

        mockMvc.perform(delete("/ads/{id}", ad.getPk()))
                .andExpect(status().isForbidden());
    }

    @Test
    void getMyAds_WhenAuthenticated_ShouldReturnUserAds() throws Exception {
        AdEntity ad1 = new AdEntity();
        ad1.setAuthor(testUser);
        ad1.setTitle("My Ad 1");
        ad1.setPrice(1000);
        ad1.setDescription("My Description 1");
        ad1.setImage("my1.jpg");
        adRepository.save(ad1);

        AdEntity ad2 = new AdEntity();
        ad2.setAuthor(testUser);
        ad2.setTitle("My Ad 2");
        ad2.setPrice(2000);
        ad2.setDescription("My Description 2");
        ad2.setImage("my2.jpg");
        adRepository.save(ad2);


        AdEntity otherAd = new AdEntity();
        otherAd.setAuthor(adminUser);
        otherAd.setTitle("Other Ad");
        otherAd.setPrice(3000);
        otherAd.setDescription("Other Description");
        otherAd.setImage("other.jpg");
        adRepository.save(otherAd);

        setAuthentication(userAuth);

        mockMvc.perform(get("/ads/me"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.count").value(2))
                .andExpect(jsonPath("$.results.length()").value(2))
                .andExpect(jsonPath("$.results[0].title").value("My Ad 1"))
                .andExpect(jsonPath("$.results[1].title").value("My Ad 2"));
    }

    @Test
    void getMyAds_WhenNotAuthenticated_ShouldReturnUnauthorized() throws Exception {
        mockMvc.perform(get("/ads/me"))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void updateAdImage_WhenOwner_ShouldUpdateImage() throws Exception {
        AdEntity ad = new AdEntity();
        ad.setAuthor(testUser);
        ad.setTitle("Test Ad");
        ad.setPrice(1000);
        ad.setDescription("Test Description");
        ad.setImage("old_image.jpg");
        ad = adRepository.save(ad);

        setAuthentication(userAuth);

        mockMvc.perform(multipart("/ads/{id}/image", ad.getPk())
                        .param("image", "new_image_updated.jpg")
                        .with(request -> {
                            request.setMethod("PATCH");
                            return request;
                        })
                        .contentType(MediaType.MULTIPART_FORM_DATA))
                .andExpect(status().isOk());

        AdEntity updatedAd = adRepository.findById(ad.getPk()).orElseThrow();
        assertEquals("new_image_updated.jpg", updatedAd.getImage());
    }

    @Test
    void updateAdImage_WhenNotOwner_ShouldReturnForbidden() throws Exception {
        AdEntity ad = new AdEntity();
        ad.setAuthor(adminUser);
        ad.setTitle("Admin Ad");
        ad.setPrice(1000);
        ad.setDescription("Admin Description");
        ad.setImage("admin_image.jpg");
        ad = adRepository.save(ad);

        setAuthentication(userAuth);

        mockMvc.perform(multipart("/ads/{id}/image", ad.getPk())
                        .param("image", "hacked_image.jpg")
                        .with(request -> {
                            request.setMethod("PATCH");
                            return request;
                        })
                        .contentType(MediaType.MULTIPART_FORM_DATA))
                .andExpect(status().isForbidden());
    }
}
