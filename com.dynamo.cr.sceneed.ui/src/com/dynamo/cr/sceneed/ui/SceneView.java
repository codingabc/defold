package com.dynamo.cr.sceneed.ui;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

import org.eclipse.core.resources.IContainer;
import org.eclipse.core.resources.IResource;
import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.jface.viewers.ArrayContentProvider;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.swt.graphics.Image;
import org.eclipse.ui.dialogs.ListDialog;
import org.eclipse.ui.dialogs.ResourceListSelectionDialog;

import com.dynamo.cr.editor.core.EditorCorePlugin;
import com.dynamo.cr.editor.core.EditorUtil;
import com.dynamo.cr.editor.core.IResourceType;
import com.dynamo.cr.editor.core.IResourceTypeRegistry;
import com.dynamo.cr.properties.IFormPropertySheetPage;
import com.dynamo.cr.sceneed.core.IRenderView;
import com.dynamo.cr.sceneed.core.ISceneView;
import com.dynamo.cr.sceneed.core.Node;

public class SceneView implements ISceneView {

    @Inject private ISceneOutlinePage outline;
    @Inject private IFormPropertySheetPage propertySheetPage;
    @Inject private IRenderView renderView;
    @Inject private SceneEditor editor;
    @Inject private IContainer contentRoot;
    @Inject private SceneRenderViewProvider sceneRenderViewProvider;

    @Override
    public void setRoot(Node root) {
        this.outline.setInput(root);
        this.sceneRenderViewProvider.setRoot(root);
    }

    @Override
    public void updateNode(Node node) {
        this.outline.update(node);
        this.propertySheetPage.refresh();
        this.renderView.refresh();
    }

    @Override
    public void updateSelection(IStructuredSelection selection) {
        // Update all selection providers
        this.sceneRenderViewProvider.setSelection(selection);
        this.outline.setSelection(selection);
    }

    @Override
    public String selectComponentType() {
        IResourceTypeRegistry registry = EditorCorePlugin.getDefault().getResourceTypeRegistry();
        IResourceType[] resourceTypes = registry.getResourceTypes();
        List<IResourceType> embeddableTypes = new ArrayList<IResourceType>();
        for (IResourceType t : resourceTypes) {
            if (t.isEmbeddable()) {
                embeddableTypes.add(t);
            }
        }

        ListDialog dialog = new ListDialog(this.editor.getSite().getShell());
        dialog.setTitle("Add Component");
        dialog.setMessage("Select a component type:");
        dialog.setContentProvider(new ArrayContentProvider());
        dialog.setInput(embeddableTypes.toArray());
        dialog.setLabelProvider(new LabelProvider() {
            @Override
            public Image getImage(Object element) {
                IResourceType resourceType = (IResourceType) element;
                return Activator.getDefault().getImage(resourceType.getFileExtension());
            }

            @Override
            public String getText(Object element) {
                IResourceType resourceType = (IResourceType) element;
                return resourceType.getName();
            }
        });

        int ret = dialog.open();
        if (ret == Dialog.OK) {
            Object[] result = dialog.getResult();
            IResourceType resourceType = (IResourceType) result[0];
            return resourceType.getFileExtension();
        }
        return null;
    }

    @Override
    public String selectComponentFromFile() {
        ResourceListSelectionDialog dialog = new ResourceListSelectionDialog(this.editor.getSite().getShell(), this.contentRoot, IResource.FILE | IResource.DEPTH_INFINITE);
        dialog.setTitle("Add Component From File");

        int ret = dialog.open();
        if (ret == Dialog.OK) {
            IResource r = (IResource) dialog.getResult()[0];
            return EditorUtil.makeResourcePath(r);
        }
        return null;
    }

    @Override
    public void setDirty(boolean dirty) {
        this.editor.setDirty(dirty);
    }
}
