package com.dynamo.cr.go.core.test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

import org.junit.Test;

import com.dynamo.cr.go.core.CollectionInstanceNode;
import com.dynamo.cr.go.core.CollectionNode;
import com.dynamo.cr.go.core.ComponentTypeNode;
import com.dynamo.cr.go.core.GameObjectInstanceNode;
import com.dynamo.cr.go.core.GameObjectNode;
import com.dynamo.cr.go.core.GoPropertyUtil;
import com.dynamo.cr.go.core.RefComponentNode;

public class GoPropertyUtilTest {

    @SuppressWarnings("serial")
    private class DummyTypeNode extends ComponentTypeNode {}

    /**
     * Hierarchy:
     * - sub_coll
     *   - go1
     *     - script1
     * - go2
     *   - script2
     *   - script3
     *   - go3
     *     - script4
     * Relative go2#script2, the returned URLs should be:
     * - sub_coll/go1
     * - sub_coll/go1#script1
     * - go2
     * - #script3
     * - go3
     * - go3#script4
     */
    @Test
    public void testExtractRelativePaths() {
        // Setup
        RefComponentNode script1 = new RefComponentNode(new DummyTypeNode());
        script1.setId("script1");
        GameObjectNode go1 = new GameObjectNode();
        go1.addChild(script1);
        GameObjectInstanceNode goi1 = new GameObjectInstanceNode(go1);
        goi1.setId("go1");

        CollectionNode subColl1 = new CollectionNode();
        subColl1.addChild(goi1);
        CollectionInstanceNode subCollI1 = new CollectionInstanceNode(subColl1);
        subCollI1.setId("sub_coll");

        RefComponentNode script2 = new RefComponentNode(new DummyTypeNode());
        script2.setId("script2");
        RefComponentNode script3 = new RefComponentNode(new DummyTypeNode());
        script3.setId("script3");
        GameObjectNode go2 = new GameObjectNode();
        go2.addChild(script2);
        go2.addChild(script3);
        GameObjectInstanceNode goi2 = new GameObjectInstanceNode(go2);
        goi2.setId("go2");

        RefComponentNode script4 = new RefComponentNode(new DummyTypeNode());
        script4.setId("script4");
        GameObjectNode go3 = new GameObjectNode();
        go3.addChild(script4);
        GameObjectInstanceNode goi3 = new GameObjectInstanceNode(go3);
        goi3.setId("go3");
        goi2.addChild(goi3);

        CollectionNode collection = new CollectionNode();
        collection.addChild(subCollI1);
        collection.addChild(goi2);

        // Tests
        String[] urls = GoPropertyUtil.extractRelativeURLs(script2);
        Set<String> urlSet = new HashSet<String>();
        urlSet.addAll(Arrays.asList(urls));
        assertEquals(6, urls.length);
        assertTrue(urlSet.contains("sub_coll/go1"));
        assertTrue(urlSet.contains("sub_coll/go1#script1"));
        assertTrue(urlSet.contains("go2"));
        assertTrue(urlSet.contains("#script3"));
        assertTrue(urlSet.contains("go3"));
        assertTrue(urlSet.contains("go3#script4"));
    }
}
