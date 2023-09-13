package ma.sg.df.creditbureau.service;

import ma.sg.df.creditbureau.domain.CbUser;
import ma.sg.df.creditbureau.dto.UserDto;
import ma.sg.df.creditbureau.exception.CreditBureauException;
import ma.sg.df.creditbureau.exception.EntityNotFoundException;
import ma.sg.df.creditbureau.mapper.UserMapper;
import ma.sg.df.creditbureau.repository.*;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.junit.runner.RunWith;
import org.mapstruct.factory.Mappers;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.MockitoJUnitRunner;

import java.util.Arrays;
import java.util.Optional;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doReturn;

@RunWith(MockitoJUnitRunner.class)
public class UserServiceTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private RoleRepository roleRepository;

    @Spy
    private UserMapper userMapper = Mappers.getMapper(UserMapper.class);

    @Mock
    private EntiteRepository entiteRepository;
    @Mock
    private DirectionRegionaleRepository directionRegionaleRepository;
    @Mock
    private UniteCommercialeRepository uniteCommercialeRepository;
    @Mock
    private AgenceRepository agenceRepository;

    @InjectMocks
    private UserService userService;

    @Rule
    public ExpectedException exceptionRule = ExpectedException.none();


    @Test
    public void shouldThrowCBExceptionWhenCreatingUserGivenUsernameAlreadyExists() {

        //Given
        UserDto userDto = UserDto.builder().email("username@gmail.com").build();
        doReturn(Optional.of(new CbUser())).when(userRepository).findByEmail("username@gmail.com");

        //expect
        exceptionRule.expect(CreditBureauException.class);
        exceptionRule.expectMessage(UserService.UTILISATEUR_EXISTE_DEJA);

        //When
        userService.createUser(userDto);

    }

    @Test
    public void shouldCreateUser() {

        //Given
        UserDto userDto = UserDto.builder().email("username@gmail.com").rolesIds(Arrays.asList(1L)).build();
        CbUser user = CbUser.builder().uuid("uuid").id(1L).email("username@gmail.com").build();

        doReturn(Optional.empty()).when(userRepository).findByEmail("username@gmail.com");
        doReturn(user).when(userRepository).save(any());
        doReturn(Optional.of(user)).when(userRepository).findById(any());

        //When
        UserDto result = userService.createUser(userDto);

        //Then
        assertThat(result, is(notNullValue()));
        assertEquals("uuid", result.getUuid());
        assertEquals("username@gmail.com", result.getEmail());
    }


    @Test
    public void shouldThrowEntityNotFoundExceptionWhenUpdatingUserGivenUserDosntExist() {

        //Given
        UserDto userDto = UserDto.builder().uuid("uuid").email("username@gmail.com").build();
        doReturn(Optional.empty()).when(userRepository).findByEmail("username@gmail.com");

        //expect
        exceptionRule.expect(EntityNotFoundException.class);
        exceptionRule.expectMessage(UserService.UTILISATEUR_NON_TROUVE);

        //When
        userService.updateUser(userDto);

    }

    @Test
    public void shouldThrowCBExceptionWhenUpdatingUserGivenUserAlreadyExists() {

        //Given
        UserDto userDto = UserDto.builder().uuid("uuid").email("username@gmail.com").build();
        doReturn(Optional.of(CbUser.builder().uuid("uuid2").build())).when(userRepository).findByEmail("username@gmail.com");

        //expect
        exceptionRule.expect(CreditBureauException.class);
        exceptionRule.expectMessage(UserService.UTILISATEUR_EXISTE_DEJA);

        //When
        userService.updateUser(userDto);

    }

    @Test
    public void shouldUpdateUser() {

        //Given
        UserDto userDto = UserDto.builder().email("username@gmail.com").uuid("uuid").nom("name").rolesIds(Arrays.asList(1L)).build();
        CbUser user = CbUser.builder().uuid("uuid").id(1L).email("username@gmail.com").build();
        CbUser updatedUser = CbUser.builder().uuid("uuid").nom("name").build();

        doReturn(Optional.of(user)).when(userRepository).findByEmail("username@gmail.com");
        doReturn(updatedUser).when(userRepository).save(any());
        doReturn(Optional.of(updatedUser)).when(userRepository).findById(any());

        //When
        UserDto result = userService.updateUser(userDto);

        //Then
        assertThat(result, is(notNullValue()));
        assertEquals("name", result.getNom());
    }

}
