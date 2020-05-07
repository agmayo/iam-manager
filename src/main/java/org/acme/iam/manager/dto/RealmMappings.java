package org.acme.iam.manager.dto;

public class RealmMappings {
    private String id;
    private String name;
    private boolean composite;
    private boolean clientRole;
    private String containerId;

    public RealmMappings(String id, String name, boolean composite, boolean clientRole, String containerId) {
        this.id = id;
        this.name = name;
        this.composite = composite;
        this.clientRole = clientRole;
        this.containerId = containerId;
    }

    public RealmMappings() {
        super();
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public boolean isComposite() {
        return composite;
    }

    public void setComposite(boolean composite) {
        this.composite = composite;
    }

    public boolean isClientRole() {
        return clientRole;
    }

    public void setClientRole(boolean clientRole) {
        this.clientRole = clientRole;
    }

    public String getContainerId() {
        return containerId;
    }

    public void setContainerId(String containerId) {
        this.containerId = containerId;
    }
    
    
    

}