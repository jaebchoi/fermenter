package org.technologybrewery.fermenter.mda.generator;

import org.apache.maven.execution.MavenSession;
import org.apache.maven.project.MavenProject;
import org.technologybrewery.fermenter.mda.notification.Notification;
import org.technologybrewery.fermenter.mda.notification.NotificationCollector;
import org.technologybrewery.fermenter.mda.notification.NotificationService;
import org.technologybrewery.fermenter.mda.notification.VelocityNotification;

import javax.inject.Inject;
import java.io.File;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class TestSuppressedNotificationGenerator extends TestGenerator {

    private List<String> suppressedMessages;

    private NotificationService notificationService;

    public TestSuppressedNotificationGenerator(List<String> messages, MavenSession session) {
        super(1, false);
        suppressedMessages = messages;
        notificationService = new NotificationService(session);
    }

    @Override
    public void generate(GenerationContext context) {
        MavenProject project = new MavenProject();
        project.setFile(new File("src/test/resources/plugin-testing-harness-pom-files/", "suppress-messages"));
        Notification notification = new VelocityNotification("test-message-id", new HashSet<>(), "templates/notifications/sample.notification.vm");
        NotificationCollector.addNotification("test-message-id", notification);
        notificationService.recordNotifications(project, suppressedMessages);
    }

}
