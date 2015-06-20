package ro.sapientia2015.story.controller;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.MutablePropertyValues;
import org.springframework.context.MessageSource;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.validation.BindingResult;
import org.springframework.validation.Validator;
import org.springframework.validation.support.BindingAwareModelMap;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import org.springframework.web.servlet.mvc.support.RedirectAttributesModelMap;

import ro.sapientia2015.story.StoryTestUtil;
import ro.sapientia2015.story.config.UnitTestContext;
import ro.sapientia2015.story.controller.StoryController;
import ro.sapientia2015.story.dto.SprintDTO;
import ro.sapientia2015.story.dto.StoryDTO;
import ro.sapientia2015.story.dto.UserDTO;
import ro.sapientia2015.story.exception.NotFoundException;
import ro.sapientia2015.story.model.Sprint;
import ro.sapientia2015.story.model.Story;
import ro.sapientia2015.story.model.User;
import ro.sapientia2015.story.service.SprintService;
import ro.sapientia2015.story.service.StoryService;
import ro.sapientia2015.story.service.UserService;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import static junit.framework.Assert.assertNotNull;
import static junit.framework.Assert.assertNull;
import static junit.framework.Assert.assertTrue;
import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.*;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = {UnitTestContext.class})
public class UserControllerTest {

    private UserController controller;

    private UserService serviceMock;

    @Resource
    private Validator validator;
    
    @Before
    public void setUp() {
        controller = new UserController();

        serviceMock = mock(UserService.class);
        ReflectionTestUtils.setField(controller, "service", serviceMock);
    }

    @Test
    public void userList1() {
        BindingAwareModelMap model = new BindingAwareModelMap();

        String view = controller.listUsers(model);

        assertEquals("user/list", view);
     }

    @Test
    public void userList2() {
        BindingAwareModelMap model = new BindingAwareModelMap();

        controller.listUsers(model);

        List<User> sprints =  (List<User>)model.asMap().get("users");
        assertNotNull(sprints);
     }

    @Test
    public void userList3() {
        BindingAwareModelMap model = new BindingAwareModelMap();

        controller.listUsers(model);

        verify(serviceMock, times(1)).findAll();
        verifyNoMoreInteractions(serviceMock);
     }
    
    @Test
    public void userList4() {
        BindingAwareModelMap model = new BindingAwareModelMap();       
        List<User> list = new ArrayList<User>();
        list.add(new User());
        when(serviceMock.findAll()).thenReturn(list);

        controller.listUsers(model);
        
        List<User> sprints =  (List<User>)model.asMap().get("users");

        assertEquals(1, sprints.size());
     }
    private BindingResult bindAndValidate(HttpServletRequest request, Object formObject) {
        WebDataBinder binder = new WebDataBinder(formObject);
        binder.setValidator(validator);
        binder.bind(new MutablePropertyValues(request.getParameterMap()));
        binder.getValidator().validate(binder.getTarget(), binder.getBindingResult());
        return binder.getBindingResult();
    }

    @Test
    public void add() {
        UserDTO formObject = new UserDTO();

        formObject.setTitle("title");
        formObject.setDescription("desc");
        
        User model = User.getBuilder("title")
        		.description("desc").build();
        
        when(serviceMock.add(formObject)).thenReturn(model);

        MockHttpServletRequest mockRequest = new MockHttpServletRequest("POST", "/user/add");
        BindingResult result = bindAndValidate(mockRequest, formObject);

        RedirectAttributesModelMap attributes = new RedirectAttributesModelMap();

        String view = controller.add(formObject, result, attributes);

        verify(serviceMock, times(1)).add(formObject);
        verifyNoMoreInteractions(serviceMock);

        String expectedView = "redirect:/user/list";
        assertEquals(expectedView, view);
    }

    
    @Test
    public void addEmptyUser1() {
    	
        UserDTO formObject = new UserDTO();

        formObject.setTitle("");
        formObject.setDescription("");
       
        MockHttpServletRequest mockRequest = new MockHttpServletRequest("POST", "/user/add");
        BindingResult result = bindAndValidate(mockRequest, formObject);

        RedirectAttributesModelMap attributes = new RedirectAttributesModelMap();

        String view = controller.add(formObject, result, attributes);

        assertEquals(UserController.VIEW_ADD, view);
    }
    
    @Test
    public void addTooLongUserTitle() {
    	
        UserDTO formObject = new UserDTO();

        formObject.setTitle("TooLongTitleeeeeeeeeeeeee");
        formObject.setDescription("");
       
        MockHttpServletRequest mockRequest = new MockHttpServletRequest("POST", "/user/add");
        BindingResult result = bindAndValidate(mockRequest, formObject);

        RedirectAttributesModelMap attributes = new RedirectAttributesModelMap();

        String view = controller.add(formObject, result, attributes);

        assertEquals(UserController.VIEW_ADD, view);
    }
    
    
    @Test
    public void addTooLongUserDescription() {
    	
        UserDTO formObject = new UserDTO();

        formObject.setTitle("");
        formObject.setDescription("TooLongTitleeeeeeeeeeeeee");
       
        MockHttpServletRequest mockRequest = new MockHttpServletRequest("POST", "/user/add");
        BindingResult result = bindAndValidate(mockRequest, formObject);

        RedirectAttributesModelMap attributes = new RedirectAttributesModelMap();

        String view = controller.add(formObject, result, attributes);

        assertEquals(UserController.VIEW_ADD, view);
    }
    
}
