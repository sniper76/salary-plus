package ag.act.configuration;

import ag.act.service.aws.S3Service;
import com.google.auth.oauth2.GoogleCredentials;
import com.google.firebase.FirebaseApp;
import com.google.firebase.FirebaseOptions;
import com.google.firebase.messaging.FirebaseMessaging;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;

import java.io.IOException;
import java.io.InputStream;

@Configuration
@Profile({"default", "local", "dev", "prod"})
public class FirebaseMessagingConfig {

    @Bean("firebaseMessaging")
    public FirebaseMessaging firebaseMessaging(
        @Value("${firebase.service-account.file:firebase/service-account.json}") String serviceAccountFile,
        S3Service s3Service
    ) throws IOException {
        final InputStream inputStream = s3Service.readObject(serviceAccountFile);
        final GoogleCredentials googleCredentials = GoogleCredentials.fromStream(inputStream);
        final FirebaseOptions firebaseOptions = FirebaseOptions
            .builder()
            .setCredentials(googleCredentials)
            .build();

        final FirebaseApp app = FirebaseApp.initializeApp(firebaseOptions, "act-app");

        return FirebaseMessaging.getInstance(app);
    }

}
