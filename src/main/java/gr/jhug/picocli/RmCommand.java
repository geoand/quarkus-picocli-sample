package gr.jhug.picocli;

import com.github.dockerjava.api.DockerClient;
import picocli.CommandLine;

@CommandLine.Command(name = "rm")
public class RmCommand implements Runnable {

    @CommandLine.Parameters(index = "0")
    String containerId;

    private final DockerClient dockerClient;

    public RmCommand(DockerClient dockerClient) {
        this.dockerClient = dockerClient;
    }

    @Override
    public void run() {
        dockerClient.removeContainerCmd(containerId).exec();
    }
}
