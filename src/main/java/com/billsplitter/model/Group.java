package com.billsplitter.model;

import java.util.List;

public class Group {
    private int groupId;
    private String groupName;
    private int createdBy;
    private List<User> members;

    public Group(int groupId, String groupName, int createdBy, List<User> members) {
        this.groupId = groupId;
        this.groupName = groupName;
        this.createdBy = createdBy;
        this.members = members;
    }

    public int getGroupId() {
        return groupId;
    }

    public String getGroupName() {
        return groupName;
    }

    public int getCreatedBy() {
        return createdBy;
    }

    public List<User> getMembers() {
        return members;
    }
}