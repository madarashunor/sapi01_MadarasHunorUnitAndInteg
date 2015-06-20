package ro.sapientia2015.story.model;

import org.junit.Test;

import static junit.framework.Assert.*;

public class UserTest {

	@Test
	public void testTitle(){
		User user = User.getBuilder("Hello").build();
		assertNotNull(user.getTitle());
	}
	
	@Test
    public void buildWithMandatoryInformation() {
        User built = User.getBuilder("Hello").build();

        assertNull(built.getId());
        assertNull(built.getCreationTime());
        assertNull(built.getDescription());
        assertNull(built.getModificationTime());
        assertEquals("Hello", built.getTitle());
        assertEquals(0L, built.getVersion());
    }
	
	@Test
    public void buildWithAllInformation() {
        User built = User.getBuilder("Hello")
                .description("Hello desc")
                .build();

        assertNull(built.getId());
        assertNull(built.getCreationTime());
        assertEquals("Hello desc", built.getDescription());
        assertNull(built.getModificationTime());
        assertEquals("Hello", built.getTitle());
        assertEquals(0L, built.getVersion());
    }
	

	@Test
    public void prePersist() {
        User user = new User();
        user.prePersist();

        assertNull(user.getId());
        assertNotNull(user.getCreationTime());
        assertNull(user.getDescription());
        assertNotNull(user.getModificationTime());
        assertNull(user.getTitle());
        assertEquals(0L, user.getVersion());
        assertEquals(user.getCreationTime(), user.getModificationTime());
    }
	
	@Test
    public void preUpdate() {
        User user = new User();
        user.prePersist();

        pause(1000);

        user.preUpdate();

        assertNull(user.getId());
        assertNotNull(user.getCreationTime());
        assertNull(user.getDescription());
        assertNotNull(user.getModificationTime());
        assertNull(user.getTitle());
        assertEquals(0L, user.getVersion());
        assertTrue(user.getModificationTime().isAfter(user.getCreationTime()));
    }
	
	 private void pause(long timeInMillis) {
	        try {
	            Thread.currentThread().sleep(timeInMillis);
	        }
	        catch (InterruptedException e) {
	            //Do Nothing
	        }
	    }
	
}
