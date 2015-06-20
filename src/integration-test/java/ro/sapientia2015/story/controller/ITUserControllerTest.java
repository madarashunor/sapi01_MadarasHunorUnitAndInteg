package ro.sapientia2015.story.controller;

import com.github.springtestdbunit.DbUnitTestExecutionListener;
import com.github.springtestdbunit.annotation.DatabaseSetup;
import com.github.springtestdbunit.annotation.ExpectedDatabase;
import com.github.springtestdbunit.assertion.DatabaseAssertionMode;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.http.MediaType;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.TestExecutionListeners;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.support.DependencyInjectionTestExecutionListener;
import org.springframework.test.context.support.DirtiesContextTestExecutionListener;
import org.springframework.test.context.transaction.TransactionalTestExecutionListener;
import org.springframework.test.web.server.MockMvc;
import org.springframework.test.web.server.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import ro.sapientia2015.common.controller.ErrorController;
import ro.sapientia2015.config.ExampleApplicationContext;
import ro.sapientia2015.context.WebContextLoader;
import ro.sapientia2015.story.StoryTestUtil;
import ro.sapientia2015.story.controller.StoryController;
import ro.sapientia2015.story.dto.StoryDTO;
import ro.sapientia2015.story.dto.UserDTO;
import ro.sapientia2015.story.model.Story;
import ro.sapientia2015.story.model.User;

import javax.annotation.Resource;

import static org.hamcrest.Matchers.allOf;
import static org.hamcrest.Matchers.hasItem;
import static org.hamcrest.Matchers.hasProperty;
import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.isEmptyOrNullString;
import static org.hamcrest.Matchers.nullValue;
import static org.springframework.test.web.server.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.server.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.server.result.MockMvcResultMatchers.flash;
import static org.springframework.test.web.server.result.MockMvcResultMatchers.forwardedUrl;
import static org.springframework.test.web.server.result.MockMvcResultMatchers.model;
import static org.springframework.test.web.server.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.server.result.MockMvcResultMatchers.view;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(loader = WebContextLoader.class, classes = { ExampleApplicationContext.class })
// @ContextConfiguration(loader = WebContextLoader.class, locations =
// {"classpath:exampleApplicationContext.xml"})
@TestExecutionListeners({ DependencyInjectionTestExecutionListener.class,
		DirtiesContextTestExecutionListener.class,
		TransactionalTestExecutionListener.class,
		DbUnitTestExecutionListener.class })
@DatabaseSetup("userData.xml")
public class ITUserControllerTest {

	private static final String FORM_FIELD_DESCRIPTION = "description";
	private static final String FORM_FIELD_ID = "id";
	private static final String FORM_FIELD_TITLE = "title";

	@Resource
	private WebApplicationContext webApplicationContext;

	private MockMvc mockMvc;

	@Before
	public void setUp() {
		mockMvc = MockMvcBuilders.webApplicationContextSetup(
				webApplicationContext).build();
	}

	@Test
	@ExpectedDatabase(value = "userData.xml", assertionMode = DatabaseAssertionMode.NON_STRICT)
	public void showAddForm() throws Exception {
		mockMvc.perform(get("/user/add"))
				.andExpect(status().isOk())
				.andExpect(view().name(UserController.VIEW_ADD))
				.andExpect(forwardedUrl("/WEB-INF/jsp/user/add.jsp"))
				.andExpect(
						model().attribute(UserController.MODEL_ATTRIBUTE,
								hasProperty("id", nullValue())))
				.andExpect(
						model().attribute(
								UserController.MODEL_ATTRIBUTE,
								hasProperty("description",
										isEmptyOrNullString())))
				.andExpect(
						model().attribute(UserController.MODEL_ATTRIBUTE,
								hasProperty("title", isEmptyOrNullString())));
	}

	@Test
	@ExpectedDatabase(value = "userData.xml", assertionMode = DatabaseAssertionMode.NON_STRICT)
	public void addEmpty() throws Exception {
		mockMvc.perform(
				post("/user/add").contentType(
						MediaType.APPLICATION_FORM_URLENCODED).sessionAttr(
						StoryController.MODEL_ATTRIBUTE, new StoryDTO()))
				.andExpect(status().isOk())
				.andExpect(view().name(UserController.VIEW_ADD))
				.andExpect(forwardedUrl("/WEB-INF/jsp/user/add.jsp"))
				.andExpect(
						model().attributeHasFieldErrors(
								UserController.MODEL_ATTRIBUTE, "title"))
				.andExpect(
						model().attribute(UserController.MODEL_ATTRIBUTE,
								hasProperty("id", nullValue())))
				.andExpect(
						model().attribute(
								UserController.MODEL_ATTRIBUTE,
								hasProperty("description",
										isEmptyOrNullString())))
				.andExpect(
						model().attribute(UserController.MODEL_ATTRIBUTE,
								hasProperty("title", isEmptyOrNullString())));
	}

	@Test
	@ExpectedDatabase(value = "userData-expected1.xml", assertionMode = DatabaseAssertionMode.NON_STRICT)
	public void addWhenTitleAndDescriptionAreTooLong() throws Exception {
		String title = StoryTestUtil
				.createStringWithLength(User.MAX_LENGTH_TITLE + 1);
		String description = StoryTestUtil
				.createStringWithLength(User.MAX_LENGTH_DESCRIPTION + 1);

		mockMvc.perform(
				post("/user/add")
						.contentType(MediaType.APPLICATION_FORM_URLENCODED)
						.param(FORM_FIELD_DESCRIPTION, description)
						.param(FORM_FIELD_TITLE, title)
						.sessionAttr(UserController.MODEL_ATTRIBUTE,
								new UserDTO()))
				.andExpect(status().isOk())
				.andExpect(view().name(UserController.VIEW_ADD))
				.andExpect(forwardedUrl("/WEB-INF/jsp/user/add.jsp"))
				.andExpect(
						model().attributeHasFieldErrors(
								UserController.MODEL_ATTRIBUTE, "title"))
				.andExpect(
						model().attributeHasFieldErrors(
								UserController.MODEL_ATTRIBUTE, "description"))
				.andExpect(
						model().attribute(UserController.MODEL_ATTRIBUTE,
								hasProperty("id", nullValue())))
				.andExpect(
						model().attribute(UserController.MODEL_ATTRIBUTE,
								hasProperty("description", is(description))))
				.andExpect(
						model().attribute(UserController.MODEL_ATTRIBUTE,
								hasProperty("title", is(title))));
	}

	@Test
	@ExpectedDatabase(value = "userData-add-expected.xml", assertionMode = DatabaseAssertionMode.NON_STRICT)
	public void add() throws Exception {
		String expectedRedirectViewPath = StoryTestUtil
				.createRedirectViewPath("/" + UserController.VIEW_LIST);

		mockMvc.perform(
				post("/user/add")
						.contentType(MediaType.APPLICATION_FORM_URLENCODED)
						.param(FORM_FIELD_DESCRIPTION, "description")
						.param(FORM_FIELD_TITLE, "title")
						.sessionAttr(UserController.MODEL_ATTRIBUTE,
								new UserDTO())).andExpect(status().isOk())
				.andExpect(view().name(expectedRedirectViewPath));
	}

	@Test
	@ExpectedDatabase(value = "userData.xml", assertionMode = DatabaseAssertionMode.NON_STRICT)
	public void findAll() throws Exception {
		mockMvc.perform(get("/user/list"))
				.andExpect(status().isOk())
				.andExpect(view().name(UserController.VIEW_LIST))
				.andExpect(forwardedUrl("/WEB-INF/jsp/user/list.jsp"))
				.andExpect(model().attribute("users", hasSize(1)))
				.andExpect(
						model().attribute(
								"users",
								hasItem(allOf(
										hasProperty("id", is(1L)),
										hasProperty("description",
												is("description")),
										hasProperty("title", is("title"))))));
	}

}
