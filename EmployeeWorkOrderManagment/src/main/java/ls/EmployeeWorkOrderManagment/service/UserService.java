package ls.EmployeeWorkOrderManagment.service;

import com.cloudinary.Cloudinary;
import com.cloudinary.utils.ObjectUtils;
import jakarta.transaction.Transactional;
import ls.EmployeeWorkOrderManagment.persistence.dao.RoleRepository;
import ls.EmployeeWorkOrderManagment.persistence.dao.UserRepository;
import ls.EmployeeWorkOrderManagment.persistence.model.role.Role;
import ls.EmployeeWorkOrderManagment.persistence.model.token.ResetToken;
import ls.EmployeeWorkOrderManagment.persistence.model.token.VerificationToken;
import ls.EmployeeWorkOrderManagment.persistence.model.user.User;
import ls.EmployeeWorkOrderManagment.web.dto.user.UserDtoMapper;
import ls.EmployeeWorkOrderManagment.web.dto.user.UserPasswordChangeDto;
import ls.EmployeeWorkOrderManagment.web.dto.user.UserRegistrationDto;
import ls.EmployeeWorkOrderManagment.web.dto.user.UserSiteRenderDto;
import ls.EmployeeWorkOrderManagment.web.error.InvalidTokenException;
import ls.EmployeeWorkOrderManagment.web.error.TokenExpiredException;
import ls.EmployeeWorkOrderManagment.web.error.UserAlreadyExistsException;
import ls.EmployeeWorkOrderManagment.web.error.UserNotFoundException;
import org.springframework.core.env.Environment;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class UserService {
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final RoleRepository roleRepository;

    private final Environment environment;
    public UserService(UserRepository userRepository,
                       PasswordEncoder passwordEncoder,
                       RoleRepository roleRepository, Environment environment) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.roleRepository = roleRepository;
        this.environment = environment;
    }

    @Transactional
    public User saveNewUserAccount(UserRegistrationDto userRegistrationData) throws UserAlreadyExistsException, NoSuchElementException {
        if(isEmailUnique(userRegistrationData.getEmail())) {
            throw new UserAlreadyExistsException("There is already an account with that email address: " + userRegistrationData.getEmail());
        }
        User user = new User();
        user.setEmail(userRegistrationData.getEmail());
        String hashedPassword = passwordEncoder.encode(userRegistrationData.getPassword());
        user.setPassword(hashedPassword);
        user.setFirstName(userRegistrationData.getFirstName());
        user.setLastName(userRegistrationData.getLastName());
        Optional<Role> role = roleRepository.findByRoleName("USER");
        role.ifPresentOrElse(
                user::addRoles,
                () -> {
                    throw new NoSuchElementException("Failed to create new account");
                }
        );
        return userRepository.save(user);
    }

    private boolean isEmailUnique(String email) {
        return userRepository.findByEmail(email).isPresent();
    }

    @Transactional
    public void enableUserAccount(VerificationToken token) throws InvalidTokenException, TokenExpiredException {
        User registeredUser = token.getUser();
        registeredUser.setEnabled(true);
        userRepository.save(registeredUser);
    }

    public User retrieveUserByEmail(String email) throws UsernameNotFoundException {
        return userRepository.findByEmail(email).orElseThrow(() -> new UsernameNotFoundException("User with this email not found"));
    }

    public UserSiteRenderDto getUserInfoByEmail(String email) throws UserNotFoundException {
        User fetchedUser = userRepository.findByEmail(email).orElseThrow(() -> new UserNotFoundException("User account with these credentials doesn't exist."));
        return UserDtoMapper.mapSiteRenderToDto(fetchedUser);
    }

    @Transactional
    public void changeUserResetPassword(UserPasswordChangeDto userRegistrationDto, ResetToken token) {
        User user = token.getUser();
        String newPassword = passwordEncoder.encode(userRegistrationDto.getPassword());
        user.setPassword(newPassword);
        userRepository.save(user);
    }

    public List<UserSiteRenderDto> getAllUsersInfo() {
        List<User> allUsers = userRepository.findAll();
        return allUsers.stream()
                .map(UserDtoMapper::mapSiteRenderToDto)
                .collect(Collectors.toList());
    }

    @Transactional
    public void deleteUserAccount(String id) {
        UUID uuid = UUID.fromString(id);
        userRepository.deleteById(uuid);
    }

    @Transactional
    public void editUserAccount(Set<UUID> rolesId, boolean enabled, UUID userId) throws UserNotFoundException{
        User user = userRepository.findById(userId).orElseThrow(() -> new UserNotFoundException("User account with this id was not found."));

        user.cleanRoles();
        Set<Role> roles = loadUserRoles(rolesId);
        roles.forEach(user::addRoles);

        user.setEnabled(enabled);
        userRepository.save(user);
    }

    private Set<Role> loadUserRoles(Set<UUID> rolesId) {
        return  rolesId.stream()
                .map(roleRepository::findById)
                .filter(Optional::isPresent)
                .map(Optional::get)
                .collect(Collectors.toSet());
    }

    @Transactional
    public void changeUserFirstName(String firstName, String userEmail) {
        User user = retrieveUserByEmail(userEmail);
        user.setFirstName(firstName);
        userRepository.save(user);
    }

    @Transactional
    public void changeUserLastName(String lastName, String userEmail) {
        User user = retrieveUserByEmail(userEmail);
        user.setLastName(lastName);
        userRepository.save(user);
    }

    @Transactional
    public void changeUserPassword(UserPasswordChangeDto userPasswordChange, String userEmail) {
        User user = retrieveUserByEmail(userEmail);
        String newPassword = passwordEncoder.encode(userPasswordChange.getPassword());
        user.setPassword(newPassword);
        userRepository.save(user);
    }

    @SuppressWarnings("rawtypes")
    @Transactional
    public void changeProfilePicture(MultipartFile profilePicture, UUID userId) throws IOException, UserNotFoundException {
        Map params = ObjectUtils.asMap(
                "use_filename", false,
                "unique_filename", false,
                "overwrite", true,
                "public_id", userId.toString(),
                "asset_folder", "EWOM"
        );

        byte[] picture = profilePicture.getInputStream().readAllBytes();
        Cloudinary cloudinaryBean = cloudinary();
        Map uploadResult = cloudinaryBean.uploader().upload(picture, params);
        User user = userRepository.findById(userId).orElseThrow(() -> new UserNotFoundException("User with this id doesn't exist."));
        user.setPicUrl((String) uploadResult.get("url"));
        userRepository.save(user);
    }

    private Cloudinary cloudinary() {
        Cloudinary cloudinary = new Cloudinary(environment.getProperty("cloudinary_url"));
        cloudinary.config.secure = true;
        return cloudinary;
    }
}
