import org.junit.Test;
import org.junit.internal.TextListener;
import org.junit.runner.JUnitCore;
import org.junit.runner.RunWith;
import org.junit.runners.*;

import static org.junit.Assert.assertEquals;

class JunitTester {

    @Test
    void testBaby(){
        assertEquals(20,20);
    }
}
