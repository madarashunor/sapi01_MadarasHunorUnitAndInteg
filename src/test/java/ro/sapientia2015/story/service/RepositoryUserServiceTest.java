package ro.sapientia2015.story.service;

import org.junit.Before;
import org.junit.Test;
import org.mockito.ArgumentCaptor;
import org.springframework.test.util.ReflectionTestUtils;

import ro.sapientia2015.story.StoryTestUtil;
import ro.sapientia2015.story.dto.StoryDTO;
import ro.sapientia2015.story.dto.UserDTO;
import ro.sapientia2015.story.exception.NotFoundException;
import ro.sapientia2015.story.model.Story;
import ro.sapientia2015.story.model.User;
import ro.sapientia2015.story.repository.StoryRepository;
import ro.sapientia2015.story.repository.UserRepository;
import ro.sapientia2015.story.service.RepositoryStoryService;

import java.util.ArrayList;
import java.util.List;

import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.assertNull;
import static org.mockito.Mockito.*;

public class RepositoryUserServiceTest {

	private RepositoryUserService service;

	private UserRepository repositoryMock;

	@Before
	public void setUp() {
		service = new RepositoryUserService();

		repositoryMock = mock(UserRepository.class);
		ReflectionTestUtils.setField(service, "repository", repositoryMock);
	}

	@Test
	public void add() {
		UserDTO dto = new UserDTO();

		dto.setId(null);
		dto.setDescription("DESC");
		dto.setTitle("TITLE");

		service.add(dto);

		ArgumentCaptor<User> userArgument = ArgumentCaptor.forClass(User.class);
		verify(repositoryMock, times(1)).save(userArgument.capture());
		verifyNoMoreInteractions(repositoryMock);

		User model = userArgument.getValue();

		assertNull(model.getId());
		assertEquals(dto.getDescription(), model.getDescription());
		assertEquals(dto.getTitle(), model.getTitle());
	}

	@Test
	public void findAll() {
		List<User> models = new ArrayList<User>();
		when(repositoryMock.findAll()).thenReturn(models);

		List<User> actual = service.findAll();

		verify(repositoryMock, times(1)).findAll();
		verifyNoMoreInteractions(repositoryMock);

		assertEquals(models, actual);
	}

	@Test
	public void findById() throws NotFoundException {
		User model = User.getBuilder("title").description("desc").build();

		ReflectionTestUtils.setField(model, "id", (long) 123456);
		when(repositoryMock.findOne((long) 123456)).thenReturn(model);

		User actual = service.findById((long) 123456);

		verify(repositoryMock, times(1)).findOne((long) 123456);
		verifyNoMoreInteractions(repositoryMock);

		assertEquals(model, actual);
	}

	@Test(expected = NotFoundException.class)
	public void findByIdWhenIsNotFound() throws NotFoundException {
		when(repositoryMock.findOne((long) 123456)).thenReturn(null);

		service.findById((long) 123456);

		verify(repositoryMock, times(1)).findOne((long) 123456);
		verifyNoMoreInteractions(repositoryMock);
	}

}
