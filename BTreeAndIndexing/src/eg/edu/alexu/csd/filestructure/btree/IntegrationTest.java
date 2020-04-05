package eg.edu.alexu.csd.filestructure.btree;

import java.util.List;

import org.junit.Assert;
import org.junit.Test;

public class IntegrationTest {

	
    private final Class<?> btreeInterfaceToTest = IBTree.class;
    private final Class<?> searchEngineInterfaceToTest = ISearchEngine.class;
	
    @Test
    public void testCreationRedBlackTree() {	
    	List<Class<?>> candidateClasses = ReflectionHelper.findClassesImplementing(btreeInterfaceToTest, btreeInterfaceToTest.getPackage());
    	Assert.assertNotNull("Failed to create instance using interface '" + btreeInterfaceToTest.getName() + "' !", candidateClasses);
    	Assert.assertEquals("You have more than one public implementation of the interface", 1, candidateClasses.size());
    }
    
    @Test
    public void testCreationTreeMap() {	
    	List<Class<?>> candidateClasses = ReflectionHelper.findClassesImplementing(searchEngineInterfaceToTest, searchEngineInterfaceToTest.getPackage());
    	Assert.assertNotNull("Failed to create instance using interface '" + searchEngineInterfaceToTest.getName() + "' !", candidateClasses);
    	Assert.assertEquals("You have more than one public implementation of the interface", 1, candidateClasses.size());
    }

}
