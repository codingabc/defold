package com.dynamo.cr.go.core;

import org.eclipse.core.runtime.IStatus;
import org.eclipse.swt.graphics.Image;

import com.dynamo.cr.properties.NotEmpty;
import com.dynamo.cr.properties.Property;
import com.dynamo.cr.sceneed.core.Node;
import com.dynamo.cr.sceneed.core.validators.Unique;

public class InstanceNode extends Node {

    @Property
    @NotEmpty(severity = IStatus.ERROR)
    @Unique
    private String id = "";

    public InstanceNode() {
        setFlags(Flags.TRANSFORMABLE);
    }

    public String getId() {
        return this.id;
    }

    public void setId(String id) {
        this.id = id;
    }

    @Override
    public Image getIcon() {
        if (hasChildren()) {
            return this.getChildren().get(0).getIcon();
        }
        return super.getIcon();
    }

}
