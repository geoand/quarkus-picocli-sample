package gr.jhug.picocli;

import com.github.dockerjava.api.DockerClient;
import com.github.dockerjava.api.model.Version;
import com.github.dockerjava.api.model.VersionComponent;
import com.github.dockerjava.api.model.VersionPlatform;
import io.quarkus.runtime.annotations.RegisterForReflection;
import picocli.CommandLine;

@CommandLine.Command(name = "version")
@RegisterForReflection(targets = {
        Version.class,
        VersionPlatform.class,
        VersionComponent.class
})
public class VersionCommand implements Runnable {

    private final DockerClient dockerClient;

    public VersionCommand(DockerClient dockerClient) {
        this.dockerClient = dockerClient;
    }

    @Override
    public void run() {
        System.out.println("API version: " + dockerClient.versionCmd().exec().getApiVersion());
    }
}
