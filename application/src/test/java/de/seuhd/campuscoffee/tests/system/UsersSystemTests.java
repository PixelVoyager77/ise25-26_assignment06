package de.seuhd.campuscoffee.tests.system;

import de.seuhd.campuscoffee.api.dtos.UserDto;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;

import java.util.List;

import static de.seuhd.campuscoffee.tests.SystemTestUtils.Requests.userRequests;
import static org.assertj.core.api.Assertions.assertThat;

public class UsersSystemTests extends AbstractSysTest {

    private UserDto user(String name) {
        return UserDto.builder()
                .loginName(name)
                .emailAddress(name + "@example.test")
                .firstName("First")
                .lastName("Last")
                .build();
    }

    @Test
    void createUser() {
        UserDto created = userRequests.create(List.of(user("jane"))).getFirst();
        assertThat(created.id()).isNotNull();
        assertThat(created.loginName()).isEqualTo("jane");
    }

    @Test
    void getAllUsers() {
        userRequests.create(List.of(user("a"), user("b")));
        List<UserDto> all = userRequests.retrieveAll();
        assertThat(all).extracting(UserDto::loginName).contains("a", "b");
    }

    @Test
    void getUserById() {
        UserDto created = userRequests.create(List.of(user("lookup"))).getFirst();
        UserDto fetched = userRequests.retrieveById(created.id());
        assertThat(fetched.loginName()).isEqualTo("lookup");
    }

    @Test
    void filterByLoginName() {
        userRequests.create(List.of(user("filterme")));
        UserDto filtered = userRequests.retrieveByFilter("login_name", "filterme");
        assertThat(filtered.loginName()).isEqualTo("filterme");
    }

    @Test
    void deleteUser() {
        UserDto created = userRequests.create(List.of(user("del"))).getFirst();

        List<Integer> result = userRequests.deleteAndReturnStatusCodes(
                List.of(created.id(), created.id())
        );

        assertThat(result).containsExactly(
                HttpStatus.NO_CONTENT.value(),
                HttpStatus.NOT_FOUND.value()
        );

        List<Long> remaining = userRequests.retrieveAll()
                .stream().map(UserDto::id).toList();

        assertThat(remaining).doesNotContain(created.id());
    }
}