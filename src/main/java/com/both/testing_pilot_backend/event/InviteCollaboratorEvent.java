package com.both.testing_pilot_backend.event;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.context.ApplicationEvent;

import java.util.UUID;

@Getter
public class InviteCollaboratorEvent extends ApplicationEvent {
    private String collaboratorEmail;
    private UUID projectCollaboratorId; // The ID of the ProjectCollaborator link
    private String verificationToken; // Changed to String as it's the JWT string
    private String verificationLink; // The full verification URL
    private String projectName;
    private String invitingUserName;
    private String invitedUserName;

    public InviteCollaboratorEvent(Object source, String collaboratorEmail, UUID projectCollaboratorId,
                                   String verificationToken, String verificationLink, String projectName,
                                   String invitingUserName, String invitedUserName) {
        super(source);
        this.collaboratorEmail = collaboratorEmail;
        this.projectCollaboratorId = projectCollaboratorId;
        this.verificationToken = verificationToken;
        this.verificationLink = verificationLink;
        this.projectName = projectName;
        this.invitingUserName = invitingUserName;
        this.invitedUserName = invitedUserName;
    }
}
